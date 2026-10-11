package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.CartItemOptions;
import com.printxpress.app.data.db.DbContract.CartItems;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.db.DbContract.ProductOptions;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.ProductOption;

import java.util.List;

/**
 * Data access for {@code cart_items} and {@code cart_item_options} (FR-2.8).
 * <p>
 * An item and its selected options are always written together in one transaction. Reads
 * attach the product, options and design so the cart can price itself.
 * </p>
 *
 * @since 1.0
 */
public class CartDao extends BaseDao<CartItem> {

    /** Row mapper for {@link CartItem}. */
    public static final CursorMapper<CartItem> MAPPER = row -> {
        CartItem c = new CartItem();
        c.setCartItemId(row.getLong(CartItems.ID));
        c.setUserId(row.getLong(CartItems.USER_ID));
        c.setProductId(row.getLong(CartItems.PRODUCT_ID));
        c.setDesignId(row.getNullableLong(CartItems.DESIGN_ID));
        c.setQuantity(row.getInt(CartItems.QUANTITY));
        c.setCustomText(row.getString(CartItems.CUSTOM_TEXT));
        c.setInstructions(row.getString(CartItems.INSTRUCTIONS));
        c.setCreatedAt(row.getDateTime(CartItems.CREATED_AT));
        return c;
    };

    /** Product lookups for the joined product. */
    private final ProductDao productDao;
    /** Design lookups for the joined design. */
    private final DesignDao designDao;

    /**
     * Creates the DAO.
     *
     * @param helper     the database helper
     * @param productDao product DAO used to attach products
     * @param designDao  design DAO used to attach designs
     */
    public CartDao(SQLiteOpenHelper helper, ProductDao productDao, DesignDao designDao) {
        super(helper);
        this.productDao = productDao;
        this.designDao = designDao;
    }

    /** @return the cart items table */
    @Override
    protected String table() {
        return CartItems.TABLE;
    }

    /** @return the cart item id column */
    @Override
    protected String idColumn() {
        return CartItems.ID;
    }

    /** @return the cart item mapper */
    @Override
    protected CursorMapper<CartItem> mapper() {
        return MAPPER;
    }

    /**
     * Converts a cart item to values.
     *
     * @param c the item
     * @return the values
     */
    @Override
    protected ContentValues toValues(CartItem c) {
        ContentValues v = new ContentValues();
        v.put(CartItems.USER_ID, c.getUserId());
        v.put(CartItems.PRODUCT_ID, c.getProductId());
        putNullable(v, CartItems.DESIGN_ID, c.getDesignId());
        v.put(CartItems.QUANTITY, c.getQuantity());
        v.put(CartItems.CUSTOM_TEXT, c.getCustomText());
        v.put(CartItems.INSTRUCTIONS, c.getInstructions());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param c the item
     * @return the id
     */
    @Override
    protected long getId(CartItem c) {
        return c.getCartItemId();
    }

    /**
     * Stores the id.
     *
     * @param c  the item
     * @param id the id
     */
    @Override
    protected void setId(CartItem c, long id) {
        c.setCartItemId(id);
    }

    /**
     * Inserts or replaces an item and its options atomically.
     *
     * @param item the item
     * @return the item id
     */
    public long save(CartItem item) {
        return inTransaction(() -> {
            if (item.getCartItemId() > 0) {
                update(item);
                writable().delete(CartItemOptions.TABLE, CartItemOptions.CART_ITEM_ID + " = ?",
                        new String[]{String.valueOf(item.getCartItemId())});
            } else {
                insert(item);
            }
            for (ProductOption option : item.getOptions()) {
                ContentValues v = new ContentValues();
                v.put(CartItemOptions.CART_ITEM_ID, item.getCartItemId());
                v.put(CartItemOptions.OPTION_ID, option.getOptionId());
                writable().insertOrThrow(CartItemOptions.TABLE, null, v);
            }
            return item.getCartItemId();
        });
    }

    /**
     * Loads one item with product, options and design.
     *
     * @param id the item id
     * @return the item, or null
     */
    @Override
    public CartItem findById(long id) {
        CartItem item = super.findById(id);
        return item == null ? null : attach(item);
    }

    /**
     * Lists a user's cart, oldest first, with products, options and designs attached.
     *
     * @param userId the owner
     * @return the items
     */
    public List<CartItem> findByUser(long userId) {
        List<CartItem> items = queryList("SELECT * FROM " + CartItems.TABLE + " WHERE " + CartItems.USER_ID
                + " = ? ORDER BY " + CartItems.ID, String.valueOf(userId));
        for (CartItem item : items) {
            attach(item);
        }
        return items;
    }

    /**
     * Changes a line's quantity.
     *
     * @param userId     the owner
     * @param cartItemId the item
     * @param quantity   the new quantity
     * @return true when updated
     */
    public boolean updateQuantity(long userId, long cartItemId, int quantity) {
        ContentValues v = new ContentValues();
        v.put(CartItems.QUANTITY, quantity);
        return writable().update(CartItems.TABLE, v, CartItems.ID + " = ? AND " + CartItems.USER_ID + " = ?",
                new String[]{String.valueOf(cartItemId), String.valueOf(userId)}) > 0;
    }

    /**
     * Removes a line owned by a user; options cascade.
     *
     * @param userId     the owner
     * @param cartItemId the item
     * @return true when removed
     */
    public boolean delete(long userId, long cartItemId) {
        return writable().delete(CartItems.TABLE, CartItems.ID + " = ? AND " + CartItems.USER_ID + " = ?",
                new String[]{String.valueOf(cartItemId), String.valueOf(userId)}) > 0;
    }

    /**
     * Empties a user's cart.
     *
     * @param userId the owner
     */
    public void clear(long userId) {
        writable().delete(CartItems.TABLE, CartItems.USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    /**
     * Counts a user's cart lines for the badge.
     *
     * @param userId the owner
     * @return the number of lines
     */
    public int count(long userId) {
        return (int) queryLong("SELECT COUNT(*) FROM " + CartItems.TABLE + " WHERE " + CartItems.USER_ID + " = ?",
                String.valueOf(userId));
    }

    /**
     * Attaches product, options and design to an item.
     *
     * @param item the item
     * @return the same item
     */
    private CartItem attach(CartItem item) {
        item.setProduct(productDao.findById(item.getProductId()));
        item.setOptions(queryList(ProductOptionDao.MAPPER, ProductOptionDao.SELECT + " JOIN " + CartItemOptions.TABLE
                + " cio ON cio." + CartItemOptions.OPTION_ID + " = po." + ProductOptions.ID + " WHERE cio."
                + CartItemOptions.CART_ITEM_ID + " = ? ORDER BY ot." + OptionTypes.SORT_ORDER,
                String.valueOf(item.getCartItemId())));
        if (item.getDesignId() != null) {
            item.setDesign(designDao.findById(item.getDesignId()));
        }
        return item;
    }
}
