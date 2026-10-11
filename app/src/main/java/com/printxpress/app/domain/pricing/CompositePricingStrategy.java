package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Runs several strategies in order, each receiving the previous result: base, then options,
 * then bulk, then promo.
 * <p>
 * Pattern role: Composite (a group of strategies is itself a strategy) with a light Chain of
 * Responsibility, because each link adjusts the running amount and passes it on.
 * </p>
 *
 * @since 1.0
 */
public final class CompositePricingStrategy implements PricingStrategy {

    /** The ordered child strategies. */
    private final List<PricingStrategy> strategies;

    /**
     * Creates a composite.
     *
     * @param strategies the child strategies in application order
     */
    public CompositePricingStrategy(PricingStrategy... strategies) {
        this.strategies = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(strategies)));
    }

    /**
     * Applies every child in order.
     *
     * @param context the pricing inputs
     * @param current the starting amount
     * @return the final amount, never negative
     */
    @Override
    public Money apply(PricingContext context, Money current) {
        Money running = current;
        for (PricingStrategy strategy : strategies) {
            running = strategy.apply(context, running);
        }
        return running.nonNegative();
    }

    /** @return the child strategies (unmodifiable) */
    public List<PricingStrategy> getStrategies() {
        return strategies;
    }
}
