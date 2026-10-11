package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.OptionTypes;
import com.printxpress.app.data.model.OptionType;

/**
 * Data access for the {@code option_types} lookup (Size, Paper Type, Material, Finish, Sides, Colour).
 *
 * @since 1.0
 */
public class OptionTypeDao extends BaseDao<OptionType> {

    /** Row mapper for {@link OptionType}. */
    public static final CursorMapper<OptionType> MAPPER = row -> {
        OptionType t = new OptionType();
        t.setOptionTypeId(row.getLong(OptionTypes.ID));
        t.setName(row.getString(OptionTypes.NAME));
        t.setSortOrder(row.getInt(OptionTypes.SORT_ORDER));
        return t;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public OptionTypeDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the option types table */
    @Override
    protected String table() {
        return OptionTypes.TABLE;
    }

    /** @return the option type id column */
    @Override
    protected String idColumn() {
        return OptionTypes.ID;
    }

    /** @return the option type mapper */
    @Override
    protected CursorMapper<OptionType> mapper() {
        return MAPPER;
    }

    /**
     * Converts an option type to values.
     *
     * @param t the option type
     * @return the values
     */
    @Override
    protected ContentValues toValues(OptionType t) {
        ContentValues v = new ContentValues();
        v.put(OptionTypes.NAME, t.getName());
        v.put(OptionTypes.SORT_ORDER, t.getSortOrder());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param t the option type
     * @return the id
     */
    @Override
    protected long getId(OptionType t) {
        return t.getOptionTypeId();
    }

    /**
     * Stores the id.
     *
     * @param t  the option type
     * @param id the id
     */
    @Override
    protected void setId(OptionType t, long id) {
        t.setOptionTypeId(id);
    }
}
