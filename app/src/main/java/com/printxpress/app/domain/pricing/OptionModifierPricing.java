package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

import java.math.BigDecimal;

/**
 * Applies option modifiers: the percentages of every chosen option are summed and added, e.g.
 * Double sided (+40%) and Spot UV (+25%) raise the price by 65%. The total factor never drops
 * below zero.
 * <p>Pattern role: Concrete Strategy.</p>
 *
 * @since 1.0
 */
public final class OptionModifierPricing implements PricingStrategy {

    /** Creates a stateless instance. */
    public OptionModifierPricing() {
        super();
    }

    /**
     * Multiplies by {@code 1 + sum(modifiers) / 100}.
     *
     * @param context the pricing inputs
     * @param current the base amount
     * @return the amount including options
     */
    @Override
    public Money apply(PricingContext context, Money current) {
        return current.times(factor(context));
    }

    /**
     * Computes the combined option factor.
     *
     * @param context the pricing inputs
     * @return the multiplier, at least zero
     */
    static BigDecimal factor(PricingContext context) {
        double percent = 0;
        for (Double modifier : context.getModifiers()) {
            percent += modifier == null ? 0 : modifier;
        }
        BigDecimal f = BigDecimal.ONE.add(BigDecimal.valueOf(percent).movePointLeft(2));
        return f.signum() < 0 ? BigDecimal.ZERO : f;
    }
}
