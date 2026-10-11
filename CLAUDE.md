# CLAUDE.md

This file guides Claude Code (claude.ai/code) when working in this repository.

## Project

**PrintXpress** – a native Android customer app for a Sri Lankan digital printing service
(business cards, flyers, posters, banners, stickers, mugs, t-shirts). Customers browse products,
configure specs, upload artwork or enter custom text, choose pickup or home delivery, track and
manage orders, receive notifications/promotions, and use design-support resources.

Academic context: ICBT / Cardiff Met module **CSE5011 Mobile Application Development**, assessment
**WRIT1** (brief: `icbthdcse5011writ1-1013.docx`). Task D (the app) is worth 40% – robust validation
and seamless SQLite integration are graded explicitly.

## Current status

**Documentation phase only. Do NOT scaffold or write application code until the user explicitly
says to start development.**

The design system is **settled** and is no longer a proposal:

- **Theme:** the Figma community file *Mobile Apps – Prototyping Kit*, page ❖ Components
  (`khDmh4kQbKO8PDgQDGSVfp`, node `6:757`). Primary `#0060DB` on cool neutrals – the kit's `#006FFD`
  darkened so white labels and blue text pass WCAG AA; `#006FFD` survives as `px_brand_bright` (fills only).
- **Font:** Roboto Flex (bundled static instances, weights 400–800 – minSdk 24 cannot vary axes).
- **Icons:** Hugeicons **Free / Stroke Rounded** only (MIT). The other styles are paid. No filled variants
  exist, so active states are shown by colour.
- **Shape:** the radius ladder in `docs/05 §6.1`, applied through `ShapeAppearance.PrintXpress.*`.

Values in `docs/05_UI_UX_DESIGN_SYSTEM.md` are tagged **Kit** (read from Figma), **Derived** (added by us,
mostly to pass WCAG AA) or **App**. Keep that tagging when editing. Still open: the kit's `Highlight/Dark`
and `Support/*/Dark` steps, and confirmation of the kit's licence line (TASK.md T-021c, T-021e).
Product images are still to be collected (`docs/07`).

Progress is tracked in `TASK.md` – tick items there as they are completed.

## Documentation map

| File | Purpose |
|------|---------|
| `docs/01_REQUIREMENTS.md` | System + project requirements, FR/NFR IDs, assumptions, marking map |
| `docs/02_ARCHITECTURE_AND_PATTERNS.md` | MVVM layers, design patterns, package tree, coding standards |
| `docs/03_DATABASE_DESIGN.md` | ERD, 3NF schema, DDL, seed data |
| `docs/04_UML_DIAGRAMS.md` | Use case, class, activity, sequence, state diagrams (PlantUML) |
| `docs/05_UI_UX_DESIGN_SYSTEM.md` | Colours, fonts, icons, spacing, screen inventory, navigation |
| `docs/06_COMPONENTS_CATALOG.md` | Every reusable UI + code component and its API |
| `docs/07_ASSETS_AND_IMAGES.md` | Image/illustration list, file names, sizes, sources |
| `docs/08_PROJECT_PLAN.md` | Phases, timeline, milestones, risks |
| `docs/09_TEST_PLAN.md` | Test strategy, test cases, test data (Task E) |
| `docs/10_TASK_A_TECH_COMPARISON_GUIDE.md` | Outline for the critical comparison essay (Task A) |
| `docs/11_DOCUMENTATION_GUIDE.md` | Outline for user + technical documentation (Task F) |
| `docs/12_TECHNICAL_DOCUMENTATION.md` | As-built technical documentation (Task F – B): build, architecture, DB, algorithms, tests |
| `docs/13_USER_GUIDE.md` | User guide with screenshots in `docs/screenshots/` (Task F – A) |

## Tech stack (fixed)

- **Language:** Java 17 (native Android only – Flutter/React Native/Ionic are forbidden by the brief)
- **IDE:** Android Studio (latest stable), Gradle Kotlin DSL build scripts
- **SDK:** minSdk 24, targetSdk/compileSdk 36
- **Database:** SQLite via `SQLiteOpenHelper` (no Room, no Firebase) – raw SQL DAOs
- **UI:** XML layouts + ViewBinding, Material Components 3, ConstraintLayout, RecyclerView, ViewPager2
- **Architecture:** MVVM + Repository (AndroidX ViewModel + LiveData), Navigation Component
- **Background:** WorkManager (order-status simulation, promo notifications), `ExecutorService` for DB I/O
- **Images:** Glide
- **Tests:** JUnit 4, Mockito, AndroidX Test, Espresso

## Architecture

Full detail in `docs/02`. Root package: `com.printxpress.app`. Layers: `core/`, `data/`, `domain/`, `ui/`, `di/`.
Dependencies are wired manually through `di/AppContainer` (Service Locator) created in
`PrintXpressApp`. ViewModels are created only through `di/ViewModelFactory`.

## Mandatory coding rules

1. **Comments: Javadoc only.** Never write `//` single-line comments or `/* */` inline comments in Java.
   Every class, interface, enum, constructor, public/protected method and non-trivial field gets a
   `/** ... */` Javadoc block with `@param`, `@return`, `@throws` as applicable. In XML use at most one
   `<!-- -->` block at the top of a file describing its purpose.
2. **OOP first:** encapsulation (private fields + getters/setters), inheritance through `Base*` classes,
   polymorphism through interfaces (`PricingStrategy`, `OrderState`, `ValidationRule`,
   `FulfilmentStrategy`), abstraction via abstract base classes and repository interfaces.
3. **Use the agreed design patterns consistently** (docs/02 §3): MVVM, Repository, DAO, Singleton,
   Factory, Builder, Strategy, State, Observer, Adapter, Template Method, Composite, Service Locator.
   Do not introduce a different pattern for a problem already solved by one of these.
4. **Reusable components only:** any UI element used on more than one screen must be a component in
   `ui/components` (see docs/06). Screens compose components; never copy-paste layout blocks.
5. **No hard-coded values** in Java or layouts: strings -> `strings.xml`, colours -> `colors.xml`/theme
   attributes, sizes -> `dimens.xml`, text -> `TextAppearance.PrintXpress.*` styles.
6. **Database access never on the main thread** – go through `AppExecutors.diskIO()`; repositories
   expose `LiveData<Resource<T>>`.
7. **All SQL** column/table names come from `DbContract`; always use parameterised queries
   (`?` placeholders) – never string-concatenate user input.
8. **Validation:** every form uses `FormValidator` with `ValidationRule` implementations; validate in
   the ViewModel and show errors on the `PxTextInput` component. Re-validate in the repository for
   business rules (e.g. cancel/reschedule only before PRINTING).
9. **Security:** passwords hashed with PBKDF2WithHmacSHA256 + per-user salt; never log personal data.
10. Naming: classes `PascalCase`, members `camelCase`, constants `UPPER_SNAKE_CASE`, layouts
    `activity_*`, `fragment_*`, `item_*`, `view_*` (components), `dialog_*`, `sheet_*`.

## Domain notes

- Currency LKR, formatted `Rs. 1,250.00` via `CurrencyFormatter`.
- Sri Lankan mobile regex: `^(?:\+94|0)7[0-9]{8}$`.
- Order statuses: PENDING -> CONFIRMED -> PROCESSING -> PRINTING -> (READY_FOR_PICKUP | OUT_FOR_DELIVERY)
  -> COMPLETED, plus CANCELLED. Cancel/reschedule allowed only before PRINTING (`OrderState` decides).
- No real backend: order progress is simulated by `OrderProgressWorker`; payment is Pay-at-Pickup /
  Cash-on-Delivery. These are documented assumptions (docs/01 §6).
