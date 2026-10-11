package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.printxpress.app.R;
import com.printxpress.app.core.util.RelativeTimeFormatter;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.data.model.enums.NotificationType;
import com.printxpress.app.databinding.ViewNotificationItemBinding;

/**
 * One notification-centre row (FR-4.4): type icon, title, message, relative time and an unread dot.
 * Unread rows sit on the primary container tint.
 *
 * @since 1.0
 */
public class NotificationItemView extends LinearLayout {

    /** The inflated children. */
    private final ViewNotificationItemBinding binding;

    /**
     * Creates the row.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public NotificationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_md);
        setPadding(getResources().getDimensionPixelSize(R.dimen.space_lg), pad,
                getResources().getDimensionPixelSize(R.dimen.space_lg), pad);
        binding = ViewNotificationItemBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a notification.
     *
     * @param notification the notification
     */
    public void bind(AppNotification notification) {
        binding.imgType.setImageResource(iconFor(notification.getType()));
        binding.txtTitle.setText(notification.getTitle());
        binding.txtMessage.setText(notification.getMessage());
        binding.txtTime.setText(RelativeTimeFormatter.format(getContext(), notification.getCreatedAt()));
        binding.dotUnread.setVisibility(notification.isRead() ? INVISIBLE : VISIBLE);
        setBackgroundColor(notification.isRead() ? resolve(com.google.android.material.R.attr.colorSurface)
                : resolve(com.google.android.material.R.attr.colorPrimaryContainer));
    }

    /**
     * Picks the icon for a notification type.
     *
     * @param type the type
     * @return the icon
     */
    private static int iconFor(NotificationType type) {
        switch (type) {
            case ORDER:
                return R.drawable.ic_package;
            case PROMO:
                return R.drawable.ic_promo;
            default:
                return R.drawable.ic_info;
        }
    }

    /**
     * Reads a colour attribute.
     *
     * @param attr the attribute
     * @return the colour
     */
    private int resolve(int attr) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }
}
