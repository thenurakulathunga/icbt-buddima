package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.Products;
import com.printxpress.app.data.model.Category;

import java.util.List;

/**
 * Data access for {@code categories}, with the active product count per category.
 *
 * @since 1.0
 */
public class CategoryDao extends BaseDao<Category> {

    /** Row mapper for {@link Category}. */
    public static final CursorMapper<Category> MAPPER = row -> {
        Category c = new Category();
        c.setCategoryId(row.getLong(Categories.ID));
        c.setName(row.getString(Categories.NAME));
        c.setDescription(row.getString(Categories.DESCRIPTION));
        c.setIconRes(row.getString(Categories.ICON_RES));
        c.setImageRes(row.getString(Categories.IMAGE_RES));
        c.setSortOrder(row.getInt(Categories.SORT_ORDER));
        c.setProductCount(row.getInt("product_count"));
        return c;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public CategoryDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the categories table */
    @Override
    protected String table() {
        return Categories.TABLE;
    }

    /** @return the category id column */
    @Override
    protected String idColumn() {
        return Categories.ID;
    }

    /** @return the category mapper */
    @Override
    protected CursorMapper<Category> mapper() {
        return MAPPER;
    }

    /**
     * Converts a category to values.
     *
     * @param c the category
     * @return the values
     */
    @Override
    protected ContentValues toValues(Category c) {
        ContentValues v = new ContentValues();
        v.put(Categories.NAME, c.getName());
        v.put(Categories.DESCRIPTION, c.getDescription());
        v.put(Categories.ICON_RES, c.getIconRes());
        v.put(Categories.IMAGE_RES, c.getImageRes());
        v.put(Categories.SORT_ORDER, c.getSortOrder());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param c the category
     * @return the id
     */
    @Override
    protected long getId(Category c) {
        return c.getCategoryId();
    }

    /**
     * Stores the id.
     *
     * @param c  the category
     * @param id the id
     */
    @Override
    protected void setId(Category c, long id) {
        c.setCategoryId(id);
    }

    /** @return every category in display order with its active product count */
    public List<Category> findAllWithCounts() {
        return queryList("SELECT c.*, (SELECT COUNT(*) FROM " + Products.TABLE + " p WHERE p." + Products.CATEGORY_ID
                + " = c." + Categories.ID + " AND p." + Products.IS_ACTIVE + " = 1) AS product_count FROM "
                + Categories.TABLE + " c ORDER BY c." + Categories.SORT_ORDER);
    }
}
