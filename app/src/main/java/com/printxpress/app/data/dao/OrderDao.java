package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.core.util.OrderNumberGenerator;
import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Addresses;
import com.printxpress.app.data.db.DbContract.Branches;
import com.printxpress.app.data.db.DbContract.CartItems;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.Designs;
import com.printxpress.app.data.db.DbContract.Districts;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.db.DbContract.OrderItemOptions;
import com.printxpress.app.data.db.DbContract.OrderItems;
import com.printxpress.app.data.db.DbContract.OrderStatusHistory;
import com.printxpress.app.data.db.DbContract.Orders;
import com.printxpress.app.data.db.DbContract.ProductOptions;
import com.printxpress.app.data.db.DbContract.Products;
import com.printxpress.app.data.db.DbContract.Promotions;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.OrderItem;
import com.printxpress.app.data.model.OrderItemOption;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.data.model.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access for orders, order lines, line options and status history (FR-2.13, FR-3.x).
 * <p>
 * Every multi-table write runs in a single SQLite transaction (NFR-7): placing an order writes
 * the header, lines, line options, the first history entry and clears the cart; a status change
 * updates the header and appends history. Business rules such as "cancel only before PRINTING"
 * are checked by the repository through {@code OrderState} before these methods are called.
 * </p>
 *
 * @since 1.0
 */
public class OrderDao extends BaseDao<Order> {

    /** Order list select with summary columns. */
    private static final String SELECT_SUMMARY = "SELECT o.*, b." + Branches.NAME + " AS branch_name, pr."
            + Promotions.PROMO_CODE + " AS promo_code, "
            + "(SELECT COUNT(*) FROM " + OrderItems.TABLE + " i WHERE i." + OrderItems.ORDER_ID + " = o." + Orders.ID
            + ") AS item_count, "
            + "(SELECT p." + Products.NAME + " FROM " + OrderItems.TABLE + " i JOIN " + Products.TABLE + " p ON p."
            + Products.ID + " = i." + OrderItems.PRODUCT_ID + " WHERE i." + OrderItems.ORDER_ID + " = o." + Orders.ID
            + " ORDER BY i." + OrderItems.ID + " LIMIT 1) AS first_product, "
            + "(SELECT p." + Products.IMAGE_RES + " FROM " + OrderItems.TABLE + " i JOIN " + Products.TABLE + " p ON p."
            + Products.ID + " = i." + OrderItems.PRODUCT_ID + " WHERE i." + OrderItems.ORDER_ID + " = o." + Orders.ID
            + " ORDER BY i." + OrderItems.ID + " LIMIT 1) AS first_image, "
            + "(SELECT a." + Addresses.LINE1 + " || ', ' || a." + Addresses.CITY + " || ', ' || d." + Districts.NAME
            + " FROM " + Addresses.TABLE + " a JOIN " + Districts.TABLE + " d ON d." + Districts.ID + " = a."
            + Addresses.DISTRICT_ID + " WHERE a." + Addresses.ID + " = o." + Orders.ADDRESS_ID + ") AS address_summary "
            + "FROM " + Orders.TABLE + " o LEFT JOIN " + Branches.TABLE + " b ON b." + Branches.ID + " = o."
            + Orders.BRANCH_ID + " LEFT JOIN " + Promotions.TABLE + " pr ON pr." + Promotions.ID + " = o."
            + Orders.PROMO_ID;

    /** Statuses that are finished, as a SQL list. */
    private static final String FINISHED = "('" + OrderStatus.COMPLETED.name() + "','" + OrderStatus.CANCELLED.name() + "')";

    /** Row mapper for {@link Order} headers with summary columns. */
    public static final CursorMapper<Order> MAPPER = row -> {
        Order o = new Order();
        o.setOrderId(row.getLong(Orders.ID));
        o.setOrderNumber(row.getString(Orders.ORDER_NUMBER));
        o.setUserId(row.getLong(Orders.USER_ID));
        o.setAddressId(row.getNullableLong(Orders.ADDRESS_ID));
        o.setBranchId(row.getNullableLong(Orders.BRANCH_ID));
        o.setPromoId(row.getNullableLong(Orders.PROMO_ID));
        o.setStatus(OrderStatus.from(row.getString(Orders.STATUS)));
        o.setFulfilmentType(FulfilmentType.from(row.getString(Orders.FULFILMENT_TYPE)));
        o.setPaymentMethod(PaymentMethod.from(row.getString(Orders.PAYMENT_METHOD)));
        o.setScheduledDate(row.getDate(Orders.SCHEDULED_DATE));
        o.setScheduledSlot(row.getString(Orders.SCHEDULED_SLOT));
        o.setSubtotal(row.getMoney(Orders.SUBTOTAL));
        o.setDiscount(row.getMoney(Orders.DISCOUNT));
        o.setDeliveryFee(row.getMoney(Orders.DELIVERY_FEE));
        o.setTotal(row.getMoney(Orders.TOTAL));
        o.setNotes(row.getString(Orders.NOTES));
        o.setCancelReason(row.getString(Orders.CANCEL_REASON));
        o.setCreatedAt(row.getDateTime(Orders.CREATED_AT));
        o.setUpdatedAt(row.getDateTime(Orders.UPDATED_AT));
        o.setBranchName(row.getString("branch_name"));
        o.setPromoCode(row.getString("promo_code"));
        o.setItemCount(row.getInt("item_count"));
        o.setFirstProductName(row.getString("first_product"));
        o.setFirstProductImageRes(row.getString("first_image"));
        o.setAddressSummary(row.getString("address_summary"));
        return o;
    };

    /** Row mapper for order lines with product and design joins. */
    private static final CursorMapper<OrderItem> ITEM_MAPPER = row -> {
        OrderItem i = new OrderItem();
        i.setOrderItemId(row.getLong(OrderItems.ID));
        i.setOrderId(row.getLong(OrderItems.ORDER_ID));
        i.setProductId(row.getLong(OrderItems.PRODUCT_ID));
        i.setDesignId(row.getNullableLong(OrderItems.DESIGN_ID));
        i.setQuantity(row.getInt(OrderItems.QUANTITY));
        i.setUnitPrice(row.getMoney(OrderItems.UNIT_PRICE));
        i.setLineTotal(row.getMoney(OrderItems.LINE_TOTAL));
        i.setCustomText(row.getString(OrderItems.CUSTOM_TEXT));
        i.setInstructions(row.getString(OrderItems.INSTRUCTIONS));
        i.setProductName(row.getString("product_name"));
        i.setProductImageRes(row.getString("product_image"));
        i.setCategoryImageRes(row.getString("category_image"));
        i.setDesignTitle(row.getString("design_title"));
        i.setDesignFilePath(row.getString("design_path"));
        i.setDesignMimeType(row.getString("design_mime"));
        i.setTurnaroundDays(row.getInt("product_turnaround"));
        return i;
    };

    /** Row mapper for line options with type and value joins. */
    private static final CursorMapper<OrderItemOption> OPTION_MAPPER = row -> {
        OrderItemOption o = new OrderItemOption();
        o.setOrderItemId(row.getLong(OrderItemOptions.ORDER_ITEM_ID));
        o.setOptionId(row.getLong(OrderItemOptions.OPTION_ID));
        o.setPriceModifierSnapshot(row.getDouble(OrderItemOptions.PRICE_MODIFIER_SNAPSHOT));
        o.setOptionTypeName(row.getString("type_name"));
        o.setValue(row.getString(ProductOptions.VALUE));
        return o;
    };

    /** Row mapper for history entries. */
    private static final CursorMapper<com.printxpress.app.data.model.OrderStatusHistory> HISTORY_MAPPER = row -> {
        com.printxpress.app.data.model.OrderStatusHistory h = new com.printxpress.app.data.model.OrderStatusHistory();
        h.setHistoryId(row.getLong(OrderStatusHistory.ID));
        h.setOrderId(row.getLong(OrderStatusHistory.ORDER_ID));
        h.setStatus(OrderStatus.from(row.getString(OrderStatusHistory.STATUS)));
        h.setRemark(row.getString(OrderStatusHistory.REMARK));
        h.setChangedAt(row.getDateTime(OrderStatusHistory.CHANGED_AT));
        return h;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public OrderDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the orders table */
    @Override
    protected String table() {
        return Orders.TABLE;
    }

    /** @return the order id column */
    @Override
    protected String idColumn() {
        return Orders.ID;
    }

    /** @return the order mapper */
    @Override
    protected CursorMapper<Order> mapper() {
        return MAPPER;
    }

    /**
     * Converts an order header to values.
     *
     * @param o the order
     * @return the values
     */
    @Override
    protected ContentValues toValues(Order o) {
        ContentValues v = new ContentValues();
        v.put(Orders.ORDER_NUMBER, o.getOrderNumber());
        v.put(Orders.USER_ID, o.getUserId());
        putNullable(v, Orders.ADDRESS_ID, o.getAddressId());
        putNullable(v, Orders.BRANCH_ID, o.getBranchId());
        putNullable(v, Orders.PROMO_ID, o.getPromoId());
        v.put(Orders.STATUS, o.getStatus().name());
        v.put(Orders.FULFILMENT_TYPE, o.getFulfilmentType().name());
        v.put(Orders.PAYMENT_METHOD, o.getPaymentMethod().name());
        v.put(Orders.SCHEDULED_DATE, DateTimeUtils.toDbDate(o.getScheduledDate()));
        v.put(Orders.SCHEDULED_SLOT, o.getScheduledSlot());
        v.put(Orders.SUBTOTAL, o.getSubtotal().toDouble());
        v.put(Orders.DISCOUNT, o.getDiscount().toDouble());
        v.put(Orders.DELIVERY_FEE, o.getDeliveryFee().toDouble());
        v.put(Orders.TOTAL, o.getTotal().toDouble());
        v.put(Orders.NOTES, o.getNotes());
        v.put(Orders.CANCEL_REASON, o.getCancelReason());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param o the order
     * @return the id
     */
    @Override
    protected long getId(Order o) {
        return o.getOrderId();
    }

    /**
     * Stores the id.
     *
     * @param o  the order
     * @param id the id
     */
    @Override
    protected void setId(Order o, long id) {
        o.setOrderId(id);
    }

    /**
     * Places an order atomically: number, header, lines, options, first history entry, empty cart.
     * If any insert fails, nothing is kept (TC-41).
     *
     * @param order     the built order (status PENDING)
     * @param clearCart whether to empty the user's cart in the same transaction
     * @return the new order id
     */
    public long insertOrderWithItems(Order order, boolean clearCart) {
        return inTransaction(() -> {
            LocalDate today = LocalDate.now();
            String prefix = OrderNumberGenerator.dayPrefix(today);
            long todayCount = queryLong("SELECT COUNT(*) FROM " + Orders.TABLE + " WHERE " + Orders.ORDER_NUMBER
                    + " LIKE ?", prefix + "%");
            order.setOrderNumber(OrderNumberGenerator.generate(today, (int) todayCount + 1));
            order.setStatus(OrderStatus.PENDING);
            insert(order);
            for (OrderItem item : order.getItems()) {
                insertItem(order.getOrderId(), item);
            }
            insertHistory(order.getOrderId(), OrderStatus.PENDING, null);
            if (clearCart) {
                writable().delete(CartItems.TABLE, CartItems.USER_ID + " = ?",
                        new String[]{String.valueOf(order.getUserId())});
            }
            return order.getOrderId();
        });
    }

    /**
     * Inserts one order line and its option snapshots.
     *
     * @param orderId the order
     * @param item    the line
     */
    private void insertItem(long orderId, OrderItem item) {
        ContentValues v = new ContentValues();
        v.put(OrderItems.ORDER_ID, orderId);
        v.put(OrderItems.PRODUCT_ID, item.getProductId());
        putNullable(v, OrderItems.DESIGN_ID, item.getDesignId());
        v.put(OrderItems.QUANTITY, item.getQuantity());
        v.put(OrderItems.UNIT_PRICE, item.getUnitPrice().toDouble());
        v.put(OrderItems.LINE_TOTAL, item.getLineTotal().toDouble());
        v.put(OrderItems.CUSTOM_TEXT, item.getCustomText());
        v.put(OrderItems.INSTRUCTIONS, item.getInstructions());
        long itemId = writable().insertOrThrow(OrderItems.TABLE, null, v);
        item.setOrderItemId(itemId);
        item.setOrderId(orderId);
        for (OrderItemOption option : item.getOptions()) {
            ContentValues ov = new ContentValues();
            ov.put(OrderItemOptions.ORDER_ITEM_ID, itemId);
            ov.put(OrderItemOptions.OPTION_ID, option.getOptionId());
            ov.put(OrderItemOptions.PRICE_MODIFIER_SNAPSHOT, option.getPriceModifierSnapshot());
            writable().insertOrThrow(OrderItemOptions.TABLE, null, ov);
        }
    }

    /**
     * Appends a status history entry.
     *
     * @param orderId the order
     * @param status  the status entered
     * @param remark  optional remark
     */
    private void insertHistory(long orderId, OrderStatus status, String remark) {
        ContentValues h = new ContentValues();
        h.put(OrderStatusHistory.ORDER_ID, orderId);
        h.put(OrderStatusHistory.STATUS, status.name());
        h.put(OrderStatusHistory.REMARK, remark);
        h.put(OrderStatusHistory.CHANGED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
        writable().insertOrThrow(OrderStatusHistory.TABLE, null, h);
    }

    /**
     * Changes status and records history in one transaction (docs/03 §6).
     *
     * @param orderId the order
     * @param status  the new status
     * @param remark  optional remark
     * @return true when the order existed
     */
    public boolean updateStatus(long orderId, OrderStatus status, String remark) {
        return inTransaction(() -> {
            ContentValues v = new ContentValues();
            v.put(Orders.STATUS, status.name());
            v.put(Orders.UPDATED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
            boolean changed = writable().update(Orders.TABLE, v, Orders.ID + " = ?",
                    new String[]{String.valueOf(orderId)}) > 0;
            if (changed) {
                insertHistory(orderId, status, remark);
            }
            return changed;
        });
    }

    /**
     * Cancels an order with a reason and records history.
     *
     * @param orderId the order
     * @param reason  the cancellation reason
     * @return true when cancelled
     */
    public boolean cancel(long orderId, String reason) {
        return inTransaction(() -> {
            ContentValues v = new ContentValues();
            v.put(Orders.STATUS, OrderStatus.CANCELLED.name());
            v.put(Orders.CANCEL_REASON, reason);
            v.put(Orders.UPDATED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
            boolean changed = writable().update(Orders.TABLE, v, Orders.ID + " = ?",
                    new String[]{String.valueOf(orderId)}) > 0;
            if (changed) {
                insertHistory(orderId, OrderStatus.CANCELLED, reason);
            }
            return changed;
        });
    }

    /**
     * Moves an order to a new date and slot, recording a history remark.
     *
     * @param orderId the order
     * @param date    the new date
     * @param slot    the new slot label
     * @param remark  the history remark
     * @return true when rescheduled
     */
    public boolean reschedule(long orderId, LocalDate date, String slot, String remark) {
        return inTransaction(() -> {
            Order current = findById(orderId);
            if (current == null) {
                return false;
            }
            ContentValues v = new ContentValues();
            v.put(Orders.SCHEDULED_DATE, DateTimeUtils.toDbDate(date));
            v.put(Orders.SCHEDULED_SLOT, slot);
            v.put(Orders.UPDATED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
            writable().update(Orders.TABLE, v, Orders.ID + " = ?", new String[]{String.valueOf(orderId)});
            insertHistory(orderId, current.getStatus(), remark);
            return true;
        });
    }

    /**
     * Finds an order header with summary columns.
     *
     * @param id the order id
     * @return the order, or null
     */
    @Override
    public Order findById(long id) {
        return querySingle(SELECT_SUMMARY + " WHERE o." + Orders.ID + " = ?", String.valueOf(id));
    }

    /**
     * Loads an order with its lines, their options and the history (FR-3.2).
     *
     * @param orderId the order
     * @return the full order, or null
     */
    public Order findDetail(long orderId) {
        Order order = findById(orderId);
        if (order == null) {
            return null;
        }
        List<OrderItem> items = queryList(ITEM_MAPPER, "SELECT i.*, p." + Products.NAME + " AS product_name, p."
                + Products.IMAGE_RES + " AS product_image, p." + Products.TURNAROUND_DAYS
                + " AS product_turnaround, c." + Categories.IMAGE_RES + " AS category_image, d."
                + Designs.TITLE + " AS design_title, d." + Designs.FILE_PATH + " AS design_path, d."
                + Designs.MIME_TYPE + " AS design_mime FROM " + OrderItems.TABLE + " i JOIN " + Products.TABLE
                + " p ON p." + Products.ID + " = i." + OrderItems.PRODUCT_ID + " JOIN " + Categories.TABLE + " c ON c."
                + Categories.ID + " = p." + Products.CATEGORY_ID + " LEFT JOIN " + Designs.TABLE + " d ON d."
                + Designs.ID + " = i." + OrderItems.DESIGN_ID + " WHERE i." + OrderItems.ORDER_ID + " = ? ORDER BY i."
                + OrderItems.ID, String.valueOf(orderId));
        for (OrderItem item : items) {
            item.setOptions(queryList(OPTION_MAPPER, "SELECT oio.*, po." + ProductOptions.VALUE + ", ot."
                    + OptionTypes.NAME + " AS type_name FROM " + OrderItemOptions.TABLE + " oio JOIN "
                    + ProductOptions.TABLE + " po ON po." + ProductOptions.ID + " = oio." + OrderItemOptions.OPTION_ID
                    + " JOIN " + OptionTypes.TABLE + " ot ON ot." + OptionTypes.ID + " = po."
                    + ProductOptions.OPTION_TYPE_ID + " WHERE oio." + OrderItemOptions.ORDER_ITEM_ID + " = ? ORDER BY ot."
                    + OptionTypes.SORT_ORDER, String.valueOf(item.getOrderItemId())));
        }
        order.setItems(items);
        order.setHistory(queryList(HISTORY_MAPPER, "SELECT * FROM " + OrderStatusHistory.TABLE + " WHERE "
                + OrderStatusHistory.ORDER_ID + " = ? ORDER BY " + OrderStatusHistory.ID, String.valueOf(orderId)));
        return order;
    }

    /**
     * Lists a user's active orders, newest first (docs/03 §6).
     *
     * @param userId the owner
     * @return orders that are neither completed nor cancelled
     */
    public List<Order> findActive(long userId) {
        return queryList(SELECT_SUMMARY + " WHERE o." + Orders.USER_ID + " = ? AND o." + Orders.STATUS + " NOT IN "
                + FINISHED + " ORDER BY o." + Orders.CREATED_AT + " DESC, o." + Orders.ID + " DESC",
                String.valueOf(userId));
    }

    /**
     * Lists a user's completed and cancelled orders, newest first (FR-1.8).
     *
     * @param userId the owner
     * @return the finished orders
     */
    public List<Order> findPast(long userId) {
        return queryList(SELECT_SUMMARY + " WHERE o." + Orders.USER_ID + " = ? AND o." + Orders.STATUS + " IN "
                + FINISHED + " ORDER BY o." + Orders.UPDATED_AT + " DESC, o." + Orders.ID + " DESC",
                String.valueOf(userId));
    }

    /** @return every unfinished order across users, used to resume the simulation after a reboot */
    public List<Order> findAllUnfinished() {
        return queryList(SELECT_SUMMARY + " WHERE o." + Orders.STATUS + " NOT IN " + FINISHED);
    }

    /**
     * Counts a user's orders, excluding cancelled ones.
     *
     * @param userId the owner
     * @return the number of orders
     */
    public int countOrders(long userId) {
        return (int) queryLong("SELECT COUNT(*) FROM " + Orders.TABLE + " WHERE " + Orders.USER_ID + " = ? AND "
                + Orders.STATUS + " <> ?", String.valueOf(userId), OrderStatus.CANCELLED.name());
    }
}
