package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Notifications;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.data.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access for the in-app notification centre (FR-4.4).
 *
 * @since 1.0
 */
public class NotificationDao extends BaseDao<AppNotification> {

    /** Row mapper for {@link AppNotification}. */
    public static final CursorMapper<AppNotification> MAPPER = row -> {
        AppNotification n = new AppNotification();
        n.setNotificationId(row.getLong(Notifications.ID));
        n.setUserId(row.getLong(Notifications.USER_ID));
        n.setOrderId(row.getNullableLong(Notifications.ORDER_ID));
        n.setPromoId(row.getNullableLong(Notifications.PROMO_ID));
        n.setType(NotificationType.from(row.getString(Notifications.TYPE)));
        n.setTitle(row.getString(Notifications.TITLE));
        n.setMessage(row.getString(Notifications.MESSAGE));
        n.setRead(row.getBoolean(Notifications.IS_READ));
        n.setCreatedAt(row.getDateTime(Notifications.CREATED_AT));
        return n;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public NotificationDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the notifications table */
    @Override
    protected String table() {
        return Notifications.TABLE;
    }

    /** @return the notification id column */
    @Override
    protected String idColumn() {
        return Notifications.ID;
    }

    /** @return the notification mapper */
    @Override
    protected CursorMapper<AppNotification> mapper() {
        return MAPPER;
    }

    /**
     * Converts a notification to values; the creation time is set to now.
     *
     * @param n the notification
     * @return the values
     */
    @Override
    protected ContentValues toValues(AppNotification n) {
        ContentValues v = new ContentValues();
        v.put(Notifications.USER_ID, n.getUserId());
        putNullable(v, Notifications.ORDER_ID, n.getOrderId());
        putNullable(v, Notifications.PROMO_ID, n.getPromoId());
        v.put(Notifications.TYPE, n.getType().name());
        v.put(Notifications.TITLE, n.getTitle());
        v.put(Notifications.MESSAGE, n.getMessage());
        v.put(Notifications.IS_READ, n.isRead() ? 1 : 0);
        v.put(Notifications.CREATED_AT, DateTimeUtils.toDbDateTime(
                n.getCreatedAt() == null ? LocalDateTime.now() : n.getCreatedAt()));
        return v;
    }

    /**
     * Returns the id.
     *
     * @param n the notification
     * @return the id
     */
    @Override
    protected long getId(AppNotification n) {
        return n.getNotificationId();
    }

    /**
     * Stores the id.
     *
     * @param n  the notification
     * @param id the id
     */
    @Override
    protected void setId(AppNotification n, long id) {
        n.setNotificationId(id);
    }

    /**
     * Lists a user's notifications, newest first.
     *
     * @param userId the recipient
     * @return the notifications
     */
    public List<AppNotification> findByUser(long userId) {
        return queryList("SELECT * FROM " + Notifications.TABLE + " WHERE " + Notifications.USER_ID + " = ? ORDER BY "
                + Notifications.ID + " DESC", String.valueOf(userId));
    }

    /**
     * Counts unread notifications for the bell badge (docs/03 §6).
     *
     * @param userId the recipient
     * @return the unread count
     */
    public int unreadCount(long userId) {
        return (int) queryLong("SELECT COUNT(*) FROM " + Notifications.TABLE + " WHERE " + Notifications.USER_ID
                + " = ? AND " + Notifications.IS_READ + " = 0", String.valueOf(userId));
    }

    /**
     * Marks one notification as read.
     *
     * @param userId         the recipient
     * @param notificationId the notification
     */
    public void markRead(long userId, long notificationId) {
        ContentValues v = new ContentValues();
        v.put(Notifications.IS_READ, 1);
        writable().update(Notifications.TABLE, v, Notifications.ID + " = ? AND " + Notifications.USER_ID + " = ?",
                new String[]{String.valueOf(notificationId), String.valueOf(userId)});
    }

    /**
     * Marks all of a user's notifications as read (TC-53).
     *
     * @param userId the recipient
     */
    public void markAllRead(long userId) {
        ContentValues v = new ContentValues();
        v.put(Notifications.IS_READ, 1);
        writable().update(Notifications.TABLE, v, Notifications.USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    /**
     * Deletes one notification.
     *
     * @param userId         the recipient
     * @param notificationId the notification
     */
    public void delete(long userId, long notificationId) {
        writable().delete(Notifications.TABLE, Notifications.ID + " = ? AND " + Notifications.USER_ID + " = ?",
                new String[]{String.valueOf(notificationId), String.valueOf(userId)});
    }
}
