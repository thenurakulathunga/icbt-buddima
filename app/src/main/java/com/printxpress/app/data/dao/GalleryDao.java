package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.GalleryItems;
import com.printxpress.app.data.model.GalleryItem;

import java.util.List;

/**
 * Data access for the sample design gallery (FR-5.2).
 *
 * @since 1.0
 */
public class GalleryDao extends BaseDao<GalleryItem> {

    /** Select with category join. */
    private static final String SELECT = "SELECT g.*, c." + Categories.NAME + " AS category_name, c."
            + Categories.IMAGE_RES + " AS category_image FROM " + GalleryItems.TABLE + " g JOIN " + Categories.TABLE
            + " c ON c." + Categories.ID + " = g." + GalleryItems.CATEGORY_ID;

    /** Row mapper for {@link GalleryItem}. */
    public static final CursorMapper<GalleryItem> MAPPER = row -> {
        GalleryItem g = new GalleryItem();
        g.setGalleryId(row.getLong(GalleryItems.ID));
        g.setCategoryId(row.getLong(GalleryItems.CATEGORY_ID));
        g.setTitle(row.getString(GalleryItems.TITLE));
        g.setDescription(row.getString(GalleryItems.DESCRIPTION));
        g.setImageRes(row.getString(GalleryItems.IMAGE_RES));
        g.setSeasonal(row.getBoolean(GalleryItems.IS_SEASONAL));
        g.setCategoryName(row.getString("category_name"));
        g.setCategoryImageRes(row.getString("category_image"));
        return g;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public GalleryDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the gallery table */
    @Override
    protected String table() {
        return GalleryItems.TABLE;
    }

    /** @return the gallery id column */
    @Override
    protected String idColumn() {
        return GalleryItems.ID;
    }

    /** @return the gallery mapper */
    @Override
    protected CursorMapper<GalleryItem> mapper() {
        return MAPPER;
    }

    /**
     * Converts a gallery item to values.
     *
     * @param g the item
     * @return the values
     */
    @Override
    protected ContentValues toValues(GalleryItem g) {
        ContentValues v = new ContentValues();
        v.put(GalleryItems.CATEGORY_ID, g.getCategoryId());
        v.put(GalleryItems.TITLE, g.getTitle());
        v.put(GalleryItems.DESCRIPTION, g.getDescription());
        v.put(GalleryItems.IMAGE_RES, g.getImageRes());
        v.put(GalleryItems.IS_SEASONAL, g.isSeasonal() ? 1 : 0);
        return v;
    }

    /**
     * Returns the id.
     *
     * @param g the item
     * @return the id
     */
    @Override
    protected long getId(GalleryItem g) {
        return g.getGalleryId();
    }

    /**
     * Stores the id.
     *
     * @param g  the item
     * @param id the id
     */
    @Override
    protected void setId(GalleryItem g, long id) {
        g.setGalleryId(id);
    }

    /**
     * Lists gallery items, optionally for one category; seasonal items first.
     *
     * @param categoryId the category, or 0 for all
     * @return the items
     */
    public List<GalleryItem> findByCategory(long categoryId) {
        String cat = String.valueOf(categoryId);
        return queryList(SELECT + " WHERE (? = 0 OR g." + GalleryItems.CATEGORY_ID + " = ?) ORDER BY g."
                + GalleryItems.IS_SEASONAL + " DESC, g." + GalleryItems.ID, cat, cat);
    }
}
