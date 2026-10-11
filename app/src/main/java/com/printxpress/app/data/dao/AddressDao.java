package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Addresses;
import com.printxpress.app.data.db.DbContract.Districts;
import com.printxpress.app.data.model.Address;

import java.util.List;

/**
 * Data access for {@code addresses}, joined with the district name and delivery fee.
 * Keeps exactly one default address per user (FR-1.6, TC-17).
 *
 * @since 1.0
 */
public class AddressDao extends BaseDao<Address> {

    /** Select with district join. */
    private static final String SELECT = "SELECT a.*, d." + Districts.NAME + " AS district_name, d."
            + Districts.DELIVERY_FEE + " AS district_fee FROM " + Addresses.TABLE + " a JOIN " + Districts.TABLE
            + " d ON d." + Districts.ID + " = a." + Addresses.DISTRICT_ID;

    /** Row mapper for {@link Address}. */
    public static final CursorMapper<Address> MAPPER = row -> {
        Address a = new Address();
        a.setAddressId(row.getLong(Addresses.ID));
        a.setUserId(row.getLong(Addresses.USER_ID));
        a.setDistrictId(row.getLong(Addresses.DISTRICT_ID));
        a.setLabel(row.getString(Addresses.LABEL));
        a.setRecipientName(row.getString(Addresses.RECIPIENT_NAME));
        a.setRecipientPhone(row.getString(Addresses.RECIPIENT_PHONE));
        a.setLine1(row.getString(Addresses.LINE1));
        a.setLine2(row.getString(Addresses.LINE2));
        a.setCity(row.getString(Addresses.CITY));
        a.setPostalCode(row.getString(Addresses.POSTAL_CODE));
        a.setDefaultAddress(row.getBoolean(Addresses.IS_DEFAULT));
        a.setDistrictName(row.getString("district_name"));
        a.setDeliveryFee(row.getMoney("district_fee"));
        return a;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public AddressDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the addresses table */
    @Override
    protected String table() {
        return Addresses.TABLE;
    }

    /** @return the address id column */
    @Override
    protected String idColumn() {
        return Addresses.ID;
    }

    /** @return the address mapper */
    @Override
    protected CursorMapper<Address> mapper() {
        return MAPPER;
    }

    /**
     * Converts an address to values.
     *
     * @param a the address
     * @return the values
     */
    @Override
    protected ContentValues toValues(Address a) {
        ContentValues v = new ContentValues();
        v.put(Addresses.USER_ID, a.getUserId());
        v.put(Addresses.DISTRICT_ID, a.getDistrictId());
        v.put(Addresses.LABEL, a.getLabel());
        v.put(Addresses.RECIPIENT_NAME, a.getRecipientName());
        v.put(Addresses.RECIPIENT_PHONE, a.getRecipientPhone());
        v.put(Addresses.LINE1, a.getLine1());
        v.put(Addresses.LINE2, a.getLine2());
        v.put(Addresses.CITY, a.getCity());
        v.put(Addresses.POSTAL_CODE, a.getPostalCode());
        v.put(Addresses.IS_DEFAULT, a.isDefaultAddress() ? 1 : 0);
        return v;
    }

    /**
     * Returns the id.
     *
     * @param a the address
     * @return the id
     */
    @Override
    protected long getId(Address a) {
        return a.getAddressId();
    }

    /**
     * Stores the id.
     *
     * @param a  the address
     * @param id the id
     */
    @Override
    protected void setId(Address a, long id) {
        a.setAddressId(id);
    }

    /**
     * Finds one address with its district.
     *
     * @param id the address id
     * @return the address, or null
     */
    @Override
    public Address findById(long id) {
        return querySingle(SELECT + " WHERE a." + Addresses.ID + " = ?", String.valueOf(id));
    }

    /**
     * Lists a user's addresses, default first.
     *
     * @param userId the owner
     * @return the addresses
     */
    public List<Address> findByUser(long userId) {
        return queryList(SELECT + " WHERE a." + Addresses.USER_ID + " = ? ORDER BY a." + Addresses.IS_DEFAULT
                + " DESC, a." + Addresses.ID + " ASC", String.valueOf(userId));
    }

    /**
     * Inserts or updates an address; the first address or one marked default becomes the only default.
     *
     * @param a the address
     * @return the address id
     */
    public long save(Address a) {
        return inTransaction(() -> {
            boolean first = queryLong("SELECT COUNT(*) FROM " + Addresses.TABLE + " WHERE " + Addresses.USER_ID
                    + " = ? AND " + Addresses.ID + " <> ?", String.valueOf(a.getUserId()),
                    String.valueOf(a.getAddressId())) == 0;
            if (first) {
                a.setDefaultAddress(true);
            }
            if (a.isDefaultAddress()) {
                clearDefault(a.getUserId());
            }
            if (a.getAddressId() > 0) {
                update(a);
            } else {
                insert(a);
            }
            return a.getAddressId();
        });
    }

    /**
     * Makes one address the default and clears the flag on the others.
     *
     * @param userId    the owner
     * @param addressId the new default
     */
    public void setDefault(long userId, long addressId) {
        runInTransaction(() -> {
            clearDefault(userId);
            ContentValues v = new ContentValues();
            v.put(Addresses.IS_DEFAULT, 1);
            writable().update(Addresses.TABLE, v, Addresses.ID + " = ? AND " + Addresses.USER_ID + " = ?",
                    new String[]{String.valueOf(addressId), String.valueOf(userId)});
        });
    }

    /**
     * Deletes an address owned by a user; promotes another address to default when needed.
     *
     * @param userId    the owner
     * @param addressId the address
     * @return true when deleted
     */
    public boolean delete(long userId, long addressId) {
        return inTransaction(() -> {
            int rows = writable().delete(Addresses.TABLE, Addresses.ID + " = ? AND " + Addresses.USER_ID + " = ?",
                    new String[]{String.valueOf(addressId), String.valueOf(userId)});
            long defaults = queryLong("SELECT COUNT(*) FROM " + Addresses.TABLE + " WHERE " + Addresses.USER_ID
                    + " = ? AND " + Addresses.IS_DEFAULT + " = 1", String.valueOf(userId));
            if (defaults == 0) {
                writable().execSQL("UPDATE " + Addresses.TABLE + " SET " + Addresses.IS_DEFAULT + " = 1 WHERE "
                        + Addresses.ID + " = (SELECT MIN(" + Addresses.ID + ") FROM " + Addresses.TABLE + " WHERE "
                        + Addresses.USER_ID + " = ?)", new Object[]{userId});
            }
            return rows > 0;
        });
    }

    /**
     * Clears the default flag on all of a user's addresses.
     *
     * @param userId the owner
     */
    private void clearDefault(long userId) {
        ContentValues v = new ContentValues();
        v.put(Addresses.IS_DEFAULT, 0);
        writable().update(Addresses.TABLE, v, Addresses.USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}
