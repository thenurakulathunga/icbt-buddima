package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.databinding.ItemNotificationBinding;

/**
 * Rows of the notification centre.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class NotificationAdapter extends BaseListAdapter<AppNotification, ItemNotificationBinding> {

    /** Creates the adapter. */
    public NotificationAdapter() {
        super(AppNotification::getNotificationId, n -> n.isRead() + n.getTitle());
    }

    /**
     * Inflates a row.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemNotificationBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemNotificationBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a notification.
     *
     * @param binding  the row binding
     * @param item     the notification
     * @param position the position
     */
    @Override
    protected void bind(ItemNotificationBinding binding, AppNotification item, int position) {
        binding.row.bind(item);
    }
}
