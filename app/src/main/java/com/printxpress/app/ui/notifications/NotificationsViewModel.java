package com.printxpress.app.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.NotificationRepository;

import java.util.Collections;
import java.util.List;

/**
 * Notification centre state (S21, FR-4.4): list, mark one or all read, delete.
 *
 * @since 1.0
 */
public class NotificationsViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Notifications. */
    private final NotificationRepository notifications;
    /** The list. */
    private final MutableLiveData<Resource<List<AppNotification>>> list = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth          auth repository
     * @param notifications notification repository
     */
    public NotificationsViewModel(AuthRepository auth, NotificationRepository notifications) {
        this.auth = auth;
        this.notifications = notifications;
    }

    /** Reloads the list. */
    public void refresh() {
        if (!auth.isLoggedIn()) {
            list.setValue(Resource.success(Collections.emptyList()));
            return;
        }
        load(notifications.getNotifications(auth.currentUserId()), list);
    }

    /**
     * Marks one notification read.
     *
     * @param n the notification
     */
    public void markRead(AppNotification n) {
        if (!n.isRead()) {
            subscribe(notifications.markRead(auth.currentUserId(), n.getNotificationId()), r -> {
                if (r.isSuccess()) {
                    refresh();
                }
            });
        }
    }

    /** Marks everything read (TC-53). */
    public void markAllRead() {
        subscribe(notifications.markAllRead(auth.currentUserId()), r -> {
            if (r.isSuccess()) {
                postMessage(R.string.all_marked_read);
                refresh();
            }
        });
    }

    /** @return the notifications */
    public LiveData<Resource<List<AppNotification>>> getNotifications() {
        return list;
    }
}
