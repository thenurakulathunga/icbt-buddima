package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Faqs;
import com.printxpress.app.data.model.Faq;

import java.util.List;

/**
 * Data access for searchable FAQs (FR-5.3).
 *
 * @since 1.0
 */
public class FaqDao extends BaseDao<Faq> {

    /** Row mapper for {@link Faq}. */
    public static final CursorMapper<Faq> MAPPER = row -> {
        Faq f = new Faq();
        f.setFaqId(row.getLong(Faqs.ID));
        f.setCategory(row.getString(Faqs.CATEGORY));
        f.setQuestion(row.getString(Faqs.QUESTION));
        f.setAnswer(row.getString(Faqs.ANSWER));
        f.setSortOrder(row.getInt(Faqs.SORT_ORDER));
        return f;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public FaqDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the faqs table */
    @Override
    protected String table() {
        return Faqs.TABLE;
    }

    /** @return the faq id column */
    @Override
    protected String idColumn() {
        return Faqs.ID;
    }

    /** @return the faq mapper */
    @Override
    protected CursorMapper<Faq> mapper() {
        return MAPPER;
    }

    /**
     * Converts a FAQ to values.
     *
     * @param f the FAQ
     * @return the values
     */
    @Override
    protected ContentValues toValues(Faq f) {
        ContentValues v = new ContentValues();
        v.put(Faqs.CATEGORY, f.getCategory());
        v.put(Faqs.QUESTION, f.getQuestion());
        v.put(Faqs.ANSWER, f.getAnswer());
        v.put(Faqs.SORT_ORDER, f.getSortOrder());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param f the FAQ
     * @return the id
     */
    @Override
    protected long getId(Faq f) {
        return f.getFaqId();
    }

    /**
     * Stores the id.
     *
     * @param f  the FAQ
     * @param id the id
     */
    @Override
    protected void setId(Faq f, long id) {
        f.setFaqId(id);
    }

    /**
     * Searches questions, answers and categories (TC-58).
     *
     * @param query the search text; blank returns everything
     * @return the matching FAQs in display order
     */
    public List<Faq> search(String query) {
        String q = query == null ? "" : query.trim();
        String like = "%" + q + "%";
        return queryList("SELECT * FROM " + Faqs.TABLE + " WHERE ? = '' OR " + Faqs.QUESTION + " LIKE ? OR "
                + Faqs.ANSWER + " LIKE ? OR " + Faqs.CATEGORY + " LIKE ? ORDER BY " + Faqs.SORT_ORDER, q, like, like, like);
    }
}
