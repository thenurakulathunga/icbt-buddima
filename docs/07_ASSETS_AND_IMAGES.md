# 07 – Assets and Images List

Everything the app needs visually, with the file name to use, the size, and where to get it for free.
**Record the source and licence of every image in the report appendix** (Harvard reference style), for
example: *Unsplash (2026) 'Business cards mockup' by <photographer>. Available at: <url> (Accessed: dd Month yyyy).*

## Free, licence-safe sources

| Source | Licence | Best for |
|--------|---------|----------|
| https://unsplash.com | Unsplash licence (free, no attribution required, but credit it anyway) | Product photos, hero images |
| https://www.pexels.com | Pexels licence (free) | Product and print-shop photos |
| https://undraw.co | MIT-like, free, colour-customisable (set the colour to `#0060DB`) | Onboarding, empty states, success |
| https://storyset.com | Free with attribution | Alternative illustrations |
| https://hugeicons.com (Free → Stroke Rounded) | MIT | All icons (docs/05 §5) |
| https://fonts.google.com/specimen/Roboto+Flex | SIL OFL 1.1 | App font (docs/05 §4) |
| https://www.freepik.com (free items) | Attribution required | Mockups (mugs, t-shirts, business cards) |
| https://lottiefiles.com (free) | Lottie Simple License | Optional animations (success tick, loading) |

## Format and placement rules

- Photos → **WebP** (quality 80), put in `res/drawable-nodpi/`, max 1080 px on the long side, < 150 KB each.
- Illustrations → **SVG imported as Vector Drawable** (`res/drawable/`), so they stay sharp and small.
- Icons → Hugeicons Free **Stroke Rounded** SVG at 24 px, `currentColor` replaced with `#000000` before
  importing, then tinted with `?attr/colorOnSurface`. Full workflow and the icon list are in docs/05 §5.
- Launcher icon → Android Studio **Image Asset Studio** (adaptive icon: foreground + `#0060DB` background).
- Rounded corners on any image asset come from the component's `ShapeAppearance` (docs/05 §6.1), not from
  the image file. Bake nothing into the bitmap.
- Naming: `img_<group>_<name>.webp`, `ill_<name>.xml`, `ic_<name>.xml`.
- DB seed columns `image_res` / `icon_res` store the drawable **name** (e.g. `img_product_business_card_std`).
  Resolve it with `getResources().getIdentifier(name, "drawable", pkg)` inside a `DrawableResolver` utility.

---

## 1. Branding

| File | Size | Description / how to make |
|------|------|---------------------------|
| `ic_launcher` (adaptive) | 432×432 fg | "PX" monogram or printer glyph, white on `#0060DB` background |
| `img_logo_full.xml` | vector | Wordmark "PrintXpress". Roboto Flex ExtraBold 800, `#1F2024` with the "X" in `#0060DB` |
| `img_logo_mark.xml` | vector | Monogram for the splash screen (SplashScreen API icon, 288 dp canvas) |

## 2. Onboarding and state illustrations (unDraw, recolour to #0060DB)

Set the unDraw colour picker to `#0060DB` before downloading. Keep the greys the illustration ships with —
they sit close enough to the kit's neutral scale (`#8F9098` / `#C5C6CC` / `#E8E9F1`). If an illustration
needs a second accent, use `#6FBAFF`; never introduce a new hue.

| File | unDraw search term | Screen |
|------|--------------------|--------|
| `ill_onboard_browse.xml` | "online shopping" / "choose" | Onboarding 1 – Browse products |
| `ill_onboard_upload.xml` | "upload" / "design tools" | Onboarding 2 – Upload your design |
| `ill_onboard_track.xml` | "delivery" / "order delivered" | Onboarding 3 – Track and receive |
| `ill_login.xml` | "login" / "secure login" | Login header |
| `ill_order_success.xml` | "order confirmed" / "completed" | Order success |
| `ill_empty_cart.xml` | "empty cart" | Cart empty |
| `ill_empty_orders.xml` | "no data" / "empty" | Orders empty |
| `ill_empty_notifications.xml` | "notify" / "push notifications" | Notifications empty |
| `ill_empty_search.xml` | "search" / "not found" | No products found |
| `ill_empty_designs.xml` | "art" / "design inspiration" | Saved designs empty |
| `ill_empty_address.xml` | "address" / "location search" | Addresses empty |
| `ill_error.xml` | "server down" / "bug fixing" | Error state |
| `ill_support.xml` | "customer support" / "contact us" | Support hub header |

## 3. Category images (Unsplash / Pexels, 800×600, WebP)

| File | Search keywords |
|------|-----------------|
| `img_cat_business_cards.webp` | "business card mockup" |
| `img_cat_flyers.webp` | "flyer mockup", "leaflet print" |
| `img_cat_posters.webp` | "poster mockup wall" |
| `img_cat_banners.webp` | "roll up banner", "vinyl banner" |
| `img_cat_stickers.webp` | "sticker sheet", "die cut stickers" |
| `img_cat_mugs.webp` | "white mug mockup", "custom printed mug" |
| `img_cat_tshirts.webp` | "t-shirt mockup", "screen printing shirt" |

## 4. Product images (800×800, WebP) – 2 or 3 per category, matching the seed data

| File | Product | Search keywords |
|------|---------|-----------------|
| `img_product_business_card_std.webp` | Standard Business Card | "business cards stack" |
| `img_product_business_card_premium.webp` | Premium Matte / Spot UV | "luxury business card" |
| `img_product_flyer_a5.webp` | A5 Flyer | "a5 flyer mockup" |
| `img_product_flyer_a4.webp` | A4 Flyer | "a4 brochure flyer" |
| `img_product_poster_a3.webp` | A3 Poster | "a3 poster frame" |
| `img_product_poster_a2.webp` | A2 Poster | "large poster print" |
| `img_product_banner_flex.webp` | Flex Banner | "outdoor banner shop" |
| `img_product_banner_rollup.webp` | Roll-up Banner | "roll up standee" |
| `img_product_sticker_diecut.webp` | Die-cut Vinyl Sticker | "vinyl stickers laptop" |
| `img_product_sticker_label.webp` | Product Labels | "product label roll" |
| `img_product_mug_classic.webp` | Classic 11oz Mug | "white ceramic mug" |
| `img_product_mug_magic.webp` | Magic/Colour Mug | "black mug print" |
| `img_product_tshirt_crew.webp` | Cotton Crew T-shirt | "white t-shirt flat lay" |
| `img_product_tshirt_polo.webp` | Polo T-shirt | "polo shirt mockup" |

## 5. Promotions (1200×600, WebP, or a gradient + illustration made in Figma/Canva)

| File | Promo | Idea |
|------|-------|------|
| `img_promo_avurudu.webp` | AVURUDU26 – Sinhala & Tamil New Year | Kolam/erabadu flowers, oil lamp, warm colours |
| `img_promo_vesak.webp` | VESAK26 – lanterns and posters | Vesak lanterns (vesak kudu) at night |
| `img_promo_bulk.webp` | BULK500 – bulk discount | Stacks of printed flyers |
| `img_promo_christmas.webp` | XMAS26 – greeting cards | Christmas cards flat lay |
| `img_promo_welcome.webp` | WELCOME10 – first order | Brand gradient + gift illustration |

## 6. Gallery samples (≈ 20 images, 800×1000, WebP)

`img_gallery_01.webp` … `img_gallery_20.webp`. Mix of business cards (4), flyers (3), posters (3), banners (2),
stickers (3), mugs (2), t-shirts (3). Mark 5 as seasonal (Avurudu, Vesak, Christmas, Deepavali, Valentine's).
Search "<product> mockup design", "wedding invitation card", "cafe menu flyer", "event poster".

## 7. Design-support illustrations (vector, create in Figma/draw.io, or use unDraw)

| File | Content |
|------|---------|
| `ill_guide_bleed.xml` | A card showing the **trim line**, the **3 mm bleed** (red) and the **5 mm safe zone** (green dashed) |
| `ill_guide_cmyk_rgb.xml` | Two overlapping colour circles: RGB (screen) vs CMYK (print) |
| `ill_guide_resolution.xml` | Pixelated 72 DPI vs sharp 300 DPI comparison |
| `ill_guide_file_formats.xml` | PDF / PNG / JPG file badges |
| `ill_guide_fonts.xml` | "Aa" with an outline/embed-fonts note |
| `img_template_bleed_business_card.png` | Downloadable/shareable template (FR-5.5) |

## 8. Miscellaneous

| File | Use |
|------|-----|
| `img_avatar_placeholder.xml` | Default profile avatar (`UserCircle` icon on `colorPrimaryContainer`, full circle) |
| `img_placeholder_product.xml` | Glide placeholder/error image (`colorSurfaceVariant` fill + `Image01` icon) |
| `img_pdf_thumb.xml` | PDF upload preview (`Pdf01` icon) |
| `bg_gradient_brand.xml` (`#0060DB → #004399`), `bg_gradient_promo.xml` (`#FFB37C → #FF616D`) | Gradient shapes (docs/05 §3.4) |
| `bg_dashed_upload.xml` | Dashed rounded rectangle for `FileUploadView`, corner radius 16 dp, stroke `colorOutline` |
| `shape_bottom_sheet.xml` | 32 dp top corners for `BaseBottomSheet` (docs/05 §6.1) |

## 9. Licence and attribution checklist

| Asset group | Licence | What the app/report must show |
|-------------|---------|-------------------------------|
| Hugeicons Free (Stroke Rounded) | MIT | MIT notice in *Settings › Open-source licences* (S34) and in docs/11 |
| Roboto Flex | SIL OFL 1.1 | OFL notice in the same licences screen |
| Figma *Mobile Apps – Prototyping Kit (Community)* | CC BY 4.0 – check the file's own licence line before submission | Credit the kit and its author in the report's design section |
| unDraw | Free, no attribution required | Credit anyway in the appendix |
| Unsplash / Pexels | Free | Photographer + URL in the appendix |
| Storyset / Freepik free items | Attribution **required** | Credit in-app (about screen or image caption) **and** in the appendix |
| Lottie (if used) | Lottie Simple License | Credit in the appendix |

**Total:** ≈ 70 assets (≈ 45 raster, ≈ 25 vector). Keep the raster total under 5 MB so the APK stays small.
