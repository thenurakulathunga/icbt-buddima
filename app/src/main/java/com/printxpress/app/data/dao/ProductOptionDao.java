package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.db.DbContract.ProductOptions;
import com.printxpress.app.data.db.DbContract.Products;
import com.printxpress.app.data.model.ProductOption;

import java.util.Collection;
import java.util.List;

/**
 * Data access for {@code product_options}, joined with the option type name and order.
 *
 * @since 1.0
 */
public class ProductOptionDao extends BaseDao<ProductOption> {

    /** Select with option type join. */
    static final String SELECT = "SELECT po.*, ot." + OptionTypes.NAME + " AS type_name, ot." + OptionTypes.SORT_ORDER
            + " AS type_sort FROM " + ProductOptions.TABLE + " po JOIN " + OptionTypes.TABLE + " ot ON ot."
            + OptionTypes.ID + " = po." + ProductOptions.OPTION_TYPE_ID;

    /** Row mapper for {@link ProductOption}. */
    public static final CursorMapper<ProductOption> MAPPER = row -> {
        ProductOption o = new ProductOption();
        o.setOptionId(row.getLong(ProductOptions.ID));
        o.setProductId(row.getLong(ProductOptions.PRODUCT_ID));
        o.setOptionTypeId(row.getLong(ProductOptions.OPTION_TYPE_ID));
        o.setValue(row.getString(ProductOptions.VALUE));
        o.setPriceModifier(row.getDouble(ProductOptions.PRICE_MODIFIER));
        o.setDefaultOption(row.getBoolean(ProductOptions.IS_DEFAULT));
        o.setOptionTypeName(row.getString("type_name"));
        o.setOptionTypeSort(row.getInt("type_sort"));
        return o;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public ProductOptionDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the product options table */
    @Override
    protected String table() {
        return ProductOptions.TABLE;
    }

    /** @return the option id column */
    @Override
    protected String idColumn() {
        return ProductOptions.ID;
    }

    /** @return the option mapper */
    @Override
    protected CursorMapper<ProductOption> mapper() {
        return MAPPER;
    }

    /**
     * Converts an option to values.
     *
     * @param o the option
     * @return the values
     */
    @Override
    protected ContentValues toValues(ProductOption o) {
        ContentValues v = new ContentValues();
        v.put(ProductOptions.PRODUCT_ID, o.getProductId());
        v.put(ProductOptions.OPTION_TYPE_ID, o.getOptionTypeId());
        v.put(ProductOptions.VALUE, o.getValue());
        v.put(ProductOptions.PRICE_MODIFIER, o.getPriceModifier());
        v.put(ProductOptions.IS_DEFAULT, o.isDefaultOption() ? 1 : 0);
        return v;
    }

    /**
     * Returns the id.
     *
     * @param o the option
     * @return the id
     */
    @Override
    protected long getId(ProductOption o) {
        return o.getOptionId();
    }

    /**
     * Stores the id.
     *
     * @param o  the option
     * @param id the id
     */
    @Override
    protected void setId(ProductOption o, long id) {
        o.setOptionId(id);
    }

    /**
     * Lists a product's options grouped by type order.
     *
     * @param productId the product
     * @return the options
     */
    public List<ProductOption> findByProduct(long productId) {
        return queryList(SELECT + " WHERE po." + ProductOptions.PRODUCT_ID + " = ? ORDER BY ot."
                + OptionTypes.SORT_ORDER + ", po." + ProductOptions.ID, String.valueOf(productId));
    }

    /**
     * Loads options by id.
     *
     * @param ids the option ids
     * @return the options in type order
     */
    public List<ProductOption> findByIds(Collection<Long> ids) {
        String[] args = new String[ids.size()];
        int i = 0;
        for (Long id : ids) {
            args[i++] = String.valueOf(id);
        }
        return queryList(SELECT + " WHERE po." + ProductOptions.ID + " IN (" + placeholders(args.length)
                + ") ORDER BY ot." + OptionTypes.SORT_ORDER, args);
    }

    /**
     * Lists distinct option values offered in a category, for the filter sheet (FR-2.3).
     * Each returned option carries only its type name, type order and value.
     *
     * @param categoryId category id, or 0 for all categories
     * @return the distinct values in type order
     */
    public List<ProductOption> findFilterValues(long categoryId) {
        String sql = "SELECT DISTINCT ot." + OptionTypes.NAME + " AS type_name, ot." + OptionTypes.SORT_ORDER
                + " AS type_sort, po." + ProductOptions.VALUE + " FROM " + ProductOptions.TABLE + " po JOIN "
                + OptionTypes.TABLE + " ot ON ot." + OptionTypes.ID + " = po." + ProductOptions.OPTION_TYPE_ID
                + " JOIN " + Products.TABLE + " p ON p." + Products.ID + " = po." + ProductOptions.PRODUCT_ID
                + " WHERE p." + Products.IS_ACTIVE + " = 1 AND (? = 0 OR p." + Products.CATEGORY_ID + " = ?)"
                + " ORDER BY ot." + OptionTypes.SORT_ORDER + ", po." + ProductOptions.VALUE;
        String cat = String.valueOf(categoryId);
        return queryList(sql, cat, cat);
    }
}
