package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.AppNotification;

import java.util.List;

/**
 * In-app notification centre (FR-4.4).
 * <p>Pattern role: Repository; also the Subject for the bell badge count (Observer).</p>
 *
 * @since 1.0
 */
public interface NotificationRepository {

    /**
     * Lists a user's notifications.
     *
     * @param userId the recipient
     * @return the notifications, newest first
     */
    LiveData<Resource<List<AppNotification>>> getNotifications(long userId);

    /**
     * Marks one notification read.
     *
     * @param userId         the recipient
     * @param notificationId the notification
     * @return true on success
     */
    LiveData<Resource<Boolean>> markRead(long userId, long notificationId);

    /**
     * Marks all notifications read.
     *
     * @param userId the recipient
     * @return true on success
     */
    LiveData<Resource<Boolean>> markAllRead(long userId);

    /**
     * Deletes one notification.
     *
     * @param userId         the recipient
     * @param notificationId the notification
     * @return true on success
     */
    LiveData<Resource<Boolean>> delete(long userId, long notificationId);

    /** @return the observable unread count for the bell badge */
    LiveData<Integer> getUnreadCount();

    /**
     * Recounts unread notifications in the background.
     *
     * @param userId the recipient, or -1 for a guest (count 0)
     */
    void refreshUnread(long userId);

    /**
     * Stores a notification on the calling thread (used by workers and order events).
     *
     * @param notification the notification
     * @return the new id
     */
    long addBlocking(AppNotification notification);
}
