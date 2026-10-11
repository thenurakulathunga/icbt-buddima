package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Designs;
import com.printxpress.app.data.model.Design;

import java.util.List;

/**
 * Data access for {@code designs}, the Saved designs library (FR-1.7).
 *
 * @since 1.0
 */
public class DesignDao extends BaseDao<Design> {

    /** Row mapper for {@link Design}. */
    public static final CursorMapper<Design> MAPPER = row -> {
        Design d = new Design();
        d.setDesignId(row.getLong(Designs.ID));
        d.setUserId(row.getLong(Designs.USER_ID));
        d.setTitle(row.getString(Designs.TITLE));
        d.setFilePath(row.getString(Designs.FILE_PATH));
        d.setMimeType(row.getString(Designs.MIME_TYPE));
        d.setFileSizeBytes(row.getLong(Designs.FILE_SIZE));
        d.setCustomText(row.getString(Designs.CUSTOM_TEXT));
        d.setCreatedAt(row.getDateTime(Designs.CREATED_AT));
        return d;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public DesignDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the designs table */
    @Override
    protected String table() {
        return Designs.TABLE;
    }

    /** @return the design id column */
    @Override
    protected String idColumn() {
        return Designs.ID;
    }

    /** @return the design mapper */
    @Override
    protected CursorMapper<Design> mapper() {
        return MAPPER;
    }

    /**
     * Converts a design to values.
     *
     * @param d the design
     * @return the values
     */
    @Override
    protected ContentValues toValues(Design d) {
        ContentValues v = new ContentValues();
        v.put(Designs.USER_ID, d.getUserId());
        v.put(Designs.TITLE, d.getTitle());
        v.put(Designs.FILE_PATH, d.getFilePath());
        v.put(Designs.MIME_TYPE, d.getMimeType());
        v.put(Designs.FILE_SIZE, d.getFileSizeBytes());
        v.put(Designs.CUSTOM_TEXT, d.getCustomText());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param d the design
     * @return the id
     */
    @Override
    protected long getId(Design d) {
        return d.getDesignId();
    }

    /**
     * Stores the id.
     *
     * @param d  the design
     * @param id the id
     */
    @Override
    protected void setId(Design d, long id) {
        d.setDesignId(id);
    }

    /**
     * Lists a user's designs, newest first.
     *
     * @param userId the owner
     * @return the designs
     */
    public List<Design> findByUser(long userId) {
        return queryList("SELECT * FROM " + Designs.TABLE + " WHERE " + Designs.USER_ID + " = ? ORDER BY "
                + Designs.ID + " DESC", String.valueOf(userId));
    }

    /**
     * Lists a user's designs that hold an uploaded file, for the design picker.
     *
     * @param userId the owner
     * @return the designs with files
     */
    public List<Design> findFilesByUser(long userId) {
        return queryList("SELECT * FROM " + Designs.TABLE + " WHERE " + Designs.USER_ID + " = ? AND "
                + Designs.FILE_PATH + " IS NOT NULL ORDER BY " + Designs.ID + " DESC", String.valueOf(userId));
    }

    /**
     * Deletes a design owned by a user.
     *
     * @param userId   the owner
     * @param designId the design
     * @return true when deleted
     */
    public boolean delete(long userId, long designId) {
        return writable().delete(Designs.TABLE, Designs.ID + " = ? AND " + Designs.USER_ID + " = ?",
                new String[]{String.valueOf(designId), String.valueOf(userId)}) > 0;
    }
}
