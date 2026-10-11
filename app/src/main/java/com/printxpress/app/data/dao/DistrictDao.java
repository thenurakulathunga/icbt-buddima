package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Districts;
import com.printxpress.app.data.model.District;

import java.util.List;

/**
 * Data access for the {@code districts} lookup table.
 *
 * @since 1.0
 */
public class DistrictDao extends BaseDao<District> {

    /** Row mapper for {@link District}. */
    public static final CursorMapper<District> MAPPER = row -> {
        District d = new District();
        d.setDistrictId(row.getLong(Districts.ID));
        d.setName(row.getString(Districts.NAME));
        d.setDeliveryFee(row.getMoney(Districts.DELIVERY_FEE));
        return d;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public DistrictDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the districts table */
    @Override
    protected String table() {
        return Districts.TABLE;
    }

    /** @return the district id column */
    @Override
    protected String idColumn() {
        return Districts.ID;
    }

    /** @return the district mapper */
    @Override
    protected CursorMapper<District> mapper() {
        return MAPPER;
    }

    /**
     * Converts a district to values.
     *
     * @param d the district
     * @return the values
     */
    @Override
    protected ContentValues toValues(District d) {
        ContentValues v = new ContentValues();
        v.put(Districts.NAME, d.getName());
        v.put(Districts.DELIVERY_FEE, d.getDeliveryFee().toDouble());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param d the district
     * @return the id
     */
    @Override
    protected long getId(District d) {
        return d.getDistrictId();
    }

    /**
     * Stores the id.
     *
     * @param d  the district
     * @param id the id
     */
    @Override
    protected void setId(District d, long id) {
        d.setDistrictId(id);
    }

    /** @return all districts sorted by name */
    public List<District> findAllSorted() {
        return findAll(Districts.NAME + " ASC");
    }
}
