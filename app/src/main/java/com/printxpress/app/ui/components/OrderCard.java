package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.databinding.ViewOrderCardBinding;

/**
 * Order summary card: number, status badge, items, schedule, date and total (FR-3.1).
 *
 * @since 1.0
 */
public class OrderCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewOrderCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public OrderCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewOrderCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows an order.
     *
     * @param order the order with summary columns
     */
    public void bind(Order order) {
        binding.imgThumb.setArtwork(order.getFirstProductImageRes(), "ic_package");
        binding.txtNumber.setText(order.getOrderNumber());
        binding.badgeStatus.setStatus(order.getStatus());
        String first = order.getFirstProductName() == null ? "" : order.getFirstProductName();
        binding.txtItems.setText(order.getItemCount() > 1
                ? getContext().getString(R.string.order_items_more, first, order.getItemCount() - 1)
                : getContext().getString(R.string.order_items_one, first));
        int scheduleRes = order.getFulfilmentType() == FulfilmentType.DELIVERY
                ? R.string.order_schedule_delivery : R.string.order_schedule_pickup;
        binding.txtSchedule.setText(getContext().getString(scheduleRes,
                DateTimeUtils.formatDisplay(order.getScheduledDate()), order.getScheduledSlot()));
        binding.txtDate.setText(getContext().getString(R.string.order_placed_on,
                DateTimeUtils.formatDisplay(order.getCreatedAt())));
        binding.txtTotal.setText(CurrencyFormatter.format(order.getTotal()));
        setContentDescription(order.getOrderNumber() + ", " + binding.badgeStatus.getText() + ", "
                + binding.txtTotal.getText());
    }
}
