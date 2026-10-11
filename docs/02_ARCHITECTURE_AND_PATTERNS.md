# 02 – Architecture, Design Patterns and Coding Standards

## 1. Technology decisions

| Concern | Choice | Why (use in the Task A/B justification) |
|---------|--------|-----------------------------------------|
| Language | **Java 17** | Required Javadoc-style documentation, strict OOP syntax, taught in the module, mature Android support |
| IDE | Android Studio | Official IDE with layout editor, profiler, emulator and Database Inspector (handy for DB evidence screenshots) |
| UI toolkit | XML + ViewBinding + Material 3 | Type-safe view access, a consistent design system, works with custom reusable views |
| Architecture | **MVVM + Repository** | Google-recommended. Keeps UI logic separate from data and survives configuration changes |
| Database | **SQLite (`SQLiteOpenHelper`)** | Required by the user and the brief. Offline, zero-config, relational (fits 3NF), shows raw SQL skill |
| Async | `ExecutorService` (`AppExecutors`) + LiveData | Keeps DB I/O off the main thread with no extra libraries |
| Navigation | Navigation Component (2 activities + fragments) | Back-stack handling, safe args, a visual nav graph for documentation |
| Background | WorkManager | Guaranteed execution for status simulation and promo notifications |
| Images | Glide | Memory-efficient loading and caching of local drawables and URIs |
| DI | Manual `AppContainer` (Service Locator) | No framework magic, easy to explain in the report, testable |

### Gradle dependencies (planned, `libs.versions.toml`)

```
androidx.appcompat:appcompat
com.google.android.material:material            (1.12+)
androidx.constraintlayout:constraintlayout
androidx.recyclerview:recyclerview
androidx.viewpager2:viewpager2
androidx.swiperefreshlayout:swiperefreshlayout
androidx.lifecycle:lifecycle-viewmodel / lifecycle-livedata
androidx.navigation:navigation-fragment / navigation-ui
androidx.work:work-runtime
androidx.core:core-splashscreen
com.github.bumptech.glide:glide
com.facebook.shimmer:shimmer                     (skeleton loading, optional)
test: junit:junit, org.mockito:mockito-core, androidx.arch.core:core-testing
androidTest: androidx.test.ext:junit, androidx.test.espresso:espresso-core, espresso-intents
```

---

## 2. Layered architecture

```
┌──────────────────────────────────────────────────────────────┐
│ UI layer        Activities / Fragments / Adapters / components│  observes LiveData
├──────────────────────────────────────────────────────────────┤
│ Presentation    ViewModels (one per screen), UiState, Event   │  validates input
├──────────────────────────────────────────────────────────────┤
│ Domain          Pricing strategies, OrderState machine,       │  pure Java, unit-tested
│                 Fulfilment strategies, Validation rules        │
├──────────────────────────────────────────────────────────────┤
│ Data            Repositories -> DAOs -> DatabaseHelper(SQLite) │  AppExecutors.diskIO()
│                 SessionManager (SharedPreferences), FileStorage│
└──────────────────────────────────────────────────────────────┘
```

Dependency rule: each layer only knows the layer directly below it. Domain classes have **no
Android imports**, so they are fully JVM-unit-testable.

### Data flow example (place order)

`CheckoutFragment` → `CheckoutViewModel.placeOrder()` → `FormValidator` → `PriceCalculator`
(`PricingStrategy` chain) → `OrderRepository.placeOrder(Order)` → `OrderDao.insertOrderWithItems()`
(single SQLite **transaction**: orders, order_items, order_item_options, order_status_history, clear cart)
→ `NotificationHelper.notifyOrderConfirmed()` → `OrderProgressWorker` enqueued → `Resource.success(orderId)`
→ LiveData → navigate to `OrderSuccessFragment`.

---

## 3. Design patterns (use these consistently)

**Primary architectural pattern: MVVM + Repository.** The GoF patterns below support it. Each one
maps to a concrete problem in PrintXpress. That mapping is the justification to give in Task B.

| # | Pattern | Type | Where | Problem it solves |
|---|---------|------|-------|-------------------|
| 1 | **MVVM** | Architectural | every screen | Separates UI from logic. State survives rotation |
| 2 | **Repository** | Architectural | `data/repository/*Repository` | One clean API over DAOs, files and session. The UI never sees SQL |
| 3 | **DAO** | Structural | `data/dao/*Dao` extends `BaseDao<T>` | Keeps all SQL per table in one place |
| 4 | **Singleton** | Creational | `DatabaseHelper`, `SessionManager`, `AppExecutors`, `NotificationHelper` | Exactly one DB connection pool and one session |
| 5 | **Factory** | Creational | `ViewModelFactory`, `OrderStateFactory`, `PricingStrategyFactory` | Creates objects without the caller knowing concrete classes |
| 6 | **Builder** | Creational | `Order.Builder`, `CartItem.Builder`, `AppDialog.Builder` | Objects with many optional fields (delivery vs pickup, promo…) |
| 7 | **Strategy** | Behavioural | `PricingStrategy`, `FulfilmentStrategy`, `ValidationRule` | Interchangeable algorithms: bulk/promo pricing, pickup vs delivery fee, rules |
| 8 | **State** | Behavioural | `OrderState` + `PendingState`, `PrintingState`… | Allowed actions (cancel/reschedule/next) depend on status. Removes big `if/switch` blocks |
| 9 | **Observer** | Behavioural | LiveData / `Observer` | UI updates automatically when data changes |
| 10 | **Adapter** | Structural | `BaseListAdapter<T, VB>` subclasses | Binds model lists to RecyclerView rows |
| 11 | **Template Method** | Behavioural | `BaseActivity`, `BaseFragment`, `BaseDao`, `BaseViewModel` | Fixed lifecycle skeleton (`initViews → initObservers → initListeners`); subclasses fill in the steps |
| 12 | **Composite** | Structural | `FormValidator` holds many `ValidationRule`s; `CompositePricingStrategy` | Treats one rule or a group of rules the same way |
| 13 | **Service Locator** | Architectural | `di/AppContainer` | Central, manual dependency wiring |
| 14 | **Chain of Responsibility** (light) | Behavioural | `CompositePricingStrategy` applies base → options → bulk → promo in order | Price adjustments that stack |

### 3.1 Key pattern sketches (reference only, not final code)

```java
/**
 * Strategy contract for calculating a price adjustment on an order line or cart.
 */
public interface PricingStrategy {
    /**
     * Applies this strategy to a running price.
     *
     * @param context the immutable pricing inputs (quantity, options, promo)
     * @param current the price calculated so far
     * @return the adjusted price
     */
    Money apply(PricingContext context, Money current);
}
```

```java
/**
 * State pattern contract; each order status decides which transitions and actions are legal.
 */
public interface OrderState {
    /** @return the status enum this state represents */
    OrderStatus getStatus();
    /** @return true when the customer may cancel in this state */
    boolean canCancel();
    /** @return true when the customer may reschedule in this state */
    boolean canReschedule();
    /**
     * Resolves the next state in the workflow.
     *
     * @param fulfilment pickup or delivery, which decides the branch after PRINTING
     * @return the next state, or this state if terminal
     */
    OrderState next(FulfilmentType fulfilment);
}
```

```java
/**
 * Template Method base for all activities: fixes the set-up order, subclasses supply the steps.
 *
 * @param <VB> the ViewBinding type of the activity layout
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    /** The inflated view binding, available after {@link #onCreate(Bundle)}. */
    protected VB binding;

    /**
     * Inflates the binding and runs the template steps in order.
     *
     * @param savedInstanceState previously saved state, may be null
     */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        initObservers();
        initListeners();
    }

    /** @param inflater layout inflater @return the inflated binding */
    protected abstract VB inflateBinding(LayoutInflater inflater);
    /** Configures views; called once. */
    protected abstract void initViews();
    /** Subscribes to ViewModel LiveData. */
    protected void initObservers() { }
    /** Registers click and input listeners. */
    protected void initListeners() { }
}
```

---

## 4. OOP concept mapping (for the report)

| Concept | Implementation |
|---------|----------------|
| Encapsulation | Model classes with private fields plus getters/setters. `Money` is an immutable value object. `SessionManager` hides SharedPreferences |
| Inheritance | `BaseActivity`, `BaseFragment`, `BaseViewModel`, `BaseDao<T>`, `BaseListAdapter<T,VB>`, `BaseBottomSheet`; `Customer extends User` is **not** used (a single role keeps it simple) |
| Polymorphism | `List<PricingStrategy>`, `OrderState` implementations, `ValidationRule` implementations, `FulfilmentStrategy` (Pickup/Delivery) |
| Abstraction | Abstract base classes and interfaces. Repositories expose intent-level methods (`cancelOrder(id, reason)`) |
| Composition | Screens composed from components. `FormValidator` composes rules |
| Generics | `BaseDao<T>`, `Resource<T>`, `BaseListAdapter<T, VB>` |

---

## 5. Package structure

```
com.printxpress.app
├── PrintXpressApp.java                 Application: builds AppContainer, notification channels, seeds DB
├── di/
│   ├── AppContainer.java               Service Locator (repositories, helpers)
│   └── ViewModelFactory.java           Factory for all ViewModels
├── core/
│   ├── base/        BaseActivity, BaseFragment, BaseViewModel, BaseBottomSheet, BaseListAdapter, BaseViewHolder
│   ├── common/      Resource<T>, Status, Event<T> (single-shot), Money, Constants
│   ├── concurrency/ AppExecutors
│   ├── session/     SessionManager
│   ├── validation/  ValidationRule, ValidationResult, FormValidator,
│   │                rules/ RequiredRule, LengthRule, RegexRule, EmailRule, SriLankanPhoneRule,
│   │                PasswordStrengthRule, MatchRule, RangeRule, StepRule, PostalCodeRule,
│   │                MinDateRule, NotSundayRule, FileTypeRule, FileSizeRule
│   ├── notification/ NotificationHelper, NotificationChannels, DeepLinkFactory
│   ├── storage/     FileStorageManager (copy SAF uri -> app files)
│   ├── security/    PasswordHasher (PBKDF2)
│   └── util/        CurrencyFormatter, DateTimeUtils, OrderNumberGenerator, KeyboardUtils, IntentUtils
├── data/
│   ├── db/          DatabaseHelper, DbContract, DatabaseSeeder, CursorMapper<T>
│   ├── model/       User, Address, District, Branch, Category, Product, OptionType, ProductOption,
│   │                Design, CartItem, Order, OrderItem, OrderStatusHistory, Promotion,
│   │                AppNotification, GalleryItem, Guideline, Faq, SupportTicket,
│   │                enums/ OrderStatus, FulfilmentType, PaymentMethod, NotificationType, DiscountType
│   ├── dao/         BaseDao<T> + one DAO per aggregate
│   └── repository/  AuthRepository, UserRepository, AddressRepository, CatalogRepository,
│                    CartRepository, OrderRepository, DesignRepository, PromotionRepository,
│                    NotificationRepository, SupportRepository, GalleryRepository
├── domain/
│   ├── pricing/     PricingStrategy, PricingContext, BasePricing, OptionModifierPricing,
│   │                BulkDiscountPricing, PromoCodePricing, CompositePricingStrategy,
│   │                PricingStrategyFactory, PriceCalculator, PriceBreakdown
│   ├── order/       OrderState, AbstractOrderState, PendingState, ConfirmedState, ProcessingState,
│   │                PrintingState, ReadyForPickupState, OutForDeliveryState, CompletedState,
│   │                CancelledState, OrderStateFactory
│   ├── fulfilment/  FulfilmentStrategy, PickupStrategy, HomeDeliveryStrategy
│   └── schedule/    ScheduleCalculator, TimeSlot
├── worker/          OrderProgressWorker, PromotionWorker
└── ui/
    ├── components/  reusable views (see docs/06)
    ├── splash/ onboarding/ auth/ main/ home/ catalog/ product/ cart/ checkout/
    ├── orders/ notifications/ offers/ gallery/ support/ profile/ settings/
```

Each feature package has `XxxFragment`, `XxxViewModel`, optional `XxxAdapter` and `XxxUiState`.

---

## 6. Navigation structure

- `SplashActivity` (SplashScreen API) → `OnboardingActivity` (first run only) → `AuthActivity`
  (`nav_auth`: Login, Register, ResetPassword) → `MainActivity`.
- `MainActivity` hosts `nav_main` + a **BottomNavigationView**: Home · Catalog · Orders · Support · Profile.
- A top-app-bar action on Home opens Notifications (badge count) and Cart.
- Order flow sub-graph: ProductDetail → ConfigureProduct → DesignInput → Cart → Checkout(Fulfilment &
  Schedule) → Review → OrderSuccess.

---

## 7. Coding standards

1. **Javadoc only** – no `//` or inline `/* */`. Every class, interface, enum constant (if not
   self-evident), constructor, public/protected method, and non-trivial private method has Javadoc.
   Class Javadoc states *responsibility* and *pattern role* (e.g. "Concrete Strategy – …").
2. One public top-level class per file. Max ~300 lines per class. Methods ≤ 30 lines.
3. `final` for fields that never change. Prefer immutable models for value objects (`Money`, `PriceBreakdown`).
4. No business logic in Activities/Fragments – only rendering and forwarding events.
5. Resources: `@string`, `@color` (theme attrs `?attr/colorPrimary`), `@dimen`, `@style`.
6. Layout naming: `activity_`, `fragment_`, `item_`, `view_` (component), `dialog_`, `sheet_`.
   View ids use `camelCase` (e.g. `btnPlaceOrder`, `inputEmail`), which suits ViewBinding.
7. SQL: constants from `DbContract`, `?` placeholders, `db.beginTransaction()` / `setTransactionSuccessful()`
   / `endTransaction()` in `try/finally` for multi-table writes, and close cursors with try-with-resources.
8. Errors: repositories never throw to the UI. They return `Resource.error(messageRes)`.
9. Logging: `Log.d` only in debug builds and never with personal data.

### Javadoc template

```java
/**
 * Validates Sri Lankan mobile numbers in local (07XXXXXXXX) or international (+947XXXXXXXX) form.
 * <p>
 * Pattern role: Concrete Strategy for {@link ValidationRule}.
 * </p>
 *
 * @author  <student name>
 * @version 1.0
 * @since   2026-10
 */
public final class SriLankanPhoneRule implements ValidationRule { ... }
```
