# 12 – PrintXpress Technical Documentation (Task F – Part B)

As-built reference for version 1.0 (commit history `Phase 3` … `Phase 8`). Design rationale lives in
docs/01–docs/06; this document records what was implemented and how to build, run and test it.

---

## 1. Overview and scope

PrintXpress is a native Android (Java) customer app for a Sri Lankan digital printing shop. It covers:

- Accounts, with registration by email or phone and PBKDF2 passwords.
- A product catalogue with search and filters.
- Product configuration with live LKR pricing.
- Artwork upload or custom text.
- A cart and checkout with pickup or home delivery, a date and slot, and promo codes.
- Order tracking, including reschedule, cancel and reorder.
- Notifications and offers.
- Design-support content (guidelines, FAQs, gallery, contact and tickets).
- Settings.

Documented assumptions (docs/01 §6) that shape the implementation:

| Assumption | Consequence in code |
|------------|---------------------|
| No backend | All data in SQLite on the device (`DatabaseHelper`, 21 tables, seeded on first run). |
| Offline payment only | `PaymentMethod` = `PAY_AT_PICKUP` / `CASH_ON_DELIVERY`, chosen by `FulfilmentStrategy`. |
| Shop actions simulated | `OrderProgressWorker` advances each order one status per step (20 s demo / 10 min). |
| Artwork stays on device | Files copied via the Storage Access Framework into `files/designs/`; no storage permission. |

## 2. Development environment

| Item | Version |
|------|---------|
| IDE | Android Studio (latest stable), bundled JBR (OpenJDK 25 runtime; Java 17 source/target) |
| Build | Gradle 9.5.0 wrapper, Android Gradle Plugin 9.3.3, Kotlin DSL, version catalogue `gradle/libs.versions.toml` |
| SDK | compileSdk 36, targetSdk 36, minSdk 24; core-library desugaring (`desugar_jdk_libs` 2.1.5) for `java.time` |
| UI | AppCompat 1.8.0, Material Components 1.14.0, ConstraintLayout 2.2.2, RecyclerView 1.4.0, ViewPager2 1.1.0, SwipeRefreshLayout 1.2.0, Shimmer 0.5.0 |
| Architecture | Lifecycle (ViewModel, LiveData) 2.11.0, Navigation 2.10.1, Fragment 1.9.0, Activity 1.13.0 |
| Background | WorkManager 2.11.2 |
| Images | Glide 4.16.0 |
| Tests | JUnit 4.13.2, Mockito 5.23.0, arch core-testing 2.2.0, AndroidX Test 1.7.0 / ext-junit 1.3.0, Espresso 3.7.0 |
| Assets | Roboto Flex (OFL, static 400–800 instances), Hugeicons Free Stroke Rounded (MIT, 99 vector drawables) |

`androidx.core` is pinned to 1.17.0 and Glide to 4.16.0 because their newer releases require
compileSdk 37 (see the TASK.md log).

## 3. Build and run

```bash
git clone <repo> && cd Thenura            # or open the folder in Android Studio and let Gradle sync
./gradlew :app:assembleDebug              # APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:installDebug               # install on a running emulator/device
./gradlew :app:testDebugUnitTest          # JVM unit tests (37)
./gradlew :app:connectedDebugAndroidTest  # instrumented DAO + Espresso tests (9), needs a device
./gradlew :app:lintDebug                  # lint report: app/build/reports/lint-results-debug.html
./gradlew :app:javadocDebug               # API docs: app/build/docs/javadoc/index.html
```

**Signed release APK:**
1. Choose **Build › Generate Signed App Bundle / APK › APK**.
2. Create or choose a keystore.
3. Select the `release` variant.

The keystore is not committed. Release minification is off (`optimization.enable = false`), so no ProGuard/R8 rules are needed.

## 4. Architecture

MVVM + Repository in five layers (docs/02 §2). Each layer depends only on the one below, and the
`domain` package has no Android imports, so it is JVM-testable.

```
ui/            Activities, Fragments, bottom sheets, adapters, reusable components (48)
  ↓ observes LiveData<Resource<T>>, calls ViewModel methods
ui/**/ *ViewModel  BaseViewModel / BaseFormViewModel: validation, subscriptions, one-shot Events
  ↓ calls repository interfaces
domain/        PriceCalculator + PricingStrategy chain, OrderState machine, FulfilmentStrategy, ScheduleCalculator
data/          Repository interfaces + *Impl (BaseRepository.execute → AppExecutors.diskIO())
               DAOs (BaseDao<T>, parameterised SQL, DbContract names) → DatabaseHelper (SQLiteOpenHelper)
               SessionManager (SharedPreferences), FileStorageManager (app-private files)
core/, di/, worker/   base classes, validation, security, utilities, notifications; AppContainer + ViewModelFactory; WorkManager jobs
```

**Package tree** (`com.printxpress.app`, 294 Java files, 94 layouts):

```
core/{base, common, concurrency, notification, security, session, storage, util, validation[/rules]}
data/{db, dao, model[/enums], repository}
di/{AppContainer, ViewModelFactory, ViewModelRegistry}
domain/{fulfilment, order, pricing, schedule}
ui/{adapters, auth, cart, catalog, checkout, components, configure, gallery, home, main, notifications,
    offers, onboarding, orders, product, profile, settings, splash, support}
worker/{OrderProgressWorker, OrderProgressScheduler, OrderEventsHandler, PromotionWorker}
```

### How place order flows through the layers

1. `ReviewFragment` → `CheckoutViewModel.placeOrder()`: it re-validates the form, then calls `OrderRepository.placeOrder(CheckoutRequest)`.
2. `OrderRepositoryImpl` runs on `diskIO` and re-checks the business rules:
   - the cart is not empty and the products are active;
   - the promo is valid;
   - the date is valid for `ScheduleCalculator`;
   - a branch or address is set.

   It then re-prices through `PriceCalculator`.
3. `OrderDao.insertOrderWithItems()` writes the order, its items, their options and the first history row, and clears the cart, all in **one transaction**.
4. `OrderEventListener.onOrderPlaced` → `OrderEventsHandler`: in-app notification, system notification, and the first `OrderProgressWorker` step.
5. `Resource.success(order)` travels through LiveData, and the fragment navigates to `OrderSuccessFragment`.

### Navigation

- `SplashActivity` decides the start: onboarding, `AuthActivity` (`nav_auth`) or `MainActivity` (`nav_main`).
- `MainActivity` shows the five bottom-navigation tabs. It also hosts the nested graphs:
  - `nav_configure`, which scopes `ConfigureViewModel`;
  - `nav_checkout`, which scopes `CheckoutViewModel`.
- Cross-screen routes live in `ShellRoutes`, so fragments never build argument bundles themselves.
- Notification taps re-enter through `SplashActivity`, which forwards to `MainActivity` (`singleTop`, `onNewIntent`). `ShellRoutes` then opens the order or offer.

## 5. Design patterns

| Pattern | Classes | Why |
|---------|---------|-----|
| MVVM | every screen + `*ViewModel` | UI state survives rotation; logic is testable |
| Repository | `*Repository` + `*RepositoryImpl` | One API over DAOs, session and files; UI never sees SQL |
| DAO | `BaseDao<T>` + 17 DAOs | All SQL per table in one place |
| Singleton | `DatabaseHelper`, `SessionManager`, `AppExecutors`, `NotificationHelper` | One connection pool, one session |
| Factory | `ViewModelFactory`, `OrderStateFactory`, `PricingStrategyFactory` | Callers do not know concrete classes |
| Builder | `Order.Builder`, `CartItem.Builder`, `AppDialog.Builder` | Many optional fields |
| Strategy | `PricingStrategy`, `FulfilmentStrategy`, `ValidationRule` | Interchangeable pricing, pickup/delivery, rules |
| State | `OrderState` + 8 states (`EditableOrderState` for the first three) | BR-7 (cancel/reschedule before printing) without `switch` blocks |
| Observer | LiveData; `OrderEventListener` → `OrderEventsHandler` | UI and notifications react to data changes |
| Adapter | `BaseListAdapter<T, VB>` + 13 adapters (plus the carousel pager adapter) | Model lists → RecyclerView/ViewPager2 |
| Template Method | `BaseActivity`, `BaseFragment`, `BaseBottomSheet`, `BaseDao`, `BaseRepository`, `AbstractRule` | Fixed skeleton (`initViews → initObservers → initListeners`, `execute`, `validate`) |
| Composite | `CompositePricingStrategy`, `CompositeRule`, `FormValidator` | A group of rules/strategies behaves like one |
| Service Locator | `di/AppContainer` | Manual dependency wiring, created once in `PrintXpressApp` |

## 6. Database

SQLite through `SQLiteOpenHelper`, schema version 1, foreign keys on (`onConfigure`). The full ERD,
3NF justification and DDL are in docs/03. All table and column names are constants in `DbContract`.
Every query uses `?` placeholders.

| Group | Tables |
|-------|--------|
| Accounts | `users`, `addresses`, `districts` (with delivery fee), `branches` |
| Catalogue | `categories`, `products`, `option_types`, `product_options` |
| Ordering | `designs`, `cart_items`, `cart_item_options`, `promotions`, `orders`, `order_items`, `order_item_options`, `order_status_history` |
| Engagement | `notifications`, `gallery_items`, `guidelines`, `faqs`, `support_tickets` |

- **Integrity:**
  - `UNIQUE` on `users.email`, `users.phone` and `promotions.promo_code`.
  - `CHECK` constraints on quantities and statuses.
  - `ON DELETE CASCADE` from `users`, so deleting an account removes the user's rows.
- **Pricing data:**
  - `products.base_price` is the price of one unit.
  - `product_options.price_modifier` is a percentage.
  - Order lines store unit price, line total and a snapshot of each option's modifier, so later price changes never alter history.
- **Seed data** (`DatabaseSeeder`, first run only): 7 categories, their products and options, 25 districts, 3 branches, six promotions (`WELCOME10`, `BULK500`, `AVURUDU26`, `VESAK26`, `DEEPAVALI26`, `XMAS26`), guidelines, FAQs and gallery samples.
- **Migration strategy:** `onUpgrade` is the place for versioned `ALTER TABLE` steps. Version 1 has none; a future schema change bumps `DATABASE_VERSION` and adds a migration step instead of dropping data.

## 7. Key algorithms

**Pricing pipeline** (`PriceCalculator`, `PricingStrategyFactory`). It is a composite chain applied in order:

1. `BasePricing` — unit price × quantity.
2. `OptionModifierPricing` — adds the sum of the option percentages.
3. `BulkDiscountPricing` (BR-4) — 10% off from 500 units, 15% from 1000, per line.
4. `PromoCodePricing` — applies the promotion to the eligible amount only (lines in the promo's category), after checking the minimum order. A fixed discount is capped at the eligible amount.

The delivery fee comes from the chosen `FulfilmentStrategy`. Worked example (TC-26/TC-38):
500 double-sided cards = 15.00 × 500 × 1.40 = Rs. 10,500.00 − 10% bulk = **Rs. 9,450.00**,
then `WELCOME10` gives − Rs. 945.00 = **Rs. 8,505.00**.

**Order state machine** (`domain/order`):
- `OrderStateFactory.from(status)` returns a state object.
- `canCancel()` and `canReschedule()` are true only in `PendingState`, `ConfirmedState` and `ProcessingState` (via `EditableOrderState`).
- `next(fulfilment)` branches after `PRINTING`: to `READY_FOR_PICKUP` for pickup, or `OUT_FOR_DELIVERY` for delivery.
- The repository re-checks the state before cancelling or rescheduling.

**Schedule** (`ScheduleCalculator`):
- The earliest date is today + turnaround (+1 day for delivery, from `HomeDeliveryStrategy`).
- It skips Sundays and the fixed holidays: 4 Feb, 13–14 Apr, 1 May, 25 Dec.
- The picker shows a 14-day window; closed days are disabled.
- `isAvailable()` validates a submitted date.

**Password hashing** (`PasswordHasher`):
- PBKDF2WithHmacSHA256, 120,000 iterations, 256-bit key.
- A 16-byte random salt per user; salt and hash are stored as hex.
- Verification uses a constant-time comparison (`MessageDigest.isEqual`).
- The derived key and the `PBEKeySpec` password are wiped after use, and so is the sign-in password array.

**File storage** (`FileStorageManager`):
- `describe()` reads the name, MIME type and size of a picked URI.
- `FileTypeRule` and `FileSizeRule` check it: PDF/PNG/JPG, ≤ 25 MB.
- `copyToDesigns()` streams it into `files/designs/<uuid>.<ext>`; only that private path is saved.
- `writeShareImage()` writes the bleed template into `cache/shared/` and returns a `FileProvider` URI.

## 8. Validation framework

- `ValidationRule` (Strategy) has one method, `validate(String) → ValidationResult`.
- `AbstractRule` (Template Method) treats blank input as valid, so `RequiredRule` decides whether a field is required.
- 15 concrete rules:
  - text: `Required`, `RequiredIfEmpty`, `Length`, `Match`, `Regex`, `Email`, `SriLankanPhone`, `PasswordStrength`, `PostalCode`;
  - numbers: `Range`, `Step`;
  - dates: `MinDate`, `NotSunday`;
  - files: `FileType`, `FileSize`.
- `CompositeRule` and `FormValidator` (Composite) map field keys to rule lists.

Each form ViewModel extends `BaseFormViewModel`:
1. It validates a field on focus loss and all fields on submit.
2. It publishes `FieldErrors`.
3. `FormBinder` shows them on the matching `PxTextInput` and focuses the first invalid field.

Repositories repeat the business-rule checks and throw `RepositoryException(messageRes)`, e.g. ticket length, cancel only before printing, promo validity, and duplicate email or phone. The validation matrix is in docs/01 §8.

## 9. Background work and notifications

- **Channels** (`NotificationChannels`): `orders` (high importance) and `promotions` (default), created at start-up.
- **Permission:**
  - On Android 13+, `MainActivity` shows a rationale dialog once for signed-in users, then requests `POST_NOTIFICATIONS`.
  - `NotificationHelper.canPost()` guards every post.
  - A refusal only hides system notifications; the in-app centre always records them.
- **Order simulation:**
  1. `OrderRepositoryImpl` notifies `OrderEventListener`s on place, advance, cancel and reschedule.
  2. `OrderEventsHandler` writes the in-app notification and posts a system one if the user wants order updates.
  3. It then asks `OrderProgressScheduler` for the next step: unique work `order_progress_<id>`, REPLACE policy, 20 s or 10 min.
  4. `OrderProgressWorker` calls `advanceBlocking()`.
  5. Terminal states cancel the work; deleting the account cancels all of it.
- **Promotions:**
  - `PromotionWorker` runs daily as a periodic job, plus once at start-up.
  - It announces each active promotion once per user. `SessionManager.claimPromoAnnouncement()` is `synchronized` and commits synchronously, so overlapping runs cannot announce twice.
- **Deep links:**
  - `DeepLinkFactory` builds `PendingIntent`s to `SplashActivity` with `orderId`/`promoId` extras.
  - `MainActivity.handleDeepLink()` routes them and removes the extras so a rotation does not re-open the screen.

## 10. Security and privacy

- **Passwords:** PBKDF2 with a per-user salt (§7). Never logged; key material and the sign-in password array are wiped after use.
- **SQL:** all SQL uses `DbContract` names and `?` parameters. An injection-shaped identifier returns no user (instrumented test `userDao_lookupAndUniqueness`).
- **Storage:**
  - Data lives in app-private storage; `FileStorageManager.delete()` refuses paths outside it.
  - The only exported files are a template under `cache/shared/`, through a non-exported `FileProvider` with a temporary read grant.
- **Minimal permissions:** only `POST_NOTIFICATIONS`. Calling, email and WhatsApp use implicit intents (`ACTION_DIAL` needs no permission).
- **Logging:** repository errors are logged in debug builds only and never include personal data.
- **Ethics and EDGE points:**
  - transparent privacy notice in Settings;
  - consent to terms at registration;
  - deleting the account removes every row (FK cascade) and every file;
  - dark theme and accessibility labels / 48 dp touch targets throughout;
  - open-source licences credited in Settings.

## 11. Reusable components

48 classes in `ui/components` (catalogue and APIs in docs/06). Screens compose these components instead of copying layout blocks:

- **Inputs:** `PxTextInput`, `PxDropdown`, `QuantityStepper`, `SearchBarView`, `OptionSelectorGroup`, `SegmentedToggle`, `SelectableCardGroup`, `DateSlotPicker`, `FileUploadView`, `PasswordStrengthMeter`.
- **Display:** `AppTopBar`, `StateLayout` (loading, empty, error and content states), `InfoBanner`, `InfoTableView`, `PriceSummaryView`, `StatusBadge`, `OrderTimelineView`, `ArtworkImageView`, `BleedTemplateView`, `ZoomLayout`.
- **Cards and rows:** `ProductCard`, `CategoryCard`, `PromoCard`/`PromoCarousel`, `OrderCard`, `CartItemCard`, `AddressCard`, `DesignThumbCard`, `GalleryTile`, `GuidelineCard`, `ExpandableFaqItem`, `NotificationItemView`, `MenuRowView`.
- **Actions and feedback:** `LoadingButton`, `StickyBottomBar`, `AppDialog`, `LoadingOverlay`, `SnackbarHelper`.

## 12. Testing

| Level | Location | Count | Command |
|-------|----------|-------|---------|
| JVM unit | `app/src/test` – validation rules + `FormValidator`, pricing (TC-26/38/39, BR-4), order states (BR-7), schedule (BR-6), Money, `CurrencyFormatter`, `PasswordHasher`, `OrderNumberGenerator` | 37 | `./gradlew :app:testDebugUnitTest` |
| Instrumented DAO | `app/src/androidTest/.../DaoInstrumentedTest` – seeded in-memory DB: UNIQUE constraints, parameterised look-up, transactional order insert, history, cancel, cart, tickets | 6 | `./gradlew :app:connectedDebugAndroidTest` |
| UI (Espresso) | `LoginFlowTest` – required fields, invalid credentials, registration email/phone validation | 3 | same |
| Manual | docs/09 TC-01 … TC-68 on the Pixel 7 AVD (API 36); evidence and results in the TASK.md log | – | – |
| Static | Android lint: 0 errors; accepted warnings listed in the TASK.md log | – | `./gradlew :app:lintDebug` |

## 13. API documentation

`./gradlew :app:javadocDebug` runs the `javadocDebug` task registered in `app/build.gradle.kts`. It uses the debug
compile classpath plus the compiled classes, so generated R, BuildConfig and ViewBinding types resolve.

- Output: `app/build/docs/javadoc/index.html`, 417 pages, window title "PrintXpress 1.0 API", linked to the Java 17 SE API.
- Doclint runs with all groups except `missing` and reports no errors.

Every class, constructor and public/protected member has a Javadoc block (project rule 1). With
`-Xdoclint:all` the only remaining notes are tag-only getter comments such as `/** @return the title */`.

## 14. Known limitations and future work

- **No server:** data does not sync between devices, and there is no shop-side admin app. The next step is a REST backend plus an admin app to confirm orders, replacing `OrderProgressWorker`.
- **Payments:** offline only. Online payment (e.g. PayHere) would need a backend and PCI-DSS handling.
- **Verification:** email/phone ownership is not checked. SMS OTP (e.g. Notify.lk) at registration and password reset is planned.
- **Holidays:** Poya and other lunar holidays are not in `ScheduleCalculator`. They need a yearly calendar table.
- **Assets:** product and gallery photos are still placeholders (category icons). Adding `img_*` drawables with the seeded names needs no code change (docs/07).
- **Localisation and platforms:** English only. Sinhala and Tamil string resources, and an iOS version, are future work.
