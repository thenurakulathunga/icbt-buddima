package com.printxpress.app.data.db;

/**
 * Single source of truth for every table name, column name and DDL statement (docs/03 §4).
 * <p>
 * DAOs only reference these constants, so a rename happens in one place and SQL never contains
 * user input: values are always bound through {@code ?} placeholders.
 * </p>
 *
 * @since 1.0
 */
public final class DbContract {

    /** Database file name. */
    public static final String DATABASE_NAME = "printxpress.db";
    /** Schema version; bump and add a step in {@code DatabaseHelper.onUpgrade} for changes. */
    public static final int DATABASE_VERSION = 1;
    /** SQLite default timestamp expression in local time. */
    private static final String NOW = "(datetime('now','localtime'))";

    /** Hidden constructor: constants holder. */
    private DbContract() {
    }

    /** Table {@code users}. */
    public static final class Users {
        /** Table name. */
        public static final String TABLE = "users";
        /** Primary key. */
        public static final String ID = "user_id";
        /** Full name. */
        public static final String FULL_NAME = "full_name";
        /** Unique email, case-insensitive. */
        public static final String EMAIL = "email";
        /** Unique canonical mobile number. */
        public static final String PHONE = "phone";
        /** PBKDF2 hash. */
        public static final String PASSWORD_HASH = "password_hash";
        /** Per-user salt. */
        public static final String PASSWORD_SALT = "password_salt";
        /** Profile photo path. */
        public static final String PROFILE_IMAGE = "profile_image_path";
        /** Order notification preference. */
        public static final String NOTIFY_ORDERS = "notify_orders";
        /** Promotion notification preference. */
        public static final String NOTIFY_PROMOS = "notify_promos";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** Last update time. */
        public static final String UPDATED_AT = "updated_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FULL_NAME + " TEXT NOT NULL, "
                + EMAIL + " TEXT UNIQUE COLLATE NOCASE, "
                + PHONE + " TEXT UNIQUE, "
                + PASSWORD_HASH + " TEXT NOT NULL, "
                + PASSWORD_SALT + " TEXT NOT NULL, "
                + PROFILE_IMAGE + " TEXT, "
                + NOTIFY_ORDERS + " INTEGER NOT NULL DEFAULT 1, "
                + NOTIFY_PROMOS + " INTEGER NOT NULL DEFAULT 1, "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ", "
                + UPDATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ", "
                + "CHECK (" + EMAIL + " IS NOT NULL OR " + PHONE + " IS NOT NULL))";

        /** Hidden constructor. */
        private Users() {
        }
    }

    /** Table {@code districts}. */
    public static final class Districts {
        /** Table name. */
        public static final String TABLE = "districts";
        /** Primary key. */
        public static final String ID = "district_id";
        /** Unique name. */
        public static final String NAME = "name";
        /** Delivery fee. */
        public static final String DELIVERY_FEE = "delivery_fee";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL UNIQUE, "
                + DELIVERY_FEE + " REAL NOT NULL CHECK (" + DELIVERY_FEE + " >= 0))";

        /** Hidden constructor. */
        private Districts() {
        }
    }

    /** Table {@code addresses}. */
    public static final class Addresses {
        /** Table name. */
        public static final String TABLE = "addresses";
        /** Primary key. */
        public static final String ID = "address_id";
        /** Owner. */
        public static final String USER_ID = "user_id";
        /** District. */
        public static final String DISTRICT_ID = "district_id";
        /** Label. */
        public static final String LABEL = "label";
        /** Recipient name. */
        public static final String RECIPIENT_NAME = "recipient_name";
        /** Recipient phone. */
        public static final String RECIPIENT_PHONE = "recipient_phone";
        /** Line 1. */
        public static final String LINE1 = "line1";
        /** Line 2. */
        public static final String LINE2 = "line2";
        /** City. */
        public static final String CITY = "city";
        /** Postal code. */
        public static final String POSTAL_CODE = "postal_code";
        /** Default flag. */
        public static final String IS_DEFAULT = "is_default";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + DISTRICT_ID + " INTEGER NOT NULL REFERENCES " + Districts.TABLE + "(" + Districts.ID + "), "
                + LABEL + " TEXT NOT NULL, "
                + RECIPIENT_NAME + " TEXT NOT NULL, "
                + RECIPIENT_PHONE + " TEXT NOT NULL, "
                + LINE1 + " TEXT NOT NULL, "
                + LINE2 + " TEXT, "
                + CITY + " TEXT NOT NULL, "
                + POSTAL_CODE + " TEXT NOT NULL CHECK (length(" + POSTAL_CODE + ") = 5), "
                + IS_DEFAULT + " INTEGER NOT NULL DEFAULT 0 CHECK (" + IS_DEFAULT + " IN (0,1)))";

        /** Hidden constructor. */
        private Addresses() {
        }
    }

    /** Table {@code branches}. */
    public static final class Branches {
        /** Table name. */
        public static final String TABLE = "branches";
        /** Primary key. */
        public static final String ID = "branch_id";
        /** Name. */
        public static final String NAME = "name";
        /** Street address. */
        public static final String ADDRESS = "address";
        /** Phone. */
        public static final String PHONE = "phone";
        /** Opening hours. */
        public static final String OPENING_HOURS = "opening_hours";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + ADDRESS + " TEXT NOT NULL, "
                + PHONE + " TEXT NOT NULL, "
                + OPENING_HOURS + " TEXT NOT NULL)";

        /** Hidden constructor. */
        private Branches() {
        }
    }

    /** Table {@code categories}. */
    public static final class Categories {
        /** Table name. */
        public static final String TABLE = "categories";
        /** Primary key. */
        public static final String ID = "category_id";
        /** Unique name. */
        public static final String NAME = "name";
        /** Description. */
        public static final String DESCRIPTION = "description";
        /** Icon drawable name. */
        public static final String ICON_RES = "icon_res";
        /** Image drawable name. */
        public static final String IMAGE_RES = "image_res";
        /** Display order. */
        public static final String SORT_ORDER = "sort_order";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL UNIQUE, "
                + DESCRIPTION + " TEXT, "
                + ICON_RES + " TEXT NOT NULL, "
                + IMAGE_RES + " TEXT NOT NULL, "
                + SORT_ORDER + " INTEGER NOT NULL DEFAULT 0)";

        /** Hidden constructor. */
        private Categories() {
        }
    }

    /** Table {@code products}. */
    public static final class Products {
        /** Table name. */
        public static final String TABLE = "products";
        /** Primary key. */
        public static final String ID = "product_id";
        /** Category. */
        public static final String CATEGORY_ID = "category_id";
        /** Name. */
        public static final String NAME = "name";
        /** Description. */
        public static final String DESCRIPTION = "description";
        /** Unit price. */
        public static final String BASE_PRICE = "base_price";
        /** Unit noun. */
        public static final String PRICE_UNIT = "price_unit";
        /** Minimum quantity. */
        public static final String MIN_QTY = "min_qty";
        /** Maximum quantity. */
        public static final String MAX_QTY = "max_qty";
        /** Quantity step. */
        public static final String QTY_STEP = "qty_step";
        /** Turnaround days. */
        public static final String TURNAROUND_DAYS = "turnaround_days";
        /** Image drawable name. */
        public static final String IMAGE_RES = "image_res";
        /** Active flag. */
        public static final String IS_ACTIVE = "is_active";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_ID + " INTEGER NOT NULL REFERENCES " + Categories.TABLE + "(" + Categories.ID + "), "
                + NAME + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT NOT NULL, "
                + BASE_PRICE + " REAL NOT NULL CHECK (" + BASE_PRICE + " > 0), "
                + PRICE_UNIT + " TEXT NOT NULL, "
                + MIN_QTY + " INTEGER NOT NULL CHECK (" + MIN_QTY + " > 0), "
                + MAX_QTY + " INTEGER NOT NULL, "
                + QTY_STEP + " INTEGER NOT NULL DEFAULT 1, "
                + TURNAROUND_DAYS + " INTEGER NOT NULL DEFAULT 2, "
                + IMAGE_RES + " TEXT NOT NULL, "
                + IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
                + "CHECK (" + MAX_QTY + " >= " + MIN_QTY + "))";

        /** Hidden constructor. */
        private Products() {
        }
    }

    /** Table {@code option_types}. */
    public static final class OptionTypes {
        /** Table name. */
        public static final String TABLE = "option_types";
        /** Primary key. */
        public static final String ID = "option_type_id";
        /** Unique name. */
        public static final String NAME = "name";
        /** Display order. */
        public static final String SORT_ORDER = "sort_order";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL UNIQUE, "
                + SORT_ORDER + " INTEGER NOT NULL DEFAULT 0)";

        /** Hidden constructor. */
        private OptionTypes() {
        }
    }

    /** Table {@code product_options}. */
    public static final class ProductOptions {
        /** Table name. */
        public static final String TABLE = "product_options";
        /** Primary key. */
        public static final String ID = "option_id";
        /** Product. */
        public static final String PRODUCT_ID = "product_id";
        /** Option type. */
        public static final String OPTION_TYPE_ID = "option_type_id";
        /** Value. */
        public static final String VALUE = "value";
        /** Price modifier percentage. */
        public static final String PRICE_MODIFIER = "price_modifier";
        /** Default flag. */
        public static final String IS_DEFAULT = "is_default";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PRODUCT_ID + " INTEGER NOT NULL REFERENCES " + Products.TABLE + "(" + Products.ID + ") ON DELETE CASCADE, "
                + OPTION_TYPE_ID + " INTEGER NOT NULL REFERENCES " + OptionTypes.TABLE + "(" + OptionTypes.ID + "), "
                + VALUE + " TEXT NOT NULL, "
                + PRICE_MODIFIER + " REAL NOT NULL DEFAULT 0, "
                + IS_DEFAULT + " INTEGER NOT NULL DEFAULT 0, "
                + "UNIQUE (" + PRODUCT_ID + ", " + OPTION_TYPE_ID + ", " + VALUE + "))";

        /** Hidden constructor. */
        private ProductOptions() {
        }
    }

    /** Table {@code designs}. */
    public static final class Designs {
        /** Table name. */
        public static final String TABLE = "designs";
        /** Primary key. */
        public static final String ID = "design_id";
        /** Owner. */
        public static final String USER_ID = "user_id";
        /** Title. */
        public static final String TITLE = "title";
        /** File path. */
        public static final String FILE_PATH = "file_path";
        /** MIME type. */
        public static final String MIME_TYPE = "mime_type";
        /** File size. */
        public static final String FILE_SIZE = "file_size_bytes";
        /** Custom text. */
        public static final String CUSTOM_TEXT = "custom_text";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + TITLE + " TEXT NOT NULL, "
                + FILE_PATH + " TEXT, "
                + MIME_TYPE + " TEXT, "
                + FILE_SIZE + " INTEGER, "
                + CUSTOM_TEXT + " TEXT, "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ", "
                + "CHECK (" + FILE_PATH + " IS NOT NULL OR " + CUSTOM_TEXT + " IS NOT NULL))";

        /** Hidden constructor. */
        private Designs() {
        }
    }

    /** Table {@code cart_items}. */
    public static final class CartItems {
        /** Table name. */
        public static final String TABLE = "cart_items";
        /** Primary key. */
        public static final String ID = "cart_item_id";
        /** Owner. */
        public static final String USER_ID = "user_id";
        /** Product. */
        public static final String PRODUCT_ID = "product_id";
        /** Design. */
        public static final String DESIGN_ID = "design_id";
        /** Quantity. */
        public static final String QUANTITY = "quantity";
        /** Custom text. */
        public static final String CUSTOM_TEXT = "custom_text";
        /** Instructions. */
        public static final String INSTRUCTIONS = "instructions";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + PRODUCT_ID + " INTEGER NOT NULL REFERENCES " + Products.TABLE + "(" + Products.ID + "), "
                + DESIGN_ID + " INTEGER REFERENCES " + Designs.TABLE + "(" + Designs.ID + ") ON DELETE SET NULL, "
                + QUANTITY + " INTEGER NOT NULL CHECK (" + QUANTITY + " > 0), "
                + CUSTOM_TEXT + " TEXT, "
                + INSTRUCTIONS + " TEXT, "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ")";

        /** Hidden constructor. */
        private CartItems() {
        }
    }

    /** Table {@code cart_item_options}. */
    public static final class CartItemOptions {
        /** Table name. */
        public static final String TABLE = "cart_item_options";
        /** Cart item. */
        public static final String CART_ITEM_ID = "cart_item_id";
        /** Product option. */
        public static final String OPTION_ID = "option_id";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + CART_ITEM_ID + " INTEGER NOT NULL REFERENCES " + CartItems.TABLE + "(" + CartItems.ID + ") ON DELETE CASCADE, "
                + OPTION_ID + " INTEGER NOT NULL REFERENCES " + ProductOptions.TABLE + "(" + ProductOptions.ID + "), "
                + "PRIMARY KEY (" + CART_ITEM_ID + ", " + OPTION_ID + "))";

        /** Hidden constructor. */
        private CartItemOptions() {
        }
    }

    /** Table {@code promotions}. */
    public static final class Promotions {
        /** Table name. */
        public static final String TABLE = "promotions";
        /** Primary key. */
        public static final String ID = "promo_id";
        /** Targeted category. */
        public static final String CATEGORY_ID = "category_id";
        /** Title. */
        public static final String TITLE = "title";
        /** Description. */
        public static final String DESCRIPTION = "description";
        /** Unique code. */
        public static final String PROMO_CODE = "promo_code";
        /** PERCENT or FIXED. */
        public static final String DISCOUNT_TYPE = "discount_type";
        /** Discount value. */
        public static final String DISCOUNT_VALUE = "discount_value";
        /** Minimum order value. */
        public static final String MIN_ORDER_VALUE = "min_order_value";
        /** Start date. */
        public static final String START_DATE = "start_date";
        /** End date. */
        public static final String END_DATE = "end_date";
        /** Banner drawable name. */
        public static final String IMAGE_RES = "image_res";
        /** Active flag. */
        public static final String IS_ACTIVE = "is_active";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_ID + " INTEGER REFERENCES " + Categories.TABLE + "(" + Categories.ID + "), "
                + TITLE + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT NOT NULL, "
                + PROMO_CODE + " TEXT NOT NULL UNIQUE, "
                + DISCOUNT_TYPE + " TEXT NOT NULL CHECK (" + DISCOUNT_TYPE + " IN ('PERCENT','FIXED')), "
                + DISCOUNT_VALUE + " REAL NOT NULL CHECK (" + DISCOUNT_VALUE + " > 0), "
                + MIN_ORDER_VALUE + " REAL NOT NULL DEFAULT 0, "
                + START_DATE + " TEXT NOT NULL, "
                + END_DATE + " TEXT NOT NULL, "
                + IMAGE_RES + " TEXT NOT NULL, "
                + IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
                + "CHECK (" + END_DATE + " >= " + START_DATE + "))";

        /** Hidden constructor. */
        private Promotions() {
        }
    }

    /** Table {@code orders}. */
    public static final class Orders {
        /** Table name. */
        public static final String TABLE = "orders";
        /** Primary key. */
        public static final String ID = "order_id";
        /** Unique order number. */
        public static final String ORDER_NUMBER = "order_number";
        /** Owner. */
        public static final String USER_ID = "user_id";
        /** Delivery address. */
        public static final String ADDRESS_ID = "address_id";
        /** Pickup branch. */
        public static final String BRANCH_ID = "branch_id";
        /** Applied promotion. */
        public static final String PROMO_ID = "promo_id";
        /** Status. */
        public static final String STATUS = "status";
        /** Fulfilment type. */
        public static final String FULFILMENT_TYPE = "fulfilment_type";
        /** Payment method. */
        public static final String PAYMENT_METHOD = "payment_method";
        /** Scheduled date. */
        public static final String SCHEDULED_DATE = "scheduled_date";
        /** Scheduled slot. */
        public static final String SCHEDULED_SLOT = "scheduled_slot";
        /** Subtotal. */
        public static final String SUBTOTAL = "subtotal";
        /** Promo discount. */
        public static final String DISCOUNT = "discount";
        /** Delivery fee. */
        public static final String DELIVERY_FEE = "delivery_fee";
        /** Total. */
        public static final String TOTAL = "total";
        /** Notes. */
        public static final String NOTES = "notes";
        /** Cancellation reason. */
        public static final String CANCEL_REASON = "cancel_reason";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** Last update time. */
        public static final String UPDATED_AT = "updated_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORDER_NUMBER + " TEXT NOT NULL UNIQUE, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + ADDRESS_ID + " INTEGER REFERENCES " + Addresses.TABLE + "(" + Addresses.ID + ") ON DELETE SET NULL, "
                + BRANCH_ID + " INTEGER REFERENCES " + Branches.TABLE + "(" + Branches.ID + "), "
                + PROMO_ID + " INTEGER REFERENCES " + Promotions.TABLE + "(" + Promotions.ID + "), "
                + STATUS + " TEXT NOT NULL DEFAULT 'PENDING' CHECK (" + STATUS + " IN "
                + "('PENDING','CONFIRMED','PROCESSING','PRINTING','READY_FOR_PICKUP',"
                + "'OUT_FOR_DELIVERY','COMPLETED','CANCELLED')), "
                + FULFILMENT_TYPE + " TEXT NOT NULL CHECK (" + FULFILMENT_TYPE + " IN ('PICKUP','DELIVERY')), "
                + PAYMENT_METHOD + " TEXT NOT NULL CHECK (" + PAYMENT_METHOD + " IN ('PAY_AT_PICKUP','CASH_ON_DELIVERY')), "
                + SCHEDULED_DATE + " TEXT NOT NULL, "
                + SCHEDULED_SLOT + " TEXT NOT NULL, "
                + SUBTOTAL + " REAL NOT NULL, "
                + DISCOUNT + " REAL NOT NULL DEFAULT 0, "
                + DELIVERY_FEE + " REAL NOT NULL DEFAULT 0, "
                + TOTAL + " REAL NOT NULL, "
                + NOTES + " TEXT, "
                + CANCEL_REASON + " TEXT, "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ", "
                + UPDATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ", "
                + "CHECK ((" + FULFILMENT_TYPE + " = 'PICKUP' AND " + BRANCH_ID + " IS NOT NULL) OR "
                + "(" + FULFILMENT_TYPE + " = 'DELIVERY' AND " + ADDRESS_ID + " IS NOT NULL)))";

        /** Hidden constructor. */
        private Orders() {
        }
    }

    /** Table {@code order_items}. */
    public static final class OrderItems {
        /** Table name. */
        public static final String TABLE = "order_items";
        /** Primary key. */
        public static final String ID = "order_item_id";
        /** Order. */
        public static final String ORDER_ID = "order_id";
        /** Product. */
        public static final String PRODUCT_ID = "product_id";
        /** Design. */
        public static final String DESIGN_ID = "design_id";
        /** Quantity. */
        public static final String QUANTITY = "quantity";
        /** Unit price snapshot. */
        public static final String UNIT_PRICE = "unit_price";
        /** Line total snapshot. */
        public static final String LINE_TOTAL = "line_total";
        /** Custom text. */
        public static final String CUSTOM_TEXT = "custom_text";
        /** Instructions. */
        public static final String INSTRUCTIONS = "instructions";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORDER_ID + " INTEGER NOT NULL REFERENCES " + Orders.TABLE + "(" + Orders.ID + ") ON DELETE CASCADE, "
                + PRODUCT_ID + " INTEGER NOT NULL REFERENCES " + Products.TABLE + "(" + Products.ID + "), "
                + DESIGN_ID + " INTEGER REFERENCES " + Designs.TABLE + "(" + Designs.ID + ") ON DELETE SET NULL, "
                + QUANTITY + " INTEGER NOT NULL CHECK (" + QUANTITY + " > 0), "
                + UNIT_PRICE + " REAL NOT NULL, "
                + LINE_TOTAL + " REAL NOT NULL, "
                + CUSTOM_TEXT + " TEXT, "
                + INSTRUCTIONS + " TEXT)";

        /** Hidden constructor. */
        private OrderItems() {
        }
    }

    /** Table {@code order_item_options}. */
    public static final class OrderItemOptions {
        /** Table name. */
        public static final String TABLE = "order_item_options";
        /** Order line. */
        public static final String ORDER_ITEM_ID = "order_item_id";
        /** Product option. */
        public static final String OPTION_ID = "option_id";
        /** Price modifier snapshot. */
        public static final String PRICE_MODIFIER_SNAPSHOT = "price_modifier_snapshot";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ORDER_ITEM_ID + " INTEGER NOT NULL REFERENCES " + OrderItems.TABLE + "(" + OrderItems.ID + ") ON DELETE CASCADE, "
                + OPTION_ID + " INTEGER NOT NULL REFERENCES " + ProductOptions.TABLE + "(" + ProductOptions.ID + "), "
                + PRICE_MODIFIER_SNAPSHOT + " REAL NOT NULL, "
                + "PRIMARY KEY (" + ORDER_ITEM_ID + ", " + OPTION_ID + "))";

        /** Hidden constructor. */
        private OrderItemOptions() {
        }
    }

    /** Table {@code order_status_history}. */
    public static final class OrderStatusHistory {
        /** Table name. */
        public static final String TABLE = "order_status_history";
        /** Primary key. */
        public static final String ID = "history_id";
        /** Order. */
        public static final String ORDER_ID = "order_id";
        /** Status entered. */
        public static final String STATUS = "status";
        /** Remark. */
        public static final String REMARK = "remark";
        /** Time of change. */
        public static final String CHANGED_AT = "changed_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORDER_ID + " INTEGER NOT NULL REFERENCES " + Orders.TABLE + "(" + Orders.ID + ") ON DELETE CASCADE, "
                + STATUS + " TEXT NOT NULL, "
                + REMARK + " TEXT, "
                + CHANGED_AT + " TEXT NOT NULL DEFAULT " + NOW + ")";

        /** Hidden constructor. */
        private OrderStatusHistory() {
        }
    }

    /** Table {@code notifications}. */
    public static final class Notifications {
        /** Table name. */
        public static final String TABLE = "notifications";
        /** Primary key. */
        public static final String ID = "notification_id";
        /** Recipient. */
        public static final String USER_ID = "user_id";
        /** Related order. */
        public static final String ORDER_ID = "order_id";
        /** Related promotion. */
        public static final String PROMO_ID = "promo_id";
        /** Type. */
        public static final String TYPE = "type";
        /** Title. */
        public static final String TITLE = "title";
        /** Message. */
        public static final String MESSAGE = "message";
        /** Read flag. */
        public static final String IS_READ = "is_read";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + ORDER_ID + " INTEGER REFERENCES " + Orders.TABLE + "(" + Orders.ID + ") ON DELETE CASCADE, "
                + PROMO_ID + " INTEGER REFERENCES " + Promotions.TABLE + "(" + Promotions.ID + "), "
                + TYPE + " TEXT NOT NULL CHECK (" + TYPE + " IN ('ORDER','PROMO','SYSTEM')), "
                + TITLE + " TEXT NOT NULL, "
                + MESSAGE + " TEXT NOT NULL, "
                + IS_READ + " INTEGER NOT NULL DEFAULT 0, "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ")";

        /** Hidden constructor. */
        private Notifications() {
        }
    }

    /** Table {@code gallery_items}. */
    public static final class GalleryItems {
        /** Table name. */
        public static final String TABLE = "gallery_items";
        /** Primary key. */
        public static final String ID = "gallery_id";
        /** Category. */
        public static final String CATEGORY_ID = "category_id";
        /** Title. */
        public static final String TITLE = "title";
        /** Description. */
        public static final String DESCRIPTION = "description";
        /** Image drawable name. */
        public static final String IMAGE_RES = "image_res";
        /** Seasonal flag. */
        public static final String IS_SEASONAL = "is_seasonal";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_ID + " INTEGER NOT NULL REFERENCES " + Categories.TABLE + "(" + Categories.ID + "), "
                + TITLE + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT, "
                + IMAGE_RES + " TEXT NOT NULL, "
                + IS_SEASONAL + " INTEGER NOT NULL DEFAULT 0)";

        /** Hidden constructor. */
        private GalleryItems() {
        }
    }

    /** Table {@code guidelines}. */
    public static final class Guidelines {
        /** Table name. */
        public static final String TABLE = "guidelines";
        /** Primary key. */
        public static final String ID = "guideline_id";
        /** Topic. */
        public static final String TOPIC = "topic";
        /** Title. */
        public static final String TITLE = "title";
        /** Content. */
        public static final String CONTENT = "content";
        /** Illustration drawable name. */
        public static final String IMAGE_RES = "image_res";
        /** Display order. */
        public static final String SORT_ORDER = "sort_order";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TOPIC + " TEXT NOT NULL CHECK (" + TOPIC + " IN ('FILE_FORMAT','RESOLUTION','COLOUR','BLEED','FONTS')), "
                + TITLE + " TEXT NOT NULL, "
                + CONTENT + " TEXT NOT NULL, "
                + IMAGE_RES + " TEXT, "
                + SORT_ORDER + " INTEGER NOT NULL DEFAULT 0)";

        /** Hidden constructor. */
        private Guidelines() {
        }
    }

    /** Table {@code faqs}. */
    public static final class Faqs {
        /** Table name. */
        public static final String TABLE = "faqs";
        /** Primary key. */
        public static final String ID = "faq_id";
        /** Category. */
        public static final String CATEGORY = "category";
        /** Question. */
        public static final String QUESTION = "question";
        /** Answer. */
        public static final String ANSWER = "answer";
        /** Display order. */
        public static final String SORT_ORDER = "sort_order";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY + " TEXT NOT NULL, "
                + QUESTION + " TEXT NOT NULL, "
                + ANSWER + " TEXT NOT NULL, "
                + SORT_ORDER + " INTEGER NOT NULL DEFAULT 0)";

        /** Hidden constructor. */
        private Faqs() {
        }
    }

    /** Table {@code support_tickets}. */
    public static final class SupportTickets {
        /** Table name. */
        public static final String TABLE = "support_tickets";
        /** Primary key. */
        public static final String ID = "ticket_id";
        /** Owner. */
        public static final String USER_ID = "user_id";
        /** Related order. */
        public static final String ORDER_ID = "order_id";
        /** Subject. */
        public static final String SUBJECT = "subject";
        /** Message. */
        public static final String MESSAGE = "message";
        /** Attachment path. */
        public static final String ATTACHMENT_PATH = "attachment_path";
        /** Status. */
        public static final String STATUS = "status";
        /** Creation time. */
        public static final String CREATED_AT = "created_at";
        /** DDL. */
        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER NOT NULL REFERENCES " + Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE, "
                + ORDER_ID + " INTEGER REFERENCES " + Orders.TABLE + "(" + Orders.ID + ") ON DELETE SET NULL, "
                + SUBJECT + " TEXT NOT NULL, "
                + MESSAGE + " TEXT NOT NULL, "
                + ATTACHMENT_PATH + " TEXT, "
                + STATUS + " TEXT NOT NULL DEFAULT 'OPEN' CHECK (" + STATUS + " IN ('OPEN','IN_PROGRESS','RESOLVED')), "
                + CREATED_AT + " TEXT NOT NULL DEFAULT " + NOW + ")";

        /** Hidden constructor. */
        private SupportTickets() {
        }
    }

    /** Every CREATE TABLE statement in dependency order. */
    public static final String[] CREATE_TABLES = {
            Users.CREATE, Districts.CREATE, Addresses.CREATE, Branches.CREATE, Categories.CREATE,
            Products.CREATE, OptionTypes.CREATE, ProductOptions.CREATE, Designs.CREATE, CartItems.CREATE,
            CartItemOptions.CREATE, Promotions.CREATE, Orders.CREATE, OrderItems.CREATE,
            OrderItemOptions.CREATE, OrderStatusHistory.CREATE, Notifications.CREATE, GalleryItems.CREATE,
            Guidelines.CREATE, Faqs.CREATE, SupportTickets.CREATE
    };

    /** Indexes from docs/03 §4. */
    public static final String[] CREATE_INDEXES = {
            "CREATE INDEX idx_products_category ON " + Products.TABLE + "(" + Products.CATEGORY_ID + ")",
            "CREATE INDEX idx_orders_user_status ON " + Orders.TABLE + "(" + Orders.USER_ID + ", " + Orders.STATUS + ")",
            "CREATE INDEX idx_order_items_order ON " + OrderItems.TABLE + "(" + OrderItems.ORDER_ID + ")",
            "CREATE INDEX idx_history_order ON " + OrderStatusHistory.TABLE + "(" + OrderStatusHistory.ORDER_ID + ")",
            "CREATE INDEX idx_notifications_user_read ON " + Notifications.TABLE + "(" + Notifications.USER_ID + ", "
                    + Notifications.IS_READ + ")",
            "CREATE INDEX idx_cart_user ON " + CartItems.TABLE + "(" + CartItems.USER_ID + ")"
    };
}
