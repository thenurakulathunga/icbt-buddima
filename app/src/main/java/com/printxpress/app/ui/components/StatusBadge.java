package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.printxpress.app.R;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * Pill badge showing an order status with its colour pair and icon (docs/05 §3.3).
 *
 * @since 1.0
 */
public class StatusBadge extends Chip {

    /**
     * Creates the badge.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public StatusBadge(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.chipStyle);
        setClickable(false);
        setCheckable(false);
        setFocusable(false);
        setEnsureMinTouchTargetSize(false);
        setChipMinHeightResource(R.dimen.space_xl);
        setTextAppearance(R.style.TextAppearance_PrintXpress_LabelMedium);
        setChipIconSizeResource(R.dimen.icon_sm);
        setChipStrokeWidth(0);
        setStatus(OrderStatus.PENDING);
    }

    /**
     * Applies a status.
     *
     * @param status the status
     */
    public void setStatus(OrderStatus status) {
        StatusStyle style = StatusStyle.of(status);
        int fg = ContextCompat.getColor(getContext(), style.getForeground());
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), style.getBackground())));
        setTextColor(fg);
        setChipIconResource(style.getIcon());
        setChipIconTint(ColorStateList.valueOf(fg));
        setChipIconVisible(true);
        setText(style.getLabel());
        setContentDescription(getContext().getString(style.getLabel()));
    }
}
