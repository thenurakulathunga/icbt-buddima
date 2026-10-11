package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Guidelines;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.model.enums.GuidelineTopic;

import java.util.List;

/**
 * Data access for print guidelines (FR-5.1).
 *
 * @since 1.0
 */
public class GuidelineDao extends BaseDao<Guideline> {

    /** Row mapper for {@link Guideline}. */
    public static final CursorMapper<Guideline> MAPPER = row -> {
        Guideline g = new Guideline();
        g.setGuidelineId(row.getLong(Guidelines.ID));
        g.setTopic(GuidelineTopic.from(row.getString(Guidelines.TOPIC)));
        g.setTitle(row.getString(Guidelines.TITLE));
        g.setContent(row.getString(Guidelines.CONTENT));
        g.setImageRes(row.getString(Guidelines.IMAGE_RES));
        g.setSortOrder(row.getInt(Guidelines.SORT_ORDER));
        return g;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public GuidelineDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the guidelines table */
    @Override
    protected String table() {
        return Guidelines.TABLE;
    }

    /** @return the guideline id column */
    @Override
    protected String idColumn() {
        return Guidelines.ID;
    }

    /** @return the guideline mapper */
    @Override
    protected CursorMapper<Guideline> mapper() {
        return MAPPER;
    }

    /**
     * Converts a guideline to values.
     *
     * @param g the guideline
     * @return the values
     */
    @Override
    protected ContentValues toValues(Guideline g) {
        ContentValues v = new ContentValues();
        v.put(Guidelines.TOPIC, g.getTopic().name());
        v.put(Guidelines.TITLE, g.getTitle());
        v.put(Guidelines.CONTENT, g.getContent());
        v.put(Guidelines.IMAGE_RES, g.getImageRes());
        v.put(Guidelines.SORT_ORDER, g.getSortOrder());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param g the guideline
     * @return the id
     */
    @Override
    protected long getId(Guideline g) {
        return g.getGuidelineId();
    }

    /**
     * Stores the id.
     *
     * @param g  the guideline
     * @param id the id
     */
    @Override
    protected void setId(Guideline g, long id) {
        g.setGuidelineId(id);
    }

    /** @return every guideline in display order */
    public List<Guideline> findAllSorted() {
        return findAll(Guidelines.SORT_ORDER);
    }
}
