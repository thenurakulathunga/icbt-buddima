# 05 – UI/UX Design System (Task C)

> **Source of truth:** the Figma community file
> [*Mobile Apps – Prototyping Kit (Community)*](https://www.figma.com/design/khDmh4kQbKO8PDgQDGSVfp/Mobile-Apps-%E2%80%93-Prototyping-Kit--Community-?node-id=6-757),
> page **❖ Components** (node `6:757`). Font: **Roboto Flex**. Icons: **Hugeicons** (free Stroke Rounded set).
> Every screen reads tokens from this file (through `colors.xml`, `themes.xml`, `type.xml`, `dimens.xml`),
> so a later palette change only touches the tokens.
>
> **Value provenance.** Each value below is marked:
> **Kit** = read directly from the Figma variables · **Derived** = added by us because the kit has no
> value for it (dark theme) or the kit value fails WCAG AA for text · **App** = a PrintXpress-only addition.

## 1. Brand concept

"**Clear, confident print**". The kit's visual language is a single vivid blue (`Highlight`) on a cool,
near-white neutral scale, with three soft support colours (success, warning, error). PrintXpress adopts it
with **one deliberate change**: the blue is darkened from the kit's `#006FFD` to `#0060DB` so that white
button labels and blue link text both pass WCAG AA (§3.5). The kit blue survives as a decorative
fill (`px_brand_bright`). Blue marks every action and every piece of progress (primary buttons, active tab,
the current step in the order timeline), and neutrals keep the product imagery in front. The print-specific
CMYK story appears in the *content* (guidelines on CMYK vs RGB, bleed, DPI), not in the chrome.

Style: Material 3 components re-skinned to the kit. Flat surfaces, thin `Neutral/Light` borders
instead of heavy shadows, rounded corners, plenty of white space, imagery-led catalogue.

## 2. Figma kit – what was pulled

### 2.1 Kit variables (read from the Figma file)

| Group | Variable | Hex |
|-------|----------|-----|
| Highlight | `Highlight/Darkest` | `#006FFD` |
| | `Highlight/Medium` | `#6FBAFF` |
| | `Highlight/Light` | `#B4DBFF` |
| | `Highlight/Lightest` | `#EAF2FF` |
| Neutral – Light | `Neutral/Light/Darkest` | `#C5C6CC` |
| | `Neutral/Light/Dark` | `#D4D6DD` |
| | `Neutral/Light/Medium` | `#E8E9F1` |
| | `Neutral/Light/Light` | `#F8F9FE` |
| | `Neutral/Light/Lightest` | `#FFFFFF` |
| Neutral – Dark | `Neutral/Dark/Darkest` | `#1F2024` |
| | `Neutral/Dark/Dark` | `#2F3036` |
| | `Neutral/Dark/Medium` | `#494A50` |
| | `Neutral/Dark/Light` | `#71727A` |
| | `Neutral/Dark/Lightest` | `#8F9098` |
| Support | `Support/Success/Medium` · `Light` | `#3AC0A0` · `#E7F4E8` |
| | `Support/Warning/Medium` · `Light` | `#FFB37C` · `#FFF4E4` |
| | `Support/Error/Medium` · `Light` | `#FF616D` · `#FFE2E5` |

The kit also has `Highlight/Dark` and `Support/*/Dark` steps. They are not used on any of the component
frames sampled, so they are **not** used here. If you want them, copy their hex values from Figma
(*Local variables* panel) into the tables in §3.

### 2.2 Kit component inventory → PrintXpress components

The kit has iOS-sized frames (375 pt wide). We keep its visual design and change the sizes to Android dp with
48 dp minimum touch targets (see §6).

| Kit section | Kit component (size in kit) | PrintXpress component (docs/06) |
|-------------|-----------------------------|---------------------------------|
| Assets | Icon (24), Avatar S/M/L (40/56/80), Image, Video | Hugeicons vectors, `ProfileHeaderView` avatar, `GalleryTile` |
| Informative | Banner (350×160), Toast – Informative/Success/Warning/Error (280×83), Dialog 2-/3-button (300 w), Tooltip (200 w), Badge – Number (24) / Icon / Empty (16) | `PromoCard`, `InfoBanner` + `SnackbarHelper`, `AppDialog`, (tooltip for guideline hints), `AppTopBar` cart/notification badges |
| Control | Button Primary / Secondary / Tertiary (h 40), Content Switcher (2–4 items), Action Sheet (375×260), Filter, Tabs (h 75) | `LoadingButton` (`filled`/`tonal`/`outlined`), `SegmentedToggle`, `BaseBottomSheet`, `FilterChipGroup`, `TabLayout` on My orders |
| Input | Checkbox, Radio, Toggle (45×28), Star Rating (20), Slider, Number Input (92×30), Text Field (Empty/Typing/Filled/Error/Inactive), Text Area, Dropdown, Search Bar (h 44) | terms checkbox, cancel-reason radios, `MenuRowView` switch, (rating – not in scope), `QuantityStepper`, `PxTextInput`, `PxDropdown`, `SearchBarView` |
| Navigation | Nav Bar (h 56; left/right = none, icon, text, avatar), Tab Bar (bottom, 3–5 items) | `AppTopBar`, `BottomNavigationView` |
| Content | Tag, List Item (h 88/104; avatar/icon/none + badge/checkbox/icon/toggle), Divider, Calendar (monthly/weekly), Accordion, List Title, Card – Vertical/Horizontal | status/turnaround chips, `MenuRowView`, `NotificationItemView`, `DateSlotPicker`, `ExpandableFaqItem`, `SectionHeader`, `ProductCard`, `CategoryCard`, `OrderCard` |
| Progress | Progress Bar (h 8), Stepped Progress Bar, Pagination Dots (8), Loader (32), Stepper (2–5 steps) | `PasswordStrengthMeter`, checkout step bar, `PageIndicator`, in-button spinner, `OrderTimelineView` |
| Chat | Message Bubble, Message Input | not used (support is via ticket form / WhatsApp link) |
| Map | Map, Location Pin | not used (addresses are typed + district dropdown – docs/01 assumption) |
| E-commerce | Shopping Cart Item (335×100) | `CartItemCard` |
| Device | iOS status bar, home indicator, keyboards | ignored – Android system UI |

**Screens in the kit's *Templates* are reference only.** PrintXpress screens (§8) are built from the
components above, never copied from the template frames.

## 3. Colour tokens

### 3.1 Light theme (`values/colors.xml` + `themes.xml`)

| Token (Material 3 attr) | Name | Hex | Source | Usage |
|-------------------------|------|-----|--------|-------|
| `colorPrimary` | Brand Blue | `#0060DB` | Derived | Filled buttons, active tab/nav, selected controls, progress, **and every blue text**: links, prices, text buttons (5.7:1 on white, 5.4:1 on the background, 5.0:1 on `colorPrimaryContainer`) |
| `colorOnPrimary` | White | `#FFFFFF` | Kit | Text/icons on primary |
| `colorPrimaryContainer` | Highlight Lightest | `#EAF2FF` | Kit | Selected chips, tonal buttons, highlighted rows, received toast bg |
| `colorOnPrimaryContainer` | Highlight Deep | `#0050B8` | Derived | Text/icons on primary container (6.6:1) |
| `colorSecondary` | Highlight Medium | `#6FBAFF` | Kit | Secondary fills, slider track, focus rings (non-text only) |
| `colorSecondaryContainer` | Highlight Light | `#B4DBFF` | Kit | Progress track, pressed state of tonal surfaces |
| `colorOnSecondaryContainer` | Neutral Dark Darkest | `#1F2024` | Kit | Text on secondary container |
| `px_brand_bright` | Highlight Darkest | `#006FFD` | Kit | The kit's own blue. Decorative **fills only** – illustration accents, the splash mark, gradient stops. Never behind white text and never as text |
| `colorError` | Error Text | `#C8102E` | Derived | Error helper text, error stroke on `PxTextInput` (5.9:1) |
| `colorErrorContainer` | Error Light | `#FFE2E5` | Kit | Error toast/banner background |
| `px_error_accent` | Error Medium | `#FF616D` | Kit | Error icon on error container, destructive illustration accents |
| `px_success` | Success Medium | `#3AC0A0` | Kit | Success fills (text on it = `#1F2024`) |
| `px_success_container` | Success Light | `#E7F4E8` | Kit | Success toast/banner background |
| `px_on_success_container` | Success Text | `#1D6B53` | Derived | Text on success container (5.6:1) |
| `px_success_text` | Success Text | `#1D6B53` (dark theme `#3AC0A0`) | Derived | Discount amounts and success text on normal surfaces (price summary, promo applied) |
| `px_warning` | Warning Medium | `#FFB37C` | Kit | Warning fills, discount badges (text on it = `#1F2024`) |
| `px_warning_container` | Warning Light | `#FFF4E4` | Kit | Warning banner ("Printing has started") |
| `px_on_warning_container` | Warning Text | `#9A4A0E` | Derived | Text on warning container (5.8:1) |
| `android:colorBackground` | Neutral Light Light | `#F8F9FE` | Kit | Screen background |
| `colorSurface` | Neutral Light Lightest | `#FFFFFF` | Kit | Cards, sheets, top bar, bottom nav |
| `colorSurfaceVariant` | Neutral Light Medium | `#E8E9F1` | Kit | Input fill (inactive), chips, skeletons |
| `colorOnSurface` | Neutral Dark Darkest | `#1F2024` | Kit | Primary text, icons |
| `px_text_strong` | Neutral Dark Dark | `#2F3036` | Kit | Headings on tinted backgrounds |
| `px_text_body` | Neutral Dark Medium | `#494A50` | Kit | Body copy in cards and guidelines |
| `colorOnSurfaceVariant` | Neutral Dark Light | `#71727A` | Kit | Secondary text, helper text, inactive icons (4.8:1) |
| `px_text_disabled` | Neutral Dark Lightest | `#8F9098` | Kit | Placeholder and disabled text **only** (3.2:1 – never for content) |
| `colorOutline` | Neutral Light Darkest | `#C5C6CC` | Kit | Input borders, card strokes, dividers on white |
| `colorOutlineVariant` | Neutral Light Dark | `#D4D6DD` | Kit | Subtle dividers, unselected radio/checkbox ring |
| `px_scrim` | Scrim | `#991F2024` | Derived | Dialog/bottom-sheet overlay (Neutral Dark Darkest at 60 %) |

### 3.2 Dark theme (`values-night/`) – derived

The kit has no dark mode. The dark theme is **Derived** by mapping the kit's own neutral scale in reverse.
Every pair below was checked for ≥ 4.5:1.

| Token | Hex | From kit step |
|-------|-----|---------------|
| `colorPrimary` | `#6FBAFF` | Highlight Medium (7.9:1 on background) |
| `colorOnPrimary` | `#1F2024` | Neutral Dark Darkest |
| `colorPrimaryContainer` | `#0050B8` | Derived deep blue |
| `colorOnPrimaryContainer` | `#EAF2FF` | Highlight Lightest |
| `px_brand_bright` | `#6FBAFF` | Highlight Medium (the bright kit blue is too dark on a dark surface) |
| `android:colorBackground` | `#1F2024` | Neutral Dark Darkest |
| `colorSurface` | `#2F3036` | Neutral Dark Dark |
| `colorSurfaceVariant` | `#494A50` | Neutral Dark Medium |
| `colorOnSurface` | `#F8F9FE` | Neutral Light Light |
| `colorOnSurfaceVariant` | `#C5C6CC` | Neutral Light Darkest (7.7:1 on surface) |
| `colorOutline` | `#71727A` | Neutral Dark Light |
| `colorError` | `#FF8A94` | Derived from Error Medium (7.2:1) |
| `px_success` / `px_warning` | `#3AC0A0` / `#FFB37C` | Kit Medium steps (unchanged) |

### 3.3 Order status colours (`StatusBadge`)

The pairs reuse the kit's Light steps as backgrounds and the derived text colours for legibility.

| Status | Background | Text | Hugeicon |
|--------|-----------|------|----------|
| PENDING | `#FFF4E4` | `#9A4A0E` | `Clock01` |
| CONFIRMED | `#EAF2FF` | `#0050B8` | `CheckmarkCircle02` |
| PROCESSING | `#EAF2FF` | `#0050B8` | `Loading03` |
| PRINTING | `#B4DBFF` | `#1F2024` | `Printer` |
| READY_FOR_PICKUP | `#E7F4E8` | `#1D6B53` | `Store01` |
| OUT_FOR_DELIVERY | `#E7F4E8` | `#1D6B53` | `DeliveryTruck01` |
| COMPLETED | `#3AC0A0` | `#1F2024` | `TaskDone01` |
| CANCELLED | `#FFE2E5` | `#B3261E` | `CancelCircle` |

### 3.4 Gradients

- `bg_gradient_brand`: `#0060DB → #004399` (135°) for the splash, home hero and profile header. White text is 5.7:1 on the light stop and 9.3:1 on the dark one.
- `bg_gradient_promo`: `#FFB37C → #FF616D` (135°) for offer banners. Always use **`#1F2024` text** on it.

### 3.5 Contrast notes (WCAG 2.1 AA, measured)

| Pair | Ratio | Rule |
|------|-------|------|
| White on `colorPrimary` `#0060DB` | 5.68:1 | Passes AA. This is why the primary is the darkened blue and not the kit's `#006FFD` |
| `#0060DB` on white / on `#F8F9FE` / on `#EAF2FF` | 5.68 / 5.40 / 5.04:1 | Passes AA on all three app surfaces, so one blue covers fills **and** text |
| White on the kit's `#006FFD` | **4.48:1** | **Fails** AA for normal text (0.02 short). This is the reason `px_brand_bright` is fills-only |
| `#006FFD` on white | 4.48:1 | Fails. Never use the kit blue as text |
| `#71727A` on `#F8F9FE` | 4.55:1 | OK for secondary text on the background |
| `#8F9098` on white | 3.18:1 | Placeholder/disabled only |
| `#FF616D` / `#3AC0A0` / `#FFB37C` on white | 2.9 / 2.3 / 1.8:1 | Fills and icons only, never text |

Check these again in the built app with the Android Studio Accessibility Scanner.

## 4. Typography – Roboto Flex

**Roboto Flex** (Google Fonts, SIL Open Font License 1.1) is one variable font that covers every weight we
need. The kit is set in Inter; Roboto Flex replaces it one-for-one, with the same sizes and weights.

**Setup (minSdk 24):** variable-font axis control through `fontVariationSettings` needs API 26+, so bundle
**static instances** instead of the variable file:

1. Download Roboto Flex from https://fonts.google.com/specimen/Roboto+Flex (a variable TTF).
2. Generate one static TTF per weight with fontTools (`pip install fonttools`). Pin `wght` and leave the other axes
   at their defaults, e.g. `fonttools varLib.instancer RobotoFlex.ttf wght=600 -o roboto_flex_semibold.ttf`.
   Open each output file and check it is static.
3. Put the files in `res/font/`: `roboto_flex_regular.ttf` (400), `roboto_flex_medium.ttf` (500),
   `roboto_flex_semibold.ttf` (600), `roboto_flex_bold.ttf` (700), `roboto_flex_extrabold.ttf` (800).
4. Create `res/font/roboto_flex.xml` (font-family with `android:` + `app:` attributes for API 24/25) and set
   `android:fontFamily="@font/roboto_flex"` in `Theme.PrintXpress`.
5. Bundle the fonts (not downloadable fonts) so the app works offline.

### Type scale (`res/values/type.xml` → `TextAppearance.PrintXpress.*`)

Kit letter spacing is in %. Android `letterSpacing` is in em, so 1 % = `0.01`. The kit's "100" line height
means "auto". On Android it becomes font size + 4–6 sp so that text doesn't clip.

| Style | Weight | Size / line height | Letter sp. | Kit style | Source | Use |
|-------|--------|-------------------|-----------|-----------|--------|-----|
| DisplaySmall | ExtraBold 800 | 32 / 40 sp | 0 | – | App | Splash, order success |
| HeadlineMedium | ExtraBold 800 | 24 / 32 sp | 0 | – | App | Large screen titles (Home greeting, Profile name) |
| HeadlineSmall | ExtraBold 800 | 20 / 28 sp | 0 | – | App | Section hero, onboarding titles |
| TitleLarge | ExtraBold 800 | 18 / 24 sp | 0 | – | App | Toolbar title, product name |
| TitleMedium | ExtraBold 800 | 16 / 22 sp | 0.005 | Heading/H3 | Kit | Card titles, dialog titles, section headers |
| TitleSmall | Bold 700 | 14 / 20 sp | 0 | Heading/H4 | Kit | List item titles, banner titles |
| LabelSmallBold | Bold 700 | 12 / 16 sp | 0 | Heading/H5 | Kit | Field labels above inputs, tag text |
| BodyLarge | Regular 400 | 16 / 22 sp | 0 | Body/Body L | Kit | Paragraphs, guideline content |
| BodyMedium | Regular 400 | 14 / 20 sp | 0 | Body/Body M | Kit | Default body, input text |
| BodySmall | Regular 400 | 12 / 16 sp | 0.01 | Body/Body S | Kit | Helper/error text, meta, timestamps |
| LabelLarge | SemiBold 600 | 14 / 20 sp | 0 | (Action M, scaled up) | App | Buttons (Android minimum for button labels) |
| LabelMedium | SemiBold 600 | 12 / 16 sp | 0 | Action/Action M | Kit | Chips, text buttons, tab labels, badges |
| Caption | SemiBold 600 | 10 / 14 sp, ALL CAPS | 0.05 | Caption/Caption M | Kit | Overlines ("NEW", "SEASONAL"), counter badges only |
| Price | ExtraBold 800 | 18 / 24 sp | 0 | – | App | Price display, colour `colorPrimary` |

The kit's `Body/Body XS` (10 sp, 1.5 %) is **not used for body text**: 10 sp is below comfortable Android
reading size. Use `Caption` for the few places that need 10 sp.

## 5. Icon library – Hugeicons

**Hugeicons Free – Stroke Rounded** (https://hugeicons.com). The free set has 4,600+ icons under the **MIT
licence**. The icons are drawn on a 24 × 24 grid with a 1.5 px stroke and rounded caps, which matches the soft corners of the kit.
The other styles (Solid, Duotone, Twotone, Bulk, Sharp) are **Pro only**. Do not use them.

**Import workflow**

1. On hugeicons.com, filter to *Free* → *Stroke Rounded*, search the name from the table below and download the **SVG**
   at 24 px. Icons are also in npm `@hugeicons/core-free-icons` (checked on v4.3.5) with the export name
   `<Name>Icon`, e.g. `Home01Icon`.
2. In the SVG, replace `currentColor` with `#000000` (Vector Asset Studio cannot parse `currentColor`).
3. Import the SVG with **Android Studio › New › Vector Asset › Local file (SVG)**, size 24 dp.
4. Name the files by **meaning, not by source**: `ic_<meaning>.xml`, e.g. `ic_nav_home.xml`. You can then
   swap an icon without touching any layout or Java code.
5. Tint in XML with `app:tint="?attr/colorOnSurface"` (inactive `?attr/colorOnSurfaceVariant`, active
   `?attr/colorPrimary`). The free set has no filled style, so the **active state is shown by colour**
   (and the nav indicator pill), not by switching to a filled glyph.
6. MIT requires the licence notice: add Hugeicons to the *Settings › Open-source licences* screen and to
   the technical documentation (docs/11).

| Area | Resource name → Hugeicons name |
|------|--------------------------------|
| Bottom nav | `ic_nav_home` → `Home01` · `ic_nav_catalog` → `GridView` · `ic_nav_orders` → `Invoice01` · `ic_nav_support` → `CustomerSupport` · `ic_nav_profile` → `UserCircle` |
| Top bar | `ic_notifications` → `Notification01` · `ic_cart` → `ShoppingCart01` · `ic_search` → `Search01` · `ic_back` → `ArrowLeft01` · `ic_close` → `Cancel01` · `ic_more` → `MoreVertical` · `ic_filter` → `FilterHorizontal` · `ic_sort` → `Sorting01` |
| Categories | `ic_cat_business_cards` → `IdentityCard` · `ic_cat_flyers` → `File01` · `ic_cat_posters` → `Image01` · `ic_cat_banners` → `Flag01` · `ic_cat_stickers` → `Sticker` · `ic_cat_mugs` → `Coffee01` · `ic_cat_tshirts` → `TShirt` |
| Ordering | `ic_upload` → `FileUpload` · `ic_text` → `TextFont` · `ic_add` → `Add01` · `ic_minus` → `MinusSign` · `ic_delete` → `Delete02` · `ic_edit` → `PencilEdit01` · `ic_image` → `Image01` · `ic_pdf` → `Pdf01` · `ic_offer` → `DiscountTag01` · `ic_calendar` → `Calendar03` · `ic_time` → `Clock01` · `ic_pickup` → `Store01` · `ic_delivery` → `DeliveryTruck01` · `ic_payment` → `Cash01` · `ic_check` → `CheckmarkCircle02` · `ic_copy` → `Copy01` · `ic_chevron_right` → `ArrowRight01` · `ic_chevron_down` → `ArrowDown01` · `ic_share` → `Share08` |
| Status | see §3.3 (`Clock01`, `CheckmarkCircle02`, `Loading03`, `Printer`, `Store01`, `DeliveryTruck01`, `TaskDone01`, `CancelCircle`) |
| Auth/profile | `ic_mail` → `Mail01` · `ic_phone` → `SmartPhone01` · `ic_lock` → `Lock` · `ic_visibility` → `View` · `ic_visibility_off` → `ViewOff` · `ic_person` → `UserCircle` · `ic_location` → `Location01` · `ic_saved_designs` → `Folder01` · `ic_history` → `History` · `ic_logout` → `Logout01` · `ic_settings` → `Settings02` · `ic_dark_mode` → `Moon02` · `ic_privacy` → `Shield01` · `ic_camera` → `Camera01` |
| Support | `ic_guidelines` → `Book02` · `ic_faq` → `HelpCircle` · `ic_whatsapp` → `Whatsapp` (or `Chat01`) · `ic_call` → `Call` · `ic_ticket` → `Ticket01` · `ic_colour` → `Palette` · `ic_bleed` → `Crop` · `ic_dpi` → `Hd` · `ic_font` → `TextFont` |
| Feedback | `ic_info` → `InformationCircle` · `ic_warning` → `Alert02` · `ic_error` → `AlertCircle` · `ic_offline` → `WifiDisconnected01` · `ic_empty` → `Inbox` · `ic_promo` → `Megaphone01` · `ic_gift` → `Gift` |

Every name above exists in the free package. Use the WhatsApp glyph only as the WhatsApp brand guidelines allow.
If in doubt, use the generic `Chat01`.

## 6. Spacing, shape, elevation (`dimens.xml`)

Kit frames are 375 pt wide and most content blocks are 327 pt, which gives a **24 pt side margin**. We keep
24 dp as the screen gutter. Kit heights under 48 are raised to the Android 48 dp touch minimum; the visible
shape can stay smaller inside a 48 dp hit area.

| Token | Value | Kit reference | | Token | Value | Kit reference |
|-------|-------|---------------|-|-------|-------|---------------|
| `space_xxs` | 4dp | – | | `radius_xs` | 4dp | see §6.1 |
| `space_xs` | 8dp | – | | `radius_sm` | 8dp | see §6.1 |
| `space_sm` | 12dp | – | | `radius_md` | 12dp | see §6.1 |
| `space_md` | 16dp (card padding) | card inner padding | | `radius_lg` | 16dp | see §6.1 |
| `space_lg` | 24dp (**screen gutter**) | (375 − 327) / 2 | | `radius_xl` | 24dp | see §6.1 |
| `space_xl` | 32dp | – | | `radius_xxl` | 32dp (sheet top corners) | see §6.1 |
| `radius_pill` | 999dp (50 %) | see §6.1 | | `elevation_sheet` | 8dp | action sheet |
| `stroke_default` | 1dp (`colorOutline`) | component borders | | `elevation_card` | 0dp (stroke instead) | flat kit cards |
| `button_height` | 48dp | Button h 40 → touch min | | `elevation_dialog` | 8dp | Dialog / overlay |
| `input_height` | 48dp | Text Field / Search h 44 → 48 | | `icon_md` / `icon_sm` | 24dp / 16dp | Icon S/M/L 16/20/24 |
| `toolbar_height` | 56dp | Nav Bar h 56 | | `avatar_sm/md/lg` | 40 / 56 / 80dp | Avatar S/M/L |
| `bottom_nav_height` | 64dp | Tab Bar 89 − 34 iOS indicator | | `badge_size` / `badge_dot` | 20dp / 8dp | Badge Number 24 / Empty 16 |
| `progress_height` | 8dp | Progress Bar h 8 | | `page_dot` | 8dp | Pagination Dots |
| `list_item_min` | 72dp | List Item h 88 (incl. padding) | | `cart_item_height` | 100dp | Shopping Cart Item h 100 |
| `touch_min` | 48dp | – | | `dialog_width` | 300dp min | Dialog w 300 |

Grid: 4 dp baseline, 24 dp side gutter, 16 dp gap. 2-column product grid on phones (3 on ≥ 600 dp width).

### 6.1 Corner radius scale

The radius ladder follows the agreed mobile scale: the smaller the element, the tighter the corner, so that
a badge inside a card never looks rounder than the card holding it. Pick the radius by **element size**, not
by taste, and never mix two radii on one component.

| Element size | Scale | Token | Value | Applied to |
|--------------|-------|-------|-------|------------|
| Small elements (badges, tags, small icon tiles) | 4–8 dp | `radius_xs` / `radius_sm` | 4dp / 8dp | `StatusBadge` (square form), counter badge, "NEW"/"SEASONAL" overline tag, category icon tile, checkbox, file-type chip on `DesignThumbCard` |
| Input fields and small buttons | 8–12 dp | `radius_md` | 12dp | `PxTextInput`, `PxDropdown`, `SearchBarView`, `QuantityStepper` buttons, promo-code field, small/secondary buttons, `DateSlotPicker` date cells |
| Standard buttons and chips | 12–16 dp (or pill) | `radius_lg` / `radius_pill` | 16dp / 999dp | `LoadingButton` (all three styles) and `StickyBottomBar` button = 16dp; `FilterChipGroup`, `OptionSelectorGroup` chips, `SegmentedToggle` pill, `StatusBadge` (pill form), promo code chip = `radius_pill` |
| Cards and containers | 16–24 dp | `radius_lg` / `radius_xl` | 16dp / 24dp | 16dp: `ProductCard`, `OrderCard`, `CartItemCard`, `AddressCard`, `GuidelineCard`, `InfoBanner`, `SelectableOptionCard`, `FileUploadView`, snackbars · 24dp: `PromoCard` / `PromoCarousel` hero, `GalleryTile`, home hero |
| Modals, bottom sheets, large popups | 24–32 dp | `radius_xl` / 32dp | 24dp / 32dp | 24dp: `AppDialog`, `LoadingOverlay` card · 32dp (top corners only): `BaseBottomSheet` – filter, reschedule, cancel, sort sheets |

Rules:

1. Avatars, the `PageIndicator` dots, the notification unread dot, the loader and the timeline step circles
   are **full circles**, not radius tokens.
2. Bottom sheets round the **top two corners only** (`shape_bottom_sheet.xml`).
3. Images inside a card use the card's radius on the corners they touch, and 0 dp where they meet the card edge
   (`ProductCard` image = 16 dp top corners).
4. The radius values live in `dimens.xml` and are applied through Material `ShapeAppearance` styles
   (`ShapeAppearance.PrintXpress.Small` / `.Medium` / `.Large` / `.ExtraLarge` / `.Pill`), never set per screen.

### 6.2 Component styling rules taken from the kit

- **Buttons:** Primary = filled `colorPrimary` · Secondary = 1.5 dp `colorPrimary` outline, blue label · Tertiary = text only, blue label.
- **Text field states:** Empty (`colorOutline` border, placeholder `px_text_disabled`) → Typing (1.5 dp `colorPrimary`
  border) → Filled (`colorOutline` border, `colorOnSurface` text) → Error (`colorError` border + `BodySmall` error text) → Inactive (`colorSurfaceVariant` fill, no border).
  The label sits **above** the field in `LabelSmallBold`, which is the kit's layout, not Material's floating label.
- **Toasts/banners** (`InfoBanner`, `SnackbarHelper`): container colour + matching Medium-step icon + dark title/body
  for Informative (`#EAF2FF` container, `colorPrimary` icon and title), Success, Warning and Error.
- **Tags/chips:** `colorPrimaryContainer` fill + `colorOnPrimaryContainer` `LabelMedium` text. Selected = `colorPrimary` fill + white.
- **Bottom nav (Tab Bar):** white surface, top divider `colorOutlineVariant`. Active = `colorPrimary` icon + label, inactive = `px_text_disabled` icon.
- **Content switcher → `SegmentedToggle`:** `colorSurfaceVariant` track, white selected pill, `LabelMedium`.
- **Stepper → `OrderTimelineView` / checkout steps:** done = `colorPrimary` filled circle + check, current = `colorPrimary` ring, not-done = `colorOutline` ring.

## 7. Motion

- Screen transitions: Material shared-axis X (forward/back) through Navigation `enterAnim` resources.
- Product image → detail: shared element transition.
- Button press: ripple (`colorPrimary` at 12 %). Loading: in-button spinner (`LoadingButton`), shimmer skeletons for lists.
- Order timeline step: 300 ms fade/scale when the status changes.
- Durations: 150 ms (small), 250 ms (medium), 350 ms (large).

## 8. Screen inventory (≈ 32 screens)

| # | Screen | Key components |
|---|--------|----------------|
| S01 | Splash | Logo, brand gradient |
| S02 | Onboarding (3 slides: Browse · Upload · Track) | ViewPager2, `PageIndicator`, `LoadingButton` |
| S03 | Login (Email/Phone toggle) | `SegmentedToggle`, `PxTextInput`, `LoadingButton` |
| S04 | Register | `PxTextInput` ×5, terms checkbox, `PasswordStrengthMeter` |
| S05 | Reset password | `PxTextInput`, `LoadingButton` |
| S06 | Home | `AppTopBar` (bell + cart badges), `SearchBarView`, `PromoCarousel`, `SectionHeader`, `CategoryChipRow`, `ProductCard` grid, gallery strip |
| S07 | Catalog (all categories) | `CategoryCard` grid |
| S08 | Product list | `SearchBarView`, `FilterChipGroup`, sort sheet, `ProductCard`, `EmptyStateView` |
| S09 | Filter bottom sheet | `OptionSelectorGroup` (type/material/size), price sort |
| S10 | Product detail | image pager, specs table, price table, sample previews, `StickyBottomBar` |
| S11 | Configure product | `OptionSelectorGroup` per option type, `QuantityStepper`, `PriceSummaryView` (live) |
| S12 | Design input | `SegmentedToggle` Upload/Text, `FileUploadView`, `PxTextInput` multiline, saved designs picker |
| S13 | Cart | `CartItemCard`, `PriceSummaryView`, `EmptyStateView` |
| S14 | Checkout – fulfilment and schedule | `SelectableOptionCard` Pickup/Delivery, `AddressCard`, branch selector, `DateSlotPicker` |
| S15 | Checkout – review and pay | order summary, promo `PxTextInput` + Apply, payment `SelectableOptionCard`, `LoadingButton` |
| S16 | Order success | illustration, order number, `LoadingButton` "Track order" |
| S17 | My orders (Active/Past tabs) | `TabLayout`, `OrderCard`, `StatusBadge`, `EmptyStateView` |
| S18 | Order detail/tracking | `OrderTimelineView`, items, `PriceSummaryView`, action buttons |
| S19 | Reschedule sheet | `DateSlotPicker` in `BaseBottomSheet` |
| S20 | Cancel sheet | reason radio list, `PxTextInput`, confirm `AppDialog` |
| S21 | Notifications | `NotificationItemView`, swipe-to-mark-read, "Mark all read" |
| S22 | Offers | `PromoCard` list, copy code |
| S23 | Offer detail | hero image, terms, code, "Shop now" |
| S24 | Gallery | category chips + staggered grid `GalleryTile` |
| S25 | Gallery viewer | full-screen ViewPager2 with pinch zoom |
| S26 | Support hub | `MenuRowView` rows: Guidelines, FAQs, Contact, My tickets |
| S27 | Print guidelines list + detail | `GuidelineCard`, illustrated content |
| S28 | FAQs | `SearchBarView`, `ExpandableFaqItem` |
| S29 | Contact support / New ticket | contact action chips (Call/Email/WhatsApp), ticket form |
| S30 | Profile | `ProfileHeaderView`, `MenuRowView` list |
| S31 | Edit profile / Change password | `PxTextInput`, avatar picker |
| S32 | Addresses list + Add/Edit address | `AddressCard`, district dropdown (`PxDropdown`) |
| S33 | Saved designs | `DesignThumbCard` grid |
| S34 | Settings | notification toggles, theme, privacy policy, open-source licences, delete account |

## 9. UX rules

1. Primary action at the bottom in a `StickyBottomBar`, thumb-reachable.
2. Validate inline when a field loses focus, and validate everything on submit. Scroll to and focus the first error.
3. Every list has loading (shimmer), empty (illustration + CTA) and error (retry) states.
4. Destructive actions (cancel order, delete address or account) always use a confirmation `AppDialog`.
5. Disabled Cancel/Reschedule buttons show a helper text explaining why ("Printing has started").
6. Show prices as `Rs. 1,250.00` and dates as `Tue, 06 Oct 2026`.
7. Show a notification permission rationale (Android 13+) before asking for the permission.
8. Never put text in `#006FFD` (`px_brand_bright`), `#FF616D`, `#3AC0A0` or `#FFB37C` on white. Blue text is
   always `colorPrimary` `#0060DB`; the other three have `px_on_*_container` text tokens (§3.5).
