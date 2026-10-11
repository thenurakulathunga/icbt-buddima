package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Users;
import com.printxpress.app.data.model.User;

import java.time.LocalDateTime;

/**
 * Data access for {@code users}: lookup by email or phone, uniqueness checks and profile updates.
 *
 * @since 1.0
 */
public class UserDao extends BaseDao<User> {

    /** Row mapper for {@link User}. */
    public static final CursorMapper<User> MAPPER = row -> {
        User u = new User();
        u.setUserId(row.getLong(Users.ID));
        u.setFullName(row.getString(Users.FULL_NAME));
        u.setEmail(row.getString(Users.EMAIL));
        u.setPhone(row.getString(Users.PHONE));
        u.setPasswordHash(row.getString(Users.PASSWORD_HASH));
        u.setPasswordSalt(row.getString(Users.PASSWORD_SALT));
        u.setProfileImagePath(row.getString(Users.PROFILE_IMAGE));
        u.setNotifyOrders(row.getBoolean(Users.NOTIFY_ORDERS));
        u.setNotifyPromos(row.getBoolean(Users.NOTIFY_PROMOS));
        u.setCreatedAt(row.getDateTime(Users.CREATED_AT));
        u.setUpdatedAt(row.getDateTime(Users.UPDATED_AT));
        return u;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public UserDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the users table */
    @Override
    protected String table() {
        return Users.TABLE;
    }

    /** @return the user id column */
    @Override
    protected String idColumn() {
        return Users.ID;
    }

    /** @return the user mapper */
    @Override
    protected CursorMapper<User> mapper() {
        return MAPPER;
    }

    /**
     * Converts a user to values; blank email or phone is stored as NULL so UNIQUE allows many.
     *
     * @param u the user
     * @return the values
     */
    @Override
    protected ContentValues toValues(User u) {
        ContentValues v = new ContentValues();
        v.put(Users.FULL_NAME, u.getFullName());
        putText(v, Users.EMAIL, u.getEmail());
        putText(v, Users.PHONE, u.getPhone());
        v.put(Users.PASSWORD_HASH, u.getPasswordHash());
        v.put(Users.PASSWORD_SALT, u.getPasswordSalt());
        v.put(Users.PROFILE_IMAGE, u.getProfileImagePath());
        v.put(Users.NOTIFY_ORDERS, u.isNotifyOrders() ? 1 : 0);
        v.put(Users.NOTIFY_PROMOS, u.isNotifyPromos() ? 1 : 0);
        v.put(Users.UPDATED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
        return v;
    }

    /**
     * Returns the user id.
     *
     * @param u the user
     * @return the id
     */
    @Override
    protected long getId(User u) {
        return u.getUserId();
    }

    /**
     * Stores the generated id.
     *
     * @param u  the user
     * @param id the id
     */
    @Override
    protected void setId(User u, long id) {
        u.setUserId(id);
    }

    /**
     * Finds a user by email (case-insensitive) or canonical phone (FR-1.2).
     *
     * @param identifier an email address or canonical phone number
     * @return the user, or null
     */
    public User findByIdentifier(String identifier) {
        return querySingle("SELECT * FROM " + Users.TABLE + " WHERE " + Users.EMAIL + " = ? COLLATE NOCASE OR "
                + Users.PHONE + " = ? LIMIT 1", identifier, identifier);
    }

    /**
     * Checks whether an email is used by another account.
     *
     * @param email     the email
     * @param excludeId a user id to ignore (the current user), or -1
     * @return true when taken
     */
    public boolean isEmailTaken(String email, long excludeId) {
        return queryLong("SELECT COUNT(*) FROM " + Users.TABLE + " WHERE " + Users.EMAIL + " = ? COLLATE NOCASE AND "
                + Users.ID + " <> ?", email, String.valueOf(excludeId)) > 0;
    }

    /**
     * Checks whether a phone number is used by another account.
     *
     * @param phone     the canonical phone
     * @param excludeId a user id to ignore, or -1
     * @return true when taken
     */
    public boolean isPhoneTaken(String phone, long excludeId) {
        return queryLong("SELECT COUNT(*) FROM " + Users.TABLE + " WHERE " + Users.PHONE + " = ? AND "
                + Users.ID + " <> ?", phone, String.valueOf(excludeId)) > 0;
    }

    /**
     * Replaces the password hash and salt.
     *
     * @param userId the user
     * @param hash   the new hash
     * @param salt   the new salt
     * @return true when updated
     */
    public boolean updatePassword(long userId, String hash, String salt) {
        ContentValues v = new ContentValues();
        v.put(Users.PASSWORD_HASH, hash);
        v.put(Users.PASSWORD_SALT, salt);
        v.put(Users.UPDATED_AT, DateTimeUtils.toDbDateTime(LocalDateTime.now()));
        return writable().update(Users.TABLE, v, Users.ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    /**
     * Updates the notification preferences (FR-4.6).
     *
     * @param userId the user
     * @param orders order notifications on or off
     * @param promos promotion notifications on or off
     * @return true when updated
     */
    public boolean updatePreferences(long userId, boolean orders, boolean promos) {
        ContentValues v = new ContentValues();
        v.put(Users.NOTIFY_ORDERS, orders ? 1 : 0);
        v.put(Users.NOTIFY_PROMOS, promos ? 1 : 0);
        return writable().update(Users.TABLE, v, Users.ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    /**
     * Puts text or NULL when blank.
     *
     * @param v     the values
     * @param key   the column
     * @param value the text
     */
    private static void putText(ContentValues v, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            v.putNull(key);
        } else {
            v.put(key, value.trim());
        }
    }
}
