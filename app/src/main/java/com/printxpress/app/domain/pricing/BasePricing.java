package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

/**
 * Starts a line price: unit price multiplied by quantity. Ignores the incoming amount.
 * <p>Pattern role: Concrete Strategy (first link of the chain).</p>
 *
 * @since 1.0
 */
public final class BasePricing implements PricingStrategy {

    /** Creates a stateless instance. */
    public BasePricing() {
        super();
    }

    /**
     * Calculates unit price times quantity.
     *
     * @param context the pricing inputs
     * @param current ignored
     * @return the base line amount
     */
    @Override
    public Money apply(PricingContext context, Money current) {
        return context.getUnitPrice().times(context.getQuantity());
    }
}
