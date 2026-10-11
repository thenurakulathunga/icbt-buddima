# 06 – Reusable Components Catalogue

Rule: if a UI element or behaviour appears on **two or more screens**, it lives here. Screens only
compose these components.

Conventions:
- Custom views live in `com.printxpress.app.ui.components`. Each one has a layout `view_<name>.xml`,
  custom attributes in `res/values/attrs.xml` (`<declare-styleable name="PxXxx">`) and full Javadoc.
- Every component exposes a **small public API** (setters + listener interfaces) and never touches
  repositories or ViewModels directly.
- Components read colours from theme attributes, so light and dark mode work automatically.

---

## A. Foundation (base classes – Template Method / Generics)

| Component | Type | Responsibility | Public API |
|-----------|------|----------------|-----------|
| `BaseActivity<VB>` | abstract class | Binding inflation, template `initViews/initObservers/initListeners`, loading overlay, snackbar | `showLoading(boolean)`, `showMessage(@StringRes)` |
| `BaseFragment<VB>` | abstract class | Same template for fragments, clears binding in `onDestroyView` | `navigate(int, Bundle)`, `observe(LiveData, Observer)` |
| `BaseViewModel` | abstract class | Shared `loading`, `message` (`Event<Integer>`) LiveData, `AppExecutors` access | `postError(int)`, `setLoading(boolean)` |
| `BaseBottomSheet<VB>` | abstract class | Rounded-top sheet with binding + template | `show(FragmentManager)` |
| `BaseListAdapter<T, VB>` | abstract class (`ListAdapter`) | DiffUtil, generic ViewHolder, item click listener | `setOnItemClickListener(OnItemClickListener<T>)`, `abstract bind(VB, T, int)` |
| `BaseDao<T>` | abstract class | Cursor mapping, insert/update/delete helpers, transaction helper | `insert(T)`, `update(T)`, `deleteById(long)`, `findById(long)`, `runInTransaction(Runnable)` |
| `Resource<T>` | generic class | Loading/Success/Error wrapper | `loading()`, `success(T)`, `error(int)` |
| `Event<T>` | generic class | One-shot LiveData event (navigation, toast) | `getContentIfNotHandled()` |
| `Money` | value object | Currency-safe arithmetic (`BigDecimal`) | `plus`, `minus`, `times`, `percent`, `format()` |

## B. Input and form components

| Component | Extends | Used on | API / attributes |
|-----------|---------|---------|------------------|
| **`PxTextInput`** | `TextInputLayout` wrapper (LinearLayout) | Login, Register, Profile, Address, Ticket, Promo, Custom text | attrs: `px_label`, `px_hint`, `px_inputType`, `px_startIcon`, `px_maxLength`, `px_helper`. API: `getText()`, `setText()`, `setError(@StringRes)`, `clearError()`, `setOnTextChanged(...)` |
| **`PxDropdown`** | `TextInputLayout` + `MaterialAutoCompleteTextView` | District, Branch, Cancel reason, Label | `setItems(List<T>, Function<T,String>)`, `getSelected()`, `setError()` |
| **`LoadingButton`** | FrameLayout (MaterialButton + ProgressIndicator) | Every submit action | attrs: `px_text`, `px_style` (filled/tonal/outlined). API: `setLoading(boolean)` |
| **`QuantityStepper`** | LinearLayout | Configure product, Cart | `setRange(min, max, step)`, `getValue()`, `setOnValueChangedListener()`. Supports typed input with range validation |
| **`OptionSelectorGroup`** | LinearLayout (title + `ChipGroup`) | Configure product, Filter sheet | `bind(OptionType, List<ProductOption>)`, `getSelectedOption()`, `setSingleSelection(boolean)`, listener |
| **`SegmentedToggle`** | `MaterialButtonToggleGroup` | Login (Email/Phone), Design input (Upload/Text), Orders filters | `setOptions(String...)`, `setOnSelectionChanged()` |
| **`FileUploadView`** | MaterialCardView | Design input, Support ticket | dashed drop-zone state, then a preview state (thumbnail/PDF icon, name, size, remove). `setFile(DesignFile)`, `clear()`, `setOnPickClickListener()`, `setError()` |
| **`DateSlotPicker`** | LinearLayout | Checkout, Reschedule sheet | horizontal date chips (next 14 valid days, Sundays disabled) + slot chips (09–12, 12–15, 15–18). `setMinDate(LocalDate)`, `getSelection()` |
| **`SelectableOptionCard`** | MaterialCardView (checkable) | Pickup vs Delivery, Payment method | attrs: `px_icon`, `px_title`, `px_subtitle`. `setChecked()`, grouped by `SelectableCardGroup` |
| **`PasswordStrengthMeter`** | LinearLayout (LinearProgressIndicator + label) | Register, Change password | `update(String password)` |
| **`SearchBarView`** | MaterialCardView | Home, Product list, FAQs | `setHint()`, `setOnQueryChanged()` (debounced 300 ms), `setOnFilterClick()` |

## C. Display components

| Component | Used on | API |
|-----------|---------|-----|
| **`AppTopBar`** (`MaterialToolbar` wrapper) | All inner screens | `setTitle()`, `showBack(boolean)`, `setCartBadge(int)`, `setNotificationBadge(int)`, action listeners |
| **`SectionHeader`** | Home, Product detail, Profile | attrs `px_title`, `px_actionText`. `setOnActionClick()` ("See all") |
| **`CategoryCard`** / `CategoryChip` | Home (row), Catalog (grid) | `bind(Category)` – icon on tinted circle + name |
| **`ProductCard`** | Home, Product list | `bind(Product)` – image, name, "from Rs. X", turnaround chip, promo ribbon |
| **`PromoCard`** + **`PromoCarousel`** | Home, Offers | `bind(Promotion)` – gradient/image, title, code chip (tap to copy). Carousel = ViewPager2 + `PageIndicator`, auto-scroll 5 s |
| **`PriceSummaryView`** | Configure, Cart, Checkout, Order detail | `bind(PriceBreakdown)` – subtotal, bulk discount, promo, delivery fee, **total** |
| **`CartItemCard`** | Cart, Checkout review | `bind(CartItem)` – thumb, specs summary, `QuantityStepper`, edit/remove |
| **`OrderCard`** | My orders, Home ("Active order") | `bind(Order)` – number, date, item count, total, `StatusBadge` |
| **`StatusBadge`** | Order card, Order detail | `setStatus(OrderStatus)` – colour/icon from the status table in docs/05 |
| **`OrderTimelineView`** | Order detail | `bind(List<OrderStatusHistory>, OrderStatus current, FulfilmentType)` – vertical stepper: done/current/upcoming |
| **`AddressCard`** | Addresses, Checkout | `bind(Address)`, selectable mode, default tag, edit/delete menu |
| **`NotificationItemView`** | Notifications | `bind(AppNotification)` – type icon, title, message, relative time, unread dot |
| **`ExpandableFaqItem`** | FAQs | `bind(Faq)`, animated expand/collapse |
| **`GuidelineCard`** | Guidelines, Design input (tip) | `bind(Guideline)` – icon, title, short text |
| **`GalleryTile`** | Gallery, Home strip, Product detail samples | `bind(GalleryItem)` – rounded image + "Seasonal" tag |
| **`DesignThumbCard`** | Saved designs, Design input picker | `bind(Design)` – preview/PDF icon, title, date, selectable |
| **`ProfileHeaderView`** | Profile | avatar (Glide circle crop), name, email/phone, gradient bg |
| **`MenuRowView`** | Profile, Support hub, Settings | attrs `px_icon`, `px_title`, `px_subtitle`, `px_showChevron`, optional `MaterialSwitch` |
| **`StickyBottomBar`** | Product detail, Configure, Cart, Checkout | left: label + price; right: `LoadingButton` |
| **`PageIndicator`** | Onboarding, Promo carousel, Gallery viewer | `attachTo(ViewPager2)` |
| **`InfoBanner`** | Checkout (turnaround note), Order detail (printing started), Guidelines | attrs `px_type` (info/warning/success/error), `setMessage()` |

## D. State and feedback components

| Component | Purpose | API |
|-----------|---------|-----|
| **`StateLayout`** | Wraps content and switches between LOADING (shimmer), CONTENT, EMPTY, ERROR | `showLoading()`, `showContent()`, `showEmpty(EmptyConfig)`, `showError(msg, retry)` |
| **`EmptyStateView`** | Illustration + title + message + CTA | `bind(@DrawableRes, @StringRes title, @StringRes msg, @StringRes cta, listener)` |
| **`ErrorStateView`** | Error illustration + retry | `setOnRetry()` |
| **`SkeletonView`** layouts | Shimmer placeholders: `view_skeleton_product`, `view_skeleton_order` | – |
| **`AppDialog`** (Builder) | Consistent confirm/info dialogs (MaterialAlertDialogBuilder wrapper) | `new AppDialog.Builder(ctx).icon().title().message().positive(text, cb).negative().destructive(true).show()` |
| **`SnackbarHelper`** | Themed success/error/info snackbars | `success(View, @StringRes)`, `error(...)`, `info(...)` |
| **`LoadingOverlay`** | Full-screen blocking progress for transactions | `show()`, `hide()` |

## E. Non-UI reusable components

| Component | Pattern | Responsibility |
|-----------|---------|----------------|
| `FormValidator` + `ValidationRule`s | Composite + Strategy | Bind rules to `PxTextInput`s; `validateAll()` returns boolean and shows errors |
| `PriceCalculator` + `PricingStrategy`s | Strategy + Composite | Single source of truth for all prices |
| `OrderStateFactory` + `OrderState`s | Factory + State | Transition and permission rules |
| `FulfilmentStrategy` | Strategy | Delivery fee + earliest date |
| `ScheduleCalculator` | – | Valid dates (turnaround, Sundays, holidays) and time slots |
| `SessionManager` | Singleton | Current user id, remember-me, first-run flag, theme |
| `PasswordHasher` | Utility | PBKDF2 hash + verify |
| `FileStorageManager` | Utility | Copy SAF URI to `files/designs`, MIME/size checks, delete |
| `NotificationHelper` | Singleton | Channels (`orders`, `promotions`), build + post + deep-link PendingIntent, respects user prefs |
| `IntentUtils` | Utility | Dial, email, WhatsApp (`https://wa.me/94XXXXXXXXX`), share |
| `CurrencyFormatter`, `DateTimeUtils`, `OrderNumberGenerator` | Utility | Formatting and IDs |
| `AppExecutors` | Singleton | `diskIO()`, `mainThread()` |
| `ViewModelFactory` | Factory | Creates every ViewModel with its repositories from `AppContainer` |

## F. Resource-level reusables

- **Styles:** `Widget.PrintXpress.Button.Filled/Tonal/Outlined/Text`, `Widget.PrintXpress.Card`,
  `Widget.PrintXpress.Chip.Filter/Choice`, `Widget.PrintXpress.TextInput`, `ShapeAppearance.PrintXpress.*`.
- **Drawables:** `bg_gradient_brand`, `bg_gradient_promo`, `bg_dashed_upload`, `bg_badge_dot`,
  `bg_rounded_top_sheet`, `divider_inset`.
- **Anim:** `slide_in_right`, `slide_out_left`, `fade_in`, `fade_out`, `expand`, `collapse`.
- **Includes:** `layout_toolbar_main.xml`, `layout_sticky_bottom.xml`.
