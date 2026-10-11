package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Money;

/**
 * BR-4 bulk discount: 10% off a line of 500 or more units, 15% off 1000 or more.
 * <p>Pattern role: Concrete Strategy.</p>
 *
 * @since 1.0
 */
public final class BulkDiscountPricing implements PricingStrategy {

    /** Creates a stateless instance. */
    public BulkDiscountPricing() {
        super();
    }

    /**
     * Subtracts the bulk discount for the line quantity.
     *
     * @param context the pricing inputs
     * @param current the amount including options
     * @return the discounted amount
     */
    @Override
    public Money apply(PricingContext context, Money current) {
        return current.minus(current.percent(percentFor(context.getQuantity())));
    }

    /**
     * Returns the bulk discount percentage for a quantity.
     *
     * @param quantity the line quantity
     * @return 0, 10 or 15
     */
    public static double percentFor(int quantity) {
        if (quantity >= Constants.BULK_TIER_2_QTY) {
            return Constants.BULK_TIER_2_PERCENT;
        }
        if (quantity >= Constants.BULK_TIER_1_QTY) {
            return Constants.BULK_TIER_1_PERCENT;
        }
        return 0;
    }
}
