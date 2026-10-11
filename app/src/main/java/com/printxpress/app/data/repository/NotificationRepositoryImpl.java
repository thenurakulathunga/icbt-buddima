package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.data.dao.NotificationDao;
import com.printxpress.app.data.model.AppNotification;

import java.util.List;

/**
 * SQLite-backed {@link NotificationRepository}.
 *
 * @since 1.0
 */
public class NotificationRepositoryImpl extends BaseRepository implements NotificationRepository {

    /** Notification access. */
    private final NotificationDao notificationDao;
    /** Unread badge count. */
    private final MutableLiveData<Integer> unread = new MutableLiveData<>(0);

    /**
     * Creates the repository.
     *
     * @param executors       background executors
     * @param notificationDao notification DAO
     */
    public NotificationRepositoryImpl(AppExecutors executors, NotificationDao notificationDao) {
        super(executors);
        this.notificationDao = notificationDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<AppNotification>>> getNotifications(long userId) {
        return execute(() -> notificationDao.findByUser(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> markRead(long userId, long notificationId) {
        return execute(() -> {
            notificationDao.markRead(userId, notificationId);
            unread.postValue(notificationDao.unreadCount(userId));
            return true;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> markAllRead(long userId) {
        return execute(() -> {
            notificationDao.markAllRead(userId);
            unread.postValue(0);
            return true;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> delete(long userId, long notificationId) {
        return execute(() -> {
            notificationDao.delete(userId, notificationId);
            unread.postValue(notificationDao.unreadCount(userId));
            return true;
        });
    }

    /** @return the observable unread count */
    @Override
    public LiveData<Integer> getUnreadCount() {
        return unread;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshUnread(long userId) {
        if (userId < 0) {
            unread.postValue(0);
            return;
        }
        executors.diskIO().execute(() -> unread.postValue(notificationDao.unreadCount(userId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long addBlocking(AppNotification notification) {
        long id = notificationDao.insert(notification);
        unread.postValue(notificationDao.unreadCount(notification.getUserId()));
        return id;
    }
}
