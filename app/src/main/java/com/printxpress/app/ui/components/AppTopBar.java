package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.appbar.MaterialToolbar;
import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewAppTopBarBinding;

/**
 * Screen top bar: title, optional back arrow, and bell and cart buttons with counter badges.
 *
 * @since 1.0
 */
public class AppTopBar extends LinearLayout {

    /** Largest number shown before "99+". */
    private static final int MAX_BADGE = 99;

    /** The inflated children. */
    private final ViewAppTopBarBinding binding;

    /**
     * Creates the bar.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public AppTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setBackgroundColor(getContext().getColor(R.color.px_surface));
        binding = ViewAppTopBarBinding.inflate(LayoutInflater.from(context), this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppTopBar);
        try {
            binding.toolbar.setTitle(a.getString(R.styleable.AppTopBar_px_title));
            showBack(a.getBoolean(R.styleable.AppTopBar_px_showBack, false));
            showActions(a.getBoolean(R.styleable.AppTopBar_px_showActions, false));
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the title.
     *
     * @param title the title
     */
    public void setTitle(CharSequence title) {
        binding.toolbar.setTitle(title);
    }

    /**
     * Sets the title.
     *
     * @param titleRes the title
     */
    public void setTitle(@StringRes int titleRes) {
        binding.toolbar.setTitle(titleRes);
    }

    /**
     * Shows or hides the back arrow.
     *
     * @param show true to show
     */
    public void showBack(boolean show) {
        if (show) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        } else {
            binding.toolbar.setNavigationIcon(null);
        }
    }

    /**
     * Registers the back action and shows the arrow.
     *
     * @param listener run on tap
     */
    public void setOnBackClick(Runnable listener) {
        showBack(true);
        binding.toolbar.setNavigationOnClickListener(v -> listener.run());
    }

    /**
     * Shows or hides the bell and cart.
     *
     * @param show true to show
     */
    public void showActions(boolean show) {
        binding.actions.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Registers the cart action.
     *
     * @param listener run on tap
     */
    public void setOnCartClick(Runnable listener) {
        binding.btnCart.setOnClickListener(v -> listener.run());
    }

    /**
     * Registers the notifications action.
     *
     * @param listener run on tap
     */
    public void setOnNotificationsClick(Runnable listener) {
        binding.btnNotifications.setOnClickListener(v -> listener.run());
    }

    /**
     * Updates the cart badge.
     *
     * @param count number of cart lines
     */
    public void setCartBadge(int count) {
        applyBadge(binding.badgeCart, count);
        binding.btnCart.setContentDescription(count > 0
                ? getResources().getQuantityString(R.plurals.cd_cart_count, count, count) : getContext().getString(R.string.cd_cart));
    }

    /**
     * Updates the notification badge.
     *
     * @param count unread notifications
     */
    public void setNotificationBadge(int count) {
        applyBadge(binding.badgeNotifications, count);
        binding.btnNotifications.setContentDescription(count > 0
                ? getResources().getQuantityString(R.plurals.cd_notifications_count, count, count)
                : getContext().getString(R.string.cd_notifications));
    }

    /** @return the toolbar, for menus */
    public MaterialToolbar getToolbar() {
        return binding.toolbar;
    }

    /**
     * Shows a count on a badge.
     *
     * @param badge the badge view
     * @param count the count; 0 hides it
     */
    private void applyBadge(TextView badge, int count) {
        badge.setText(count > MAX_BADGE ? getContext().getString(R.string.badge_overflow) : String.valueOf(count));
        badge.setVisibility(count > 0 ? VISIBLE : GONE);
    }
}
