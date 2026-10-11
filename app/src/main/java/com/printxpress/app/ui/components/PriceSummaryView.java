package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.printxpress.app.R;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.databinding.ViewPriceSummaryBinding;
import com.printxpress.app.domain.pricing.PriceBreakdown;

/**
 * Price breakdown: items, bulk discount, promo, delivery and total (FR-2.11). Zero discount rows
 * are hidden; delivery shows only for delivery orders.
 *
 * @since 1.0
 */
public class PriceSummaryView extends LinearLayout {

    /** The inflated children. */
    private final ViewPriceSummaryBinding binding;

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PriceSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewPriceSummaryBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a breakdown.
     *
     * @param price the breakdown
     */
    public void bind(PriceBreakdown price) {
        binding.valItems.setText(CurrencyFormatter.format(price.getItemsGross()));
        showRow(binding.rowBulk, binding.valBulk, price.getBulkDiscount().isPositive(),
                CurrencyFormatter.formatDiscount(price.getBulkDiscount()));
        showRow(binding.rowPromo, binding.valPromo, price.getPromoDiscount().isPositive(),
                CurrencyFormatter.formatDiscount(price.getPromoDiscount()));
        if (price.getPromoCode() != null) {
            binding.lblPromo.setText(getContext().getString(R.string.price_promo, price.getPromoCode()));
        }
        showRow(binding.rowDelivery, binding.valDelivery, price.isShowDelivery(),
                price.getDeliveryFee().isZero() ? getContext().getString(R.string.price_delivery_free)
                        : CurrencyFormatter.format(price.getDeliveryFee()));
        binding.valTotal.setText(CurrencyFormatter.format(price.getTotal()));
    }

    /**
     * Shows or hides one row.
     *
     * @param row     the row container
     * @param value   the value text view
     * @param visible whether to show it
     * @param text    the value text
     */
    private static void showRow(View row, TextView value, boolean visible, String text) {
        row.setVisibility(visible ? VISIBLE : GONE);
        value.setText(text);
    }
}
