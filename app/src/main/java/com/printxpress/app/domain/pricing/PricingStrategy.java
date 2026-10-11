package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

/**
 * Strategy contract for calculating a price adjustment on an order line or cart.
 * <p>
 * Pattern role: Strategy. New discount rules are added as new classes without changing the
 * calculator (Open/Closed principle).
 * </p>
 *
 * @since 1.0
 */
public interface PricingStrategy {

    /**
     * Applies this strategy to a running price.
     *
     * @param context the immutable pricing inputs (quantity, options, promo)
     * @param current the price calculated so far
     * @return the adjusted price
     */
    Money apply(PricingContext context, Money current);
}
