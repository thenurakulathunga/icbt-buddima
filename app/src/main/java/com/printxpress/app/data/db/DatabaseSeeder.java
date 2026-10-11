package com.printxpress.app.data.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.printxpress.app.data.db.DbContract.Branches;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.Districts;
import com.printxpress.app.data.db.DbContract.Faqs;
import com.printxpress.app.data.db.DbContract.GalleryItems;
import com.printxpress.app.data.db.DbContract.Guidelines;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.db.DbContract.ProductOptions;
import com.printxpress.app.data.db.DbContract.Products;
import com.printxpress.app.data.db.DbContract.Promotions;

/**
 * Inserts the reference data from docs/03 §5 when the database is first created.
 * <p>
 * Runs inside the {@code onCreate} transaction, so either all seed rows exist or none do.
 * Prices: {@code base_price} is the price of one unit, and {@code price_modifier} is a percentage
 * added to the unit price (for example Double sided = +40%). Image columns store drawable names
 * that {@code DrawableResolver} turns into resources.
 * </p>
 *
 * @since 1.0
 */
public final class DatabaseSeeder {

    /** Option type ids in insertion order. */
    private static final long SIZE = 1;
    /** Option type id: Paper Type. */
    private static final long PAPER = 2;
    /** Option type id: Material. */
    private static final long MATERIAL = 3;
    /** Option type id: Finish. */
    private static final long FINISH = 4;
    /** Option type id: Sides. */
    private static final long SIDES = 5;
    /** Option type id: Colour. */
    private static final long COLOUR = 6;

    /** The database being seeded. */
    private final SQLiteDatabase db;

    /**
     * Creates a seeder.
     *
     * @param db a writable database inside a transaction
     */
    public DatabaseSeeder(SQLiteDatabase db) {
        this.db = db;
    }

    /** Seeds every reference table. */
    public void seed() {
        seedDistricts();
        seedBranches();
        seedCategories();
        seedOptionTypes();
        seedProducts();
        seedPromotions();
        seedGallery();
        seedGuidelines();
        seedFaqs();
    }

    /** All 25 districts; Colombo Rs 300, Gampaha and Kalutara Rs 400, others Rs 600. */
    private void seedDistricts() {
        String[] districts = {"Ampara", "Anuradhapura", "Badulla", "Batticaloa", "Colombo", "Galle", "Gampaha",
                "Hambantota", "Jaffna", "Kalutara", "Kandy", "Kegalle", "Kilinochchi", "Kurunegala", "Mannar",
                "Matale", "Matara", "Monaragala", "Mullaitivu", "Nuwara Eliya", "Polonnaruwa", "Puttalam",
                "Ratnapura", "Trincomalee", "Vavuniya"};
        for (String name : districts) {
            double fee = "Colombo".equals(name) ? 300 : ("Gampaha".equals(name) || "Kalutara".equals(name)) ? 400 : 600;
            ContentValues v = new ContentValues();
            v.put(Districts.NAME, name);
            v.put(Districts.DELIVERY_FEE, fee);
            db.insertOrThrow(Districts.TABLE, null, v);
        }
    }

    /** Three pickup branches. */
    private void seedBranches() {
        branch("PrintXpress Colombo 03", "No. 45, Galle Road, Colombo 03", "0112345678", "Mon-Sat 8.30 am - 6.00 pm");
        branch("PrintXpress Kandy", "No. 12, Peradeniya Road, Kandy", "0812345678", "Mon-Sat 9.00 am - 5.30 pm");
        branch("PrintXpress Galle", "No. 8, Wakwella Road, Galle", "0912345678", "Mon-Sat 9.00 am - 5.30 pm");
    }

    /**
     * Inserts a branch.
     *
     * @param name    branch name
     * @param address street address
     * @param phone   phone
     * @param hours   opening hours
     */
    private void branch(String name, String address, String phone, String hours) {
        ContentValues v = new ContentValues();
        v.put(Branches.NAME, name);
        v.put(Branches.ADDRESS, address);
        v.put(Branches.PHONE, phone);
        v.put(Branches.OPENING_HOURS, hours);
        db.insertOrThrow(Branches.TABLE, null, v);
    }

    /** Seven categories in display order (ids 1-7). */
    private void seedCategories() {
        category("Business Cards", "Premium cards that make a first impression", "business_cards", 1);
        category("Flyers", "Leaflets and handouts for promotions and events", "flyers", 2);
        category("Posters", "Vivid posters from A3 to A2", "posters", 3);
        category("Banners", "Flex and roll-up banners for shops and events", "banners", 4);
        category("Stickers", "Die-cut stickers and product labels", "stickers", 5);
        category("Mugs", "Custom printed ceramic and magic mugs", "mugs", 6);
        category("T-Shirts", "Cotton crew and polo shirts with your design", "tshirts", 7);
    }

    /**
     * Inserts a category.
     *
     * @param name        category name
     * @param description short description
     * @param key         resource key used to build drawable names
     * @param order       display order
     */
    private void category(String name, String description, String key, int order) {
        ContentValues v = new ContentValues();
        v.put(Categories.NAME, name);
        v.put(Categories.DESCRIPTION, description);
        v.put(Categories.ICON_RES, "ic_cat_" + key);
        v.put(Categories.IMAGE_RES, "img_cat_" + key);
        v.put(Categories.SORT_ORDER, order);
        db.insertOrThrow(Categories.TABLE, null, v);
    }

    /** Six option types (ids 1-6). */
    private void seedOptionTypes() {
        String[] names = {"Size", "Paper Type", "Material", "Finish", "Sides", "Colour"};
        for (int i = 0; i < names.length; i++) {
            ContentValues v = new ContentValues();
            v.put(OptionTypes.NAME, names[i]);
            v.put(OptionTypes.SORT_ORDER, i + 1);
            db.insertOrThrow(OptionTypes.TABLE, null, v);
        }
    }

    /** Products per category with their options. */
    private void seedProducts() {
        seedBusinessCards();
        seedFlyersAndPosters();
        seedBanners();
        seedStickers();
        seedMerchandise();
    }

    /** Business card products. */
    private void seedBusinessCards() {
        long std = product(1, "Standard Business Card", "Classic 3.5 x 2 inch cards on 300gsm art board. "
                + "Crisp full-colour print, ideal for everyday networking.", 15.00, "card", 100, 5000, 50, 2,
                "img_product_business_card_std");
        options(std, SIZE, "3.5\" x 2\"", 0, true);
        options(std, PAPER, "300gsm Art", 0, true, "350gsm Matte", 15, false);
        options(std, FINISH, "Matte", 0, true, "Gloss", 0, false, "Spot UV", 25, false);
        options(std, SIDES, "Single sided", 0, true, "Double sided", 40, false);

        long premium = product(1, "Premium Matte Business Card", "Thick 350gsm matte cards with optional spot UV "
                + "or gold foil for a luxury finish.", 28.00, "card", 100, 5000, 50, 3,
                "img_product_business_card_premium");
        options(premium, SIZE, "3.5\" x 2\"", 0, true);
        options(premium, PAPER, "350gsm Matte", 0, true, "400gsm Soft-touch", 20, false);
        options(premium, FINISH, "Matte", 0, true, "Spot UV", 25, false, "Gold Foil", 45, false);
        options(premium, SIDES, "Single sided", 0, true, "Double sided", 40, false);
    }

    /** Flyer and poster products. */
    private void seedFlyersAndPosters() {
        long a5 = product(2, "A5 Flyer", "Handy A5 flyers for menus, offers and events.", 18.00, "flyer",
                50, 10000, 50, 2, "img_product_flyer_a5");
        options(a5, SIZE, "A5", 0, true);
        options(a5, PAPER, "130gsm Gloss", 0, true, "170gsm Matte", 12, false);
        options(a5, SIDES, "Single sided", 0, true, "Double sided", 40, false);
        options(a5, COLOUR, "Full colour", 0, true, "Black and white", -40, false);

        long a4 = product(2, "A4 Flyer", "A4 flyers and brochures with room for detail.", 30.00, "flyer",
                50, 10000, 50, 2, "img_product_flyer_a4");
        options(a4, SIZE, "A4", 0, true);
        options(a4, PAPER, "130gsm Gloss", 0, true, "170gsm Matte", 12, false);
        options(a4, SIDES, "Single sided", 0, true, "Double sided", 40, false);
        options(a4, COLOUR, "Full colour", 0, true, "Black and white", -40, false);

        long a3 = product(3, "A3 Poster", "Bright A3 posters for notice boards and shop windows.", 250.00, "poster",
                1, 500, 1, 2, "img_product_poster_a3");
        options(a3, SIZE, "A3", 0, true);
        options(a3, PAPER, "170gsm Gloss", 0, true, "200gsm Matte", 10, false);
        options(a3, FINISH, "Gloss", 0, true, "Matte", 0, false);

        long a2 = product(3, "A2 Poster", "Large-format A2 posters that stand out at events.", 450.00, "poster",
                1, 300, 1, 3, "img_product_poster_a2");
        options(a2, SIZE, "A2", 0, true);
        options(a2, PAPER, "170gsm Gloss", 0, true, "200gsm Matte", 10, false);
        options(a2, FINISH, "Gloss", 0, true, "Matte", 0, false);
    }

    /** Banner products. */
    private void seedBanners() {
        long flex = product(4, "Flex Banner", "Weather-proof flex banners with eyelets for shop fronts and events.",
                3500.00, "banner", 1, 50, 1, 3, "img_product_banner_flex");
        options(flex, SIZE, "2 x 4 ft", 0, true, "3 x 6 ft", 80, false, "4 x 8 ft", 150, false);
        options(flex, MATERIAL, "Flex", 0, true);

        long rollup = product(4, "Roll-up Banner", "Portable roll-up standee with carry bag, perfect for exhibitions.",
                9500.00, "banner", 1, 20, 1, 4, "img_product_banner_rollup");
        options(rollup, SIZE, "33 x 80 in", 0, true, "47 x 80 in", 35, false);
        options(rollup, MATERIAL, "PVC", 0, true, "Vinyl", 20, false);
    }

    /** Sticker products. */
    private void seedStickers() {
        long diecut = product(5, "Die-cut Vinyl Sticker", "Durable vinyl stickers cut to any shape.", 45.00, "sticker",
                20, 5000, 10, 3, "img_product_sticker_diecut");
        options(diecut, SIZE, "2 x 2 in", 0, true, "3 x 3 in", 40, false, "4 x 4 in", 80, false);
        options(diecut, MATERIAL, "Vinyl", 0, true);
        options(diecut, FINISH, "Gloss", 0, true, "Matte", 0, false);

        long labels = product(5, "Product Labels", "Roll labels for jars, bottles and packaging.", 12.00, "label",
                100, 10000, 100, 3, "img_product_sticker_label");
        options(labels, SIZE, "2 x 1 in", 0, true, "3 x 2 in", 50, false);
        options(labels, MATERIAL, "Vinyl", 0, true, "Paper", -20, false);
    }

    /** Mug and t-shirt products. */
    private void seedMerchandise() {
        long mug = product(6, "Classic 11oz Mug", "White ceramic mug, dishwasher safe, printed all around.", 1200.00,
                "mug", 1, 500, 1, 3, "img_product_mug_classic");
        options(mug, MATERIAL, "Ceramic", 0, true);
        options(mug, COLOUR, "White", 0, true, "White with black inner", 15, false);

        long magic = product(6, "Magic Colour Mug", "Black mug that reveals your design when filled with a hot drink.",
                1950.00, "mug", 1, 200, 1, 4, "img_product_mug_magic");
        options(magic, MATERIAL, "Ceramic", 0, true);
        options(magic, COLOUR, "Black", 0, true);

        long crew = product(7, "Cotton Crew T-Shirt", "Soft 180gsm cotton crew-neck tee with durable print.",
                1800.00, "shirt", 1, 1000, 1, 4, "img_product_tshirt_crew");
        options(crew, SIZE, "S", 0, false, "M", 0, true, "L", 0, false, "XL", 10, false);
        options(crew, MATERIAL, "Cotton", 0, true);
        options(crew, COLOUR, "White", 0, true, "Black", 10, false, "Navy", 10, false);
        options(crew, SIDES, "Front only", 0, true, "Front and back", 40, false);

        long polo = product(7, "Polo T-Shirt", "Smart pique polo shirts for teams and uniforms.", 2600.00, "shirt",
                1, 500, 1, 5, "img_product_tshirt_polo");
        options(polo, SIZE, "S", 0, false, "M", 0, true, "L", 0, false, "XL", 10, false);
        options(polo, MATERIAL, "Cotton", 0, true);
        options(polo, COLOUR, "White", 0, true, "Black", 10, false);
    }

    /**
     * Inserts a product.
     *
     * @param categoryId category id
     * @param name       product name
     * @param desc       description
     * @param price      unit price
     * @param unit       unit noun
     * @param min        minimum quantity
     * @param max        maximum quantity
     * @param step       quantity step
     * @param days       turnaround days
     * @param image      image drawable name
     * @return the new product id
     */
    private long product(long categoryId, String name, String desc, double price, String unit, int min, int max,
                         int step, int days, String image) {
        ContentValues v = new ContentValues();
        v.put(Products.CATEGORY_ID, categoryId);
        v.put(Products.NAME, name);
        v.put(Products.DESCRIPTION, desc);
        v.put(Products.BASE_PRICE, price);
        v.put(Products.PRICE_UNIT, unit);
        v.put(Products.MIN_QTY, min);
        v.put(Products.MAX_QTY, max);
        v.put(Products.QTY_STEP, step);
        v.put(Products.TURNAROUND_DAYS, days);
        v.put(Products.IMAGE_RES, image);
        return db.insertOrThrow(Products.TABLE, null, v);
    }

    /**
     * Inserts option values for one type as repeating triples of value, modifier and default flag.
     *
     * @param productId the product
     * @param typeId    the option type
     * @param triples   value (String), modifier (Number), default (Boolean), repeated
     */
    private void options(long productId, long typeId, Object... triples) {
        for (int i = 0; i + 2 < triples.length; i += 3) {
            ContentValues v = new ContentValues();
            v.put(ProductOptions.PRODUCT_ID, productId);
            v.put(ProductOptions.OPTION_TYPE_ID, typeId);
            v.put(ProductOptions.VALUE, (String) triples[i]);
            v.put(ProductOptions.PRICE_MODIFIER, ((Number) triples[i + 1]).doubleValue());
            v.put(ProductOptions.IS_DEFAULT, Boolean.TRUE.equals(triples[i + 2]) ? 1 : 0);
            db.insertOrThrow(ProductOptions.TABLE, null, v);
        }
    }

    /** Seasonal and standing promotions. */
    private void seedPromotions() {
        promo(null, "Avurudu Offer", "Celebrate the Sinhala and Tamil New Year with 15% off every order.",
                "AVURUDU26", "PERCENT", 15, 0, "2026-04-01", "2026-04-30", "img_promo_avurudu");
        promo(3L, "Vesak Lantern and Poster Deal", "Rs. 500 off Vesak posters on orders above Rs. 2,000.",
                "VESAK26", "FIXED", 500, 2000, "2026-05-01", "2026-05-31", "img_promo_vesak");
        promo(null, "Bulk Order Saver", "Spend Rs. 20,000 or more and save 10% on your whole order.",
                "BULK500", "PERCENT", 10, 20000, "2026-01-01", "2026-12-31", "img_promo_bulk");
        promo(null, "Deepavali Festival Offer", "Light up the festival with 12% off orders above Rs. 3,000.",
                "DEEPAVALI26", "PERCENT", 12, 3000, "2026-10-01", "2026-11-15", "img_promo_deepavali");
        promo(1L, "Christmas Cards Special", "20% off business and greeting cards for the festive season.",
                "XMAS26", "PERCENT", 20, 0, "2026-12-01", "2026-12-31", "img_promo_christmas");
        promo(null, "Welcome Discount", "New to PrintXpress? Enjoy 10% off your first order.",
                "WELCOME10", "PERCENT", 10, 0, "2026-01-01", "2027-12-31", "img_promo_welcome");
    }

    /**
     * Inserts a promotion.
     *
     * @param categoryId targeted category or null
     * @param title      title
     * @param desc       description
     * @param code       promo code
     * @param type       PERCENT or FIXED
     * @param value      discount value
     * @param minOrder   minimum order value
     * @param start      start date
     * @param end        end date
     * @param image      banner drawable name
     */
    private void promo(Long categoryId, String title, String desc, String code, String type, double value,
                       double minOrder, String start, String end, String image) {
        ContentValues v = new ContentValues();
        if (categoryId != null) {
            v.put(Promotions.CATEGORY_ID, categoryId);
        }
        v.put(Promotions.TITLE, title);
        v.put(Promotions.DESCRIPTION, desc);
        v.put(Promotions.PROMO_CODE, code);
        v.put(Promotions.DISCOUNT_TYPE, type);
        v.put(Promotions.DISCOUNT_VALUE, value);
        v.put(Promotions.MIN_ORDER_VALUE, minOrder);
        v.put(Promotions.START_DATE, start);
        v.put(Promotions.END_DATE, end);
        v.put(Promotions.IMAGE_RES, image);
        db.insertOrThrow(Promotions.TABLE, null, v);
    }

    /** Twenty gallery samples across categories; five are seasonal. */
    private void seedGallery() {
        Object[][] items = {
                {1L, "Minimal Architect Card", "Clean white card with a single accent line", false},
                {1L, "Cafe Loyalty Card", "Stamp card for a Colombo coffee shop", false},
                {1L, "Gold Foil Salon Card", "Premium matte card with gold foil logo", false},
                {1L, "Wedding Invitation", "Floral invitation card with spot UV", true},
                {2L, "Restaurant Menu Flyer", "Double-sided A4 takeaway menu", false},
                {2L, "Tuition Class Flyer", "A5 flyer for an O/L maths class", false},
                {2L, "Avurudu Sale Flyer", "Festive flyer with erabadu flowers", true},
                {3L, "Music Night Poster", "Bold A3 concert poster", false},
                {3L, "Vesak Dansal Poster", "A2 poster for a community dansal", true},
                {3L, "Gym Opening Poster", "High-contrast A3 fitness poster", false},
                {4L, "Shop Front Flex", "3 x 6 ft flex banner for a hardware store", false},
                {4L, "Expo Roll-up", "Roll-up standee for a tech exhibition", false},
                {5L, "Laptop Sticker Pack", "Die-cut vinyl stickers in pastel colours", false},
                {5L, "Honey Jar Labels", "Roll labels for a home-made honey brand", false},
                {5L, "Christmas Gift Tags", "Round gift stickers with snowflakes", true},
                {6L, "Couple Photo Mug", "Classic mug with a wrap-around photo", false},
                {6L, "Office Team Mug", "Magic mug with a team logo", false},
                {7L, "Sports Day T-Shirt", "House colours for a school sports meet", false},
                {7L, "Startup Team Polo", "Embroidered-look print on a navy polo", false},
                {7L, "Deepavali Family Tee", "Matching family tees with a diya motif", true},
        };
        for (int i = 0; i < items.length; i++) {
            ContentValues v = new ContentValues();
            v.put(GalleryItems.CATEGORY_ID, (Long) items[i][0]);
            v.put(GalleryItems.TITLE, (String) items[i][1]);
            v.put(GalleryItems.DESCRIPTION, (String) items[i][2]);
            v.put(GalleryItems.IMAGE_RES, String.format(java.util.Locale.US, "img_gallery_%02d", i + 1));
            v.put(GalleryItems.IS_SEASONAL, Boolean.TRUE.equals(items[i][3]) ? 1 : 0);
            db.insertOrThrow(GalleryItems.TABLE, null, v);
        }
    }

    /** Five print guidelines (FR-5.1). */
    private void seedGuidelines() {
        guideline("FILE_FORMAT", "Accepted file formats", "Send print-ready PDF files (PDF/X-1a preferred) for the best "
                + "results. PNG and JPG are accepted for photos and simple designs. Keep each file under 25 MB. "
                + "Word, PowerPoint and Photoshop (PSD) files are not accepted; export them to PDF first.",
                "ill_guide_file_formats", 1);
        guideline("RESOLUTION", "Resolution: 300 DPI", "Images should be 300 DPI at the final print size. A 72 DPI "
                + "web image looks sharp on screen but prints blurred and pixelated. For an A4 flyer that means "
                + "about 2480 x 3508 pixels. Never upscale a small image; use the original high-resolution file.",
                "ill_guide_resolution", 2);
        guideline("COLOUR", "CMYK vs RGB colour", "Screens mix light in RGB, but printers mix ink in CMYK (cyan, "
                + "magenta, yellow, black). Very bright RGB blues and greens cannot be reproduced exactly in ink and "
                + "will look slightly duller. Design in CMYK where possible and expect small colour differences.",
                "ill_guide_cmyk_rgb", 3);
        guideline("BLEED", "Bleed and safe zone", "Extend backgrounds 3 mm beyond the trim line on every side (the "
                + "bleed) so no white edge appears after cutting. Keep text and logos at least 5 mm inside the trim "
                + "line (the safe zone). A standard business card with bleed is 95 x 57 mm.",
                "ill_guide_bleed", 4);
        guideline("FONTS", "Fonts and outlines", "Embed all fonts when exporting to PDF, or convert text to outlines. "
                + "Missing fonts are replaced by the printer and can change your layout. Keep body text at 7 pt or "
                + "larger and thin lines at least 0.25 pt.", "ill_guide_fonts", 5);
    }

    /**
     * Inserts a guideline.
     *
     * @param topic   topic enum name
     * @param title   title
     * @param content content
     * @param image   illustration drawable name
     * @param order   display order
     */
    private void guideline(String topic, String title, String content, String image, int order) {
        ContentValues v = new ContentValues();
        v.put(Guidelines.TOPIC, topic);
        v.put(Guidelines.TITLE, title);
        v.put(Guidelines.CONTENT, content);
        v.put(Guidelines.IMAGE_RES, image);
        v.put(Guidelines.SORT_ORDER, order);
        db.insertOrThrow(Guidelines.TABLE, null, v);
    }

    /** Twelve frequently asked questions. */
    private void seedFaqs() {
        String[][] faqs = {
                {"Orders", "How long does printing take?", "Each product shows its turnaround time, usually 2 to 5 "
                        + "working days. The earliest pickup or delivery date is calculated for you at checkout."},
                {"Orders", "Can I cancel my order?", "Yes. You can cancel from the order details screen while the "
                        + "order is Pending, Confirmed or Processing. Once printing has started it can no longer be "
                        + "cancelled."},
                {"Orders", "Can I change my pickup or delivery date?", "Yes, you can reschedule before printing "
                        + "starts. Open the order and tap Reschedule."},
                {"Orders", "Can I reorder a past order?", "Open a completed order and tap Reorder to add the same "
                        + "items to your cart."},
                {"Delivery", "Which areas do you deliver to?", "We deliver to all 25 districts in Sri Lanka. The "
                        + "delivery fee depends on the district: Colombo Rs. 300, Gampaha and Kalutara Rs. 400, and "
                        + "Rs. 600 elsewhere."},
                {"Delivery", "Do you deliver on Sundays?", "No. Pickups and deliveries run Monday to Saturday."},
                {"Files", "Which file formats do you accept?", "PDF, PNG and JPG files up to 25 MB. See Print "
                        + "guidelines for tips on preparing your file."},
                {"Files", "Why do printed colours look different from my screen?", "Screens use RGB light and "
                        + "printers use CMYK ink, so very bright colours print slightly duller. Read the CMYK vs RGB "
                        + "guideline for details."},
                {"Files", "I do not have a design. Can I just send text?", "Yes. Choose Custom text on the design "
                        + "step and type what you want printed. Our team will lay it out for you."},
                {"Payment", "How do I pay?", "Pay at the branch when you collect your order, or pay cash on "
                        + "delivery. Online payment is not available yet."},
                {"Payment", "How do promo codes work?", "Enter one promo code at checkout. The code must be active "
                        + "and your order must meet its minimum value."},
                {"Account", "How do I delete my account?", "Go to Profile, Settings, Delete account. Your account "
                        + "and all saved data on this device are removed permanently."},
        };
        for (int i = 0; i < faqs.length; i++) {
            ContentValues v = new ContentValues();
            v.put(Faqs.CATEGORY, faqs[i][0]);
            v.put(Faqs.QUESTION, faqs[i][1]);
            v.put(Faqs.ANSWER, faqs[i][2]);
            v.put(Faqs.SORT_ORDER, i + 1);
            db.insertOrThrow(Faqs.TABLE, null, v);
        }
    }
}
