package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.db.DbContract.ProductOptions;
import com.printxpress.app.data.db.DbContract.Products;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data access for {@code products}: listing, search, filter and sort (FR-2.2, FR-2.3).
 * <p>
 * The filter query is assembled from contract constants and {@code ?} placeholders only;
 * the user's search text and chosen values are always bound, never concatenated.
 * </p>
 *
 * @since 1.0
 */
public class ProductDao extends BaseDao<Product> {

    /** Select with category join. */
    private static final String SELECT = "SELECT p.*, c." + Categories.NAME + " AS category_name, c."
            + Categories.IMAGE_RES + " AS category_image FROM " + Products.TABLE + " p JOIN " + Categories.TABLE
            + " c ON c." + Categories.ID + " = p." + Products.CATEGORY_ID;

    /** Row mapper for {@link Product}. */
    public static final CursorMapper<Product> MAPPER = row -> {
        Product p = new Product();
        p.setProductId(row.getLong(Products.ID));
        p.setCategoryId(row.getLong(Products.CATEGORY_ID));
        p.setName(row.getString(Products.NAME));
        p.setDescription(row.getString(Products.DESCRIPTION));
        p.setBasePrice(row.getMoney(Products.BASE_PRICE));
        p.setPriceUnit(row.getString(Products.PRICE_UNIT));
        p.setMinQty(row.getInt(Products.MIN_QTY));
        p.setMaxQty(row.getInt(Products.MAX_QTY));
        p.setQtyStep(row.getInt(Products.QTY_STEP));
        p.setTurnaroundDays(row.getInt(Products.TURNAROUND_DAYS));
        p.setImageRes(row.getString(Products.IMAGE_RES));
        p.setActive(row.getBoolean(Products.IS_ACTIVE));
        p.setCategoryName(row.getString("category_name"));
        p.setCategoryImageRes(row.getString("category_image"));
        return p;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public ProductDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the products table */
    @Override
    protected String table() {
        return Products.TABLE;
    }

    /** @return the product id column */
    @Override
    protected String idColumn() {
        return Products.ID;
    }

    /** @return the product mapper */
    @Override
    protected CursorMapper<Product> mapper() {
        return MAPPER;
    }

    /**
     * Converts a product to values.
     *
     * @param p the product
     * @return the values
     */
    @Override
    protected ContentValues toValues(Product p) {
        ContentValues v = new ContentValues();
        v.put(Products.CATEGORY_ID, p.getCategoryId());
        v.put(Products.NAME, p.getName());
        v.put(Products.DESCRIPTION, p.getDescription());
        v.put(Products.BASE_PRICE, p.getBasePrice().toDouble());
        v.put(Products.PRICE_UNIT, p.getPriceUnit());
        v.put(Products.MIN_QTY, p.getMinQty());
        v.put(Products.MAX_QTY, p.getMaxQty());
        v.put(Products.QTY_STEP, p.getQtyStep());
        v.put(Products.TURNAROUND_DAYS, p.getTurnaroundDays());
        v.put(Products.IMAGE_RES, p.getImageRes());
        v.put(Products.IS_ACTIVE, p.isActive() ? 1 : 0);
        return v;
    }

    /**
     * Returns the id.
     *
     * @param p the product
     * @return the id
     */
    @Override
    protected long getId(Product p) {
        return p.getProductId();
    }

    /**
     * Stores the id.
     *
     * @param p  the product
     * @param id the id
     */
    @Override
    protected void setId(Product p, long id) {
        p.setProductId(id);
    }

    /**
     * Finds a product with its category.
     *
     * @param id the product id
     * @return the product, or null
     */
    @Override
    public Product findById(long id) {
        return querySingle(SELECT + " WHERE p." + Products.ID + " = ?", String.valueOf(id));
    }

    /**
     * Lists products for the home screen: the first product of each category.
     *
     * @param limit maximum number of products
     * @return the products
     */
    public List<Product> findPopular(int limit) {
        return queryList(SELECT + " WHERE p." + Products.IS_ACTIVE + " = 1 AND p." + Products.ID + " IN (SELECT MIN("
                + Products.ID + ") FROM " + Products.TABLE + " GROUP BY " + Products.CATEGORY_ID + ") ORDER BY c."
                + Categories.SORT_ORDER + " LIMIT ?", String.valueOf(limit));
    }

    /**
     * Searches products with a filter.
     *
     * @param filter the criteria
     * @return the matching active products
     */
    public List<Product> search(ProductFilter filter) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE p.").append(Products.IS_ACTIVE).append(" = 1");
        List<String> args = new ArrayList<>();
        if (filter.getCategoryId() > 0) {
            sql.append(" AND p.").append(Products.CATEGORY_ID).append(" = ?");
            args.add(String.valueOf(filter.getCategoryId()));
        }
        if (!filter.getQuery().isEmpty()) {
            sql.append(" AND (p.").append(Products.NAME).append(" LIKE ? OR c.").append(Categories.NAME)
                    .append(" LIKE ?)");
            String like = "%" + filter.getQuery() + "%";
            args.add(like);
            args.add(like);
        }
        for (Map.Entry<String, Set<String>> entry : filter.getOptionValues().entrySet()) {
            appendOptionClause(sql, args, entry.getKey(), entry.getValue());
        }
        sql.append(" ORDER BY ").append(orderBy(filter.getSort()));
        return queryList(sql.toString(), args.toArray(new String[0]));
    }

    /**
     * Adds an EXISTS clause requiring one of the values for an option type.
     *
     * @param sql    the query being built
     * @param args   the bound arguments
     * @param type   the option type name
     * @param values the accepted values
     */
    private static void appendOptionClause(StringBuilder sql, List<String> args, String type, Set<String> values) {
        sql.append(" AND EXISTS (SELECT 1 FROM ").append(ProductOptions.TABLE).append(" po JOIN ")
                .append(OptionTypes.TABLE).append(" ot ON ot.").append(OptionTypes.ID).append(" = po.")
                .append(ProductOptions.OPTION_TYPE_ID).append(" WHERE po.").append(ProductOptions.PRODUCT_ID)
                .append(" = p.").append(Products.ID).append(" AND ot.").append(OptionTypes.NAME).append(" = ? AND po.")
                .append(ProductOptions.VALUE).append(" IN (").append(placeholders(values.size())).append("))");
        args.add(type);
        args.addAll(values);
    }

    /**
     * Maps a sort order to an ORDER BY expression.
     *
     * @param sort the sort order
     * @return the expression
     */
    private static String orderBy(ProductFilter.Sort sort) {
        switch (sort) {
            case PRICE_LOW_HIGH:
                return "(p." + Products.BASE_PRICE + " * p." + Products.MIN_QTY + ") ASC";
            case PRICE_HIGH_LOW:
                return "(p." + Products.BASE_PRICE + " * p." + Products.MIN_QTY + ") DESC";
            case NAME:
                return "p." + Products.NAME + " COLLATE NOCASE ASC";
            case POPULAR:
            default:
                return "c." + Categories.SORT_ORDER + ", p." + Products.ID;
        }
    }
}
