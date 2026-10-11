package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Branches;
import com.printxpress.app.data.model.Branch;

/**
 * Data access for the {@code branches} pickup locations.
 *
 * @since 1.0
 */
public class BranchDao extends BaseDao<Branch> {

    /** Row mapper for {@link Branch}. */
    public static final CursorMapper<Branch> MAPPER = row -> {
        Branch b = new Branch();
        b.setBranchId(row.getLong(Branches.ID));
        b.setName(row.getString(Branches.NAME));
        b.setAddress(row.getString(Branches.ADDRESS));
        b.setPhone(row.getString(Branches.PHONE));
        b.setOpeningHours(row.getString(Branches.OPENING_HOURS));
        return b;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public BranchDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the branches table */
    @Override
    protected String table() {
        return Branches.TABLE;
    }

    /** @return the branch id column */
    @Override
    protected String idColumn() {
        return Branches.ID;
    }

    /** @return the branch mapper */
    @Override
    protected CursorMapper<Branch> mapper() {
        return MAPPER;
    }

    /**
     * Converts a branch to values.
     *
     * @param b the branch
     * @return the values
     */
    @Override
    protected ContentValues toValues(Branch b) {
        ContentValues v = new ContentValues();
        v.put(Branches.NAME, b.getName());
        v.put(Branches.ADDRESS, b.getAddress());
        v.put(Branches.PHONE, b.getPhone());
        v.put(Branches.OPENING_HOURS, b.getOpeningHours());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param b the branch
     * @return the id
     */
    @Override
    protected long getId(Branch b) {
        return b.getBranchId();
    }

    /**
     * Stores the id.
     *
     * @param b  the branch
     * @param id the id
     */
    @Override
    protected void setId(Branch b, long id) {
        b.setBranchId(id);
    }
}
