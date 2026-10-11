package com.printxpress.app.ui.components;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * Visual style for each order status: background, text colour, Hugeicon and label (docs/05 §3.3).
 * Shared by {@link StatusBadge}, {@link OrderTimelineView} and notifications.
 *
 * @since 1.0
 */
public enum StatusStyle {
    /** Pending. */
    PENDING(R.color.px_status_pending_bg, R.color.px_status_pending_fg, R.drawable.ic_status_pending,
            R.string.status_pending),
    /** Confirmed. */
    CONFIRMED(R.color.px_status_confirmed_bg, R.color.px_status_confirmed_fg, R.drawable.ic_status_confirmed,
            R.string.status_confirmed),
    /** Processing. */
    PROCESSING(R.color.px_status_confirmed_bg, R.color.px_status_confirmed_fg, R.drawable.ic_status_processing,
            R.string.status_processing),
    /** Printing. */
    PRINTING(R.color.px_status_printing_bg, R.color.px_status_printing_fg, R.drawable.ic_status_printing,
            R.string.status_printing),
    /** Ready for pickup. */
    READY_FOR_PICKUP(R.color.px_status_ready_bg, R.color.px_status_ready_fg, R.drawable.ic_status_ready,
            R.string.status_ready_for_pickup),
    /** Out for delivery. */
    OUT_FOR_DELIVERY(R.color.px_status_ready_bg, R.color.px_status_ready_fg, R.drawable.ic_status_delivery,
            R.string.status_out_for_delivery),
    /** Completed. */
    COMPLETED(R.color.px_status_completed_bg, R.color.px_status_completed_fg, R.drawable.ic_status_completed,
            R.string.status_completed),
    /** Cancelled. */
    CANCELLED(R.color.px_status_cancelled_bg, R.color.px_status_cancelled_fg, R.drawable.ic_status_cancelled,
            R.string.status_cancelled);

    /** Background colour. */
    @ColorRes
    private final int background;
    /** Text and icon colour. */
    @ColorRes
    private final int foreground;
    /** Icon. */
    @DrawableRes
    private final int icon;
    /** Label. */
    @StringRes
    private final int label;

    /**
     * Creates a style.
     *
     * @param background background colour
     * @param foreground text colour
     * @param icon       icon
     * @param label      label
     */
    StatusStyle(int background, int foreground, int icon, int label) {
        this.background = background;
        this.foreground = foreground;
        this.icon = icon;
        this.label = label;
    }

    /**
     * Returns the style of a status.
     *
     * @param status the status
     * @return the style
     */
    public static StatusStyle of(OrderStatus status) {
        return valueOf((status == null ? OrderStatus.PENDING : status).name());
    }

    /** @return the background colour */
    public int getBackground() {
        return background;
    }

    /** @return the text colour */
    public int getForeground() {
        return foreground;
    }

    /** @return the icon */
    public int getIcon() {
        return icon;
    }

    /** @return the label */
    public int getLabel() {
        return label;
    }
}
