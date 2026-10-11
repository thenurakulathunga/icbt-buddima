package com.printxpress.app.core.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Application-wide business constants that are not user-visible text (text lives in strings.xml).
 *
 * @since 1.0
 */
public final class Constants {

    /** Largest accepted upload size in bytes (BR-10: 25 MB). */
    public static final long MAX_UPLOAD_BYTES = 25L * 1024L * 1024L;
    /** MIME type for PDF artwork. */
    public static final String MIME_PDF = "application/pdf";
    /** MIME type for PNG artwork. */
    public static final String MIME_PNG = "image/png";
    /** MIME type for JPEG artwork. */
    public static final String MIME_JPEG = "image/jpeg";
    /** Accepted upload MIME types (BR-10). */
    public static final List<String> ACCEPTED_MIME_TYPES =
            Collections.unmodifiableList(Arrays.asList(MIME_PDF, MIME_PNG, MIME_JPEG));

    /** Maximum characters of custom design text (docs/01 §8). */
    public static final int MAX_CUSTOM_TEXT = 250;
    /** Maximum characters of print instructions. */
    public static final int MAX_INSTRUCTIONS = 200;
    /** Maximum characters of an optional cancellation note. */
    public static final int MAX_CANCEL_NOTE = 200;
    /** Number of selectable days shown by the date picker. */
    public static final int SCHEDULE_WINDOW_DAYS = 14;
    /** Extra lead days added for home delivery on top of turnaround. */
    public static final int DELIVERY_EXTRA_DAYS = 1;

    /** Quantity at which the first bulk discount applies (BR-4). */
    public static final int BULK_TIER_1_QTY = 500;
    /** Discount percentage of the first bulk tier. */
    public static final double BULK_TIER_1_PERCENT = 10;
    /** Quantity at which the second bulk discount applies (BR-4). */
    public static final int BULK_TIER_2_QTY = 1000;
    /** Discount percentage of the second bulk tier. */
    public static final double BULK_TIER_2_PERCENT = 15;

    /** Sri Lankan mobile number pattern (local or +94 form). */
    public static final String REGEX_SL_MOBILE = "^(?:\\+94|0)7[0-9]{8}$";
    /** Five-digit Sri Lankan postal code. */
    public static final String REGEX_POSTAL_CODE = "^\\d{5}$";
    /** Person name: letters, spaces, dots and apostrophes. */
    public static final String REGEX_NAME = "^[\\p{L} .']+$";
    /** City name: letters, spaces, dots and hyphens. */
    public static final String REGEX_CITY = "^[\\p{L} .\\-]+$";
    /** Promo code: 4 to 12 upper-case letters or digits. */
    public static final String REGEX_PROMO_CODE = "^[A-Z0-9]{4,12}$";

    /** Order number prefix (BR-11). */
    public static final String ORDER_NUMBER_PREFIX = "PX";
    /** Support phone number used by the dialer intent. */
    public static final String SUPPORT_PHONE = "+94112345678";
    /** Support email address. */
    public static final String SUPPORT_EMAIL = "support@printxpress.lk";
    /** WhatsApp number in international form without the plus sign. */
    public static final String SUPPORT_WHATSAPP = "94771234567";

    /** Folder under app-private files for uploaded artwork. */
    public static final String DESIGNS_DIR = "designs";
    /** Folder under app-private files for profile photos. */
    public static final String AVATARS_DIR = "avatars";
    /** Folder under cache used for shareable exports. */
    public static final String SHARE_DIR = "shared";

    /** Navigation argument key for an order id. */
    public static final String ARG_ORDER_ID = "orderId";
    /** Navigation argument key for a product id. */
    public static final String ARG_PRODUCT_ID = "productId";
    /** Navigation argument key for a category id. */
    public static final String ARG_CATEGORY_ID = "categoryId";
    /** Navigation argument key for a promotion id. */
    public static final String ARG_PROMO_ID = "promoId";
    /** Navigation argument key for a cart item id being edited. */
    public static final String ARG_CART_ITEM_ID = "cartItemId";
    /** Navigation argument key for an address id. */
    public static final String ARG_ADDRESS_ID = "addressId";
    /** Navigation argument key for a guideline id. */
    public static final String ARG_GUIDELINE_ID = "guidelineId";
    /** Navigation argument key for a gallery position. */
    public static final String ARG_POSITION = "position";
    /** Navigation argument key for a screen title. */
    public static final String ARG_TITLE = "title";
    /** Navigation argument key for a search query. */
    public static final String ARG_QUERY = "query";
    /** Intent extra that asks the auth flow to return to the caller after login. */
    public static final String EXTRA_RETURN_AFTER_LOGIN = "returnAfterLogin";

    /** Hidden constructor: constants holder. */
    private Constants() {
    }
}
