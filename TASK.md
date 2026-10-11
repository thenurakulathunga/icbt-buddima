# TASK.md – PrintXpress Development Tasks

Status legend: `[ ]` todo · `[~]` in progress · `[x]` done. Priority: **M**ust / **S**hould / **C**ould.
Reference docs in brackets. Update this file whenever a task changes state.

> **Current phase: Development in progress.** Phases 3–8 committed. Open items: T-024 product photos, T-092 remaining Espresso
> flows, T-093 full manual TC run with evidence, T-095 accessibility/rotation/API 24 checks, T-103 … T-105 report
> and submission.
> Each phase is committed to git locally as `Phase N: …`. Dev notes/decisions are logged at the end of this file.
> Theme (Figma *Mobile Apps – Prototyping Kit*), font (Roboto Flex), icon library (Hugeicons Free
> Stroke Rounded) and the corner-radius scale are now **fixed** in docs/05. Images are still to be collected (T-024).

---

## Phase 0 – Analysis (Task A)
- [x] T-001 Analyse the assessment brief and extract requirements [docs/01]
- [ ] T-002 Research Sri Lankan mobile OS market share (StatCounter) (M)
- [ ] T-003 Write the Task A critical comparison, ~1000 words, Harvard references [docs/10] (M)

## Phase 1 – System and database design (Task B)
- [x] T-010 Draft the use case, class, activity, state and sequence diagrams [docs/04]
- [x] T-011 Draft the ERD, normalisation and DDL [docs/03]
- [ ] T-012 Render the diagrams (PlantUML/draw.io) and export PNGs for the report (M)
- [ ] T-013 Write the design-decision explanations under each diagram (M)
- [ ] T-014 Write the UNF → 1NF → 2NF → 3NF tables for the report (M)

## Phase 2 – UI/UX design (Task C)
- [x] T-020 Define the design system: colours, fonts, icons, spacing [docs/05]
- [x] T-021 Pull the user's Figma kit (*Mobile Apps – Prototyping Kit*, Components page), adopt Roboto Flex
      and Hugeicons, rewrite the tokens and component mapping [docs/05]
- [x] T-021a Corner-radius scale (4–32 dp ladder + pill) and `ShapeAppearance` rules [docs/05 §6.1]
- [x] T-021b Re-align the assets list with the new palette, font, icons and licences [docs/07]
- [ ] T-021c Read the kit's remaining values in Figma *Inspect* and confirm them in docs/05:
      `Highlight/Dark`, `Support/*/Dark` (M)
- [x] T-021d `colorPrimary` = `#0060DB` (5.7:1, passes WCAG AA). The kit's `#006FFD` is kept as
      `px_brand_bright` for decorative fills only [docs/05 §3.5]
- [ ] T-021e Confirm the Figma kit's community licence and credit line for the report [docs/07 §9] (M)
- [ ] T-022 Low-fi wireframes for all 34 screens (M)
- [ ] T-023 Hi-fi mockups in Figma using the tokens and the kit components (M)
- [ ] T-024 Collect/download the assets in docs/07 and log their licences (M)
- [ ] T-025 Create the launcher icon + logo (`#0060DB`, Roboto Flex ExtraBold wordmark) (M)

## Phase 3 – Foundation (Task D)
- [x] T-030 Create the Android Studio project `PrintXpress`, package `com.printxpress.app`, Java, minSdk 24, ViewBinding (M)
- [x] T-031 Add dependencies (`libs.versions.toml`) [docs/02 §1] (M)
- [x] T-032 Theme: `colors.xml` (light/night), `themes.xml`, `type.xml`, `dimens.xml`, `styles.xml`,
      `ShapeAppearance.PrintXpress.*`, Roboto Flex static instances (400–800) in `res/font` [docs/05 §3–§6] (M)
- [x] T-032a Generate the Roboto Flex static TTFs with fontTools and verify each file is static [docs/05 §4] (M)
- [x] T-032b Import the Hugeicons SVGs as vector drawables with semantic `ic_*` names [docs/05 §5] (M)
- [x] T-033 Core: `PrintXpressApp`, `AppContainer`, `ViewModelFactory`, `AppExecutors`, `Resource`, `Event`, `Money` (M)
- [x] T-034 Base classes: `BaseActivity`, `BaseFragment`, `BaseViewModel`, `BaseBottomSheet`, `BaseListAdapter` (M)
- [x] T-035 `DbContract`, `DatabaseHelper` (singleton, FK on), `DatabaseSeeder` [docs/03] (M)
- [x] T-036 Models + enums for all 21 tables (M)
- [x] T-037 `BaseDao<T>` + all DAOs with parameterised queries (M)
- [x] T-038 Repositories (11) returning `LiveData<Resource<T>>` (M)
- [x] T-039 Validation framework: `ValidationRule`, `FormValidator`, 15 rules [docs/01 §8] (M)
- [x] T-040 Domain: pricing strategies, `PriceCalculator`, `OrderState` machine, `FulfilmentStrategy`, `ScheduleCalculator` (M)
- [x] T-041 Utilities: `PasswordHasher`, `SessionManager`, `FileStorageManager`, `CurrencyFormatter`, `DateTimeUtils`, `OrderNumberGenerator`, `IntentUtils`, `DrawableResolver` (M)
- [x] T-042 Components library, inputs: `PxTextInput`, `PxDropdown`, `LoadingButton`, `QuantityStepper`, `OptionSelectorGroup`, `SegmentedToggle`, `FileUploadView`, `DateSlotPicker`, `SelectableOptionCard`, `PasswordStrengthMeter`, `SearchBarView` [docs/06 B] (M)
- [x] T-043 Components library, display: `AppTopBar`, `SectionHeader`, `CategoryCard`, `ProductCard`, `PromoCard`/`PromoCarousel`, `PriceSummaryView`, `CartItemCard`, `OrderCard`, `StatusBadge`, `OrderTimelineView`, `AddressCard`, `NotificationItemView`, `ExpandableFaqItem`, `GuidelineCard`, `GalleryTile`, `DesignThumbCard`, `ProfileHeaderView`, `MenuRowView`, `StickyBottomBar`, `PageIndicator`, `InfoBanner` [docs/06 C] (M)
- [x] T-044 State/feedback: `StateLayout`, `EmptyStateView`, `ErrorStateView`, skeletons, `AppDialog`, `SnackbarHelper`, `LoadingOverlay` [docs/06 D] (M)
- [x] T-045 Navigation graphs `nav_auth`, `nav_main`, bottom navigation (M)

## Phase 4 – Features sprint 1
- [x] T-050 Splash (SplashScreen API) + onboarding (first run) (M)
- [x] T-051 Register with full validation + uniqueness (FR-1.1) (M)
- [x] T-052 Login email/phone + session (FR-1.2, 1.3) (M)
- [x] T-053 Reset password (FR-1.9) (C)
- [x] T-054 Home screen (promos, categories, popular products, active order) (M)
- [x] T-055 Catalog + product list with search/filter/sort (FR-2.1–2.3) (M)
- [x] T-056 Product detail (FR-2.4) (M)
- [x] T-057 Profile, edit profile, change password (FR-1.4, 1.5) (M)
- [x] T-058 Addresses CRUD + default + district dropdown (FR-1.6) (M)

## Phase 5 – Features sprint 2
- [x] T-060 Configure product: options, quantity stepper, live price (FR-2.5) (M)
- [x] T-061 Design input: SAF upload with type/size checks, custom text, save design (FR-2.6, 2.7) (M)
- [x] T-062 Cart (FR-2.8) (M)
- [x] T-063 Checkout: fulfilment, address/branch, date + slot (FR-2.9, 2.10) (M)
- [x] T-064 Review: promo code, payment method, price breakdown (FR-2.11, 2.12) (M)
- [x] T-065 Place order transaction + success screen (FR-2.13) (M)
- [x] T-066 Saved designs screen (FR-1.7) (M)

## Phase 6 – Features sprint 3
- [x] T-070 My orders tabs + order detail + timeline (FR-3.1, 3.2, 3.6) (M)
- [x] T-071 Reschedule sheet (FR-3.3) (M)
- [x] T-072 Cancel sheet with reason (FR-3.4) (M)
- [x] T-073 Reorder (FR-3.5) (C)
- [x] T-074 `NotificationHelper`, channels, Android 13 permission flow (M)
- [x] T-075 `OrderProgressWorker` (status simulation + notifications) (FR-4.1, 4.2) (M)
- [x] T-076 `PromotionWorker` seasonal promos (FR-4.3) (M)
- [x] T-077 Notification centre + deep links + badge (FR-4.4) (M)
- [x] T-078 Offers list + detail + copy code (FR-4.5) (M)
- [x] T-079 Settings: notification prefs, theme, privacy, open-source licences (Hugeicons MIT, Roboto Flex
      OFL, asset credits), delete account (FR-4.6, 1.10) (S)
- [x] T-080 Gallery + full-screen viewer (FR-5.2) (M)
- [x] T-081 Guidelines list/detail (FR-5.1) (M)
- [x] T-082 FAQs with search (FR-5.3) (M)
- [x] T-083 Contact support + tickets (FR-5.4) (M)
- [x] T-084 Bleed template share (FR-5.5) (C)

## Phase 7 – Testing and polish (Task E)
- [x] T-090 Unit tests: validation rules, pricing, order state, schedule, hasher [docs/09 §5] (M)
- [x] T-091 Instrumented DAO tests (M)
- [~] T-092 Espresso flows: login, register validation, place order, cancel (S) – login + register validation
      automated; place order and cancel verified manually on the emulator (see log)
- [ ] T-093 Run manual test cases TC-01 … TC-68, capture evidence (M)
- [ ] T-094 Fix defects and log them (M)
- [ ] T-095 Accessibility Scanner, dark mode, rotation, API 24 smoke test (M)
- [x] T-096 Remove unused resources, run lint and fix warnings (S)

## Phase 8 – Documentation and submission (Task F)
- [x] T-100 Generate Javadoc HTML (M) – `./gradlew :app:javadocDebug` → `app/build/docs/javadoc/`
- [x] T-101 User guide with screenshots [docs/11 A] (M) – `docs/13_USER_GUIDE.md`, `docs/screenshots/`
- [x] T-102 Technical documentation [docs/11 B] (M) – `docs/12_TECHNICAL_DOCUMENTATION.md`
- [ ] T-103 Assemble the report: intro, Tasks A–F, conclusion, references, appendices (M)
- [ ] T-104 Check the word count (3000) and Harvard references (M)
- [ ] T-105 Export the PDF `stXXXXXXXXCSE5011WRIT1.pdf` and submit to Turnitin ≥ 24 h early (M)

---

## Development notes and decisions (log)

- **Phase 3:** compileSdk/targetSdk 36 kept as documented; `androidx.core` pinned to 1.17.0 and Glide to 4.16.0
  because core 1.19 / Glide 5 require compileSdk 37. Core-library desugaring enabled so `java.time` works on API 24.
- Roboto Flex: Google Fonts only serves a static 400, so all five weights were instanced from the upstream variable
  font with fontTools `varLib.instancer` (every axis pinned), subset to Latin, and verified static (no `fvar`/`gvar`).
- Hugeicons: 99 icons generated from `@hugeicons/core-free-icons@4.3.5` by `tools/hugeicons_to_vector.mjs`.
- Pricing data model: `products.base_price` is the price of **one unit** (Standard Business Card Rs. 15.00 per card,
  i.e. Rs. 1,500 / 100) and `product_options.price_modifier` is a **percentage** (Double sided = +40). BR-4 bulk
  discount applies per line. Seeded promo `DEEPAVALI26` added so a seasonal code is live during the demo period.
- Colour token `px_success_text` (Derived: `#1D6B53` light / `#3AC0A0` dark) added for discount text on surfaces.
- **Phase 4:** verified on the Pixel 7 emulator (API 36): onboarding → register (validation matrix + TC-01) → Home,
  catalogue → product list → filter sheet → product detail (price guide shows BR-4 tiers), profile, address form
  validation. Guests can browse; customer-only actions go through `AuthGate` (sign in, then continue).
- **Phase 5:** configure + design input share `ConfigureViewModel` (scoped to `nav_configure`), checkout + review
  share `CheckoutViewModel` (scoped to `nav_checkout`). Every uploaded file is stored as a Saved design. The
  repository re-validates the whole checkout and recalculates prices before the single-transaction insert.
  Emulator run: TC-26 (Rs. 9,450.00), TC-37 (Sunday closed, earliest date pre-selected), TC-38/39 (WELCOME10,
  NOTREAL1), TC-40 (order `PX-260922-0001`, Rs. 8,505.00).
- **Phase 6:** order events flow `OrderRepository` → `OrderEventsHandler` (in-app notification + system notification
  + next `OrderProgressWorker` step; 20 s in demo mode, 10 min otherwise). Notification taps go through
  `SplashActivity` → `MainActivity` (`singleTop`) and `ShellRoutes`. Promotion announcements are claimed atomically
  per user in `SessionManager`; this fixed duplicates when the one-off and periodic jobs overlapped. The bleed template
  is drawn to scale by `BleedTemplateView` and shared as a PNG through the FileProvider (`cache/shared/`).
  `ZoomLayout` gives the gallery viewer pinch, pan and double-tap zoom. Emulator run: permission rationale → allow,
  notification centre → offer deep link, copy code (TC-56), bleed guideline + share sheet (TC-57), FAQ search (TC-58),
  ticket validation (TC-60) and send, gallery filter + viewer, settings (prefs saved, dark theme, licences),
  reschedule, cancel without reason → `err_reason_required` (TC-47), cancel with reason + system notification
  (TC-48), reorder, and a new pickup order `PX-260922-0002` advancing Confirmed → Printing → Ready for pickup →
  Completed in demo mode (TC-44, TC-50). docs/09 TC-60 now expects `err_ticket_subject` for a short subject.
- **Phase 7 (in progress):** `./gradlew :app:testDebugUnitTest` – 37 JVM tests pass (validation rules and
  `FormValidator`, pricing incl. TC-26/TC-38/TC-39 figures, BR-4 tiers, fixed-promo cap, order-state paths and
  BR-7, schedule/BR-6, Money, LKR formatting, PBKDF2, order numbers). `./gradlew :app:connectedDebugAndroidTest`
  – 9 tests pass on the Pixel 7 AVD (API 36): 6 DAO tests on a seeded in-memory database (UNIQUE constraints,
  injection-shaped identifier returns nothing, single-transaction order insert + history + cart clear, cancel →
  Past, tickets) and 3 Espresso auth checks. Lint: 0 errors; warnings 127 → 38 after removing 23 dead strings and
  2 dead plurals, turning 5 count strings into plurals, adding input hints, `ZoomLayout.performClick` and ©.
  `app/lint.xml` keeps the docs/05 token files and the generated icon set out of `UnusedResources`. The remaining
  warnings are accepted: `UnusedAttribute` (API 28+ attributes ignored on older devices), dependency/target-SDK
  notices (versions pinned for compileSdk 36), `ApplySharedPref` (the promo claim needs a synchronous `commit`),
  `NotifyDataSetChanged` in three small adapters, and layout-only `Overdraw`/`RtlSymmetry` hints.
  Still to do: T-093 (full manual TC run with evidence), T-095 (Accessibility Scanner, rotation, API 24 image).
- **Phase 8:** `javadocDebug` Gradle task (debug compile classpath + compiled classes so R/BuildConfig/ViewBinding
  resolve) builds 417 pages with no doclint errors. Rule 1 audit: 53 classes relied on implicit default
  constructors; all now declare a documented constructor (placed after the fields). The only remaining
  `-Xdoclint:all` notes are tag-only getter comments. The user guide uses 27 fresh light-theme screenshots from a
  clean install (register → order `PX-260922-0001` with WELCOME10 → tracking → support → settings). docs/03 now
  records the per-unit/percentage pricing convention and `DEEPAVALI26`; docs/05 lists `px_success_text`.
  T-103 … T-105 (report, word count, Harvard references, PDF, Turnitin) are the student's own write-up and
  submission and are left open.
- Product/category photos are still pending (T-024): `ArtworkImageView` shows the category Hugeicon on the primary
  container until a `img_*` drawable with the seeded name is added – no code change needed.
