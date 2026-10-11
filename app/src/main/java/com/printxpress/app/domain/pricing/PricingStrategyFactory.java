package com.printxpress.app.domain.pricing;

/**
 * Builds the pricing chains so callers never assemble concrete strategies themselves.
 * <p>Pattern role: Factory.</p>
 *
 * @since 1.0
 */
public final class PricingStrategyFactory {

    /** Hidden constructor: static factory. */
    private PricingStrategyFactory() {
    }

    /** @return base price plus options, before any discount */
    public static PricingStrategy grossLine() {
        return new CompositePricingStrategy(new BasePricing(), new OptionModifierPricing());
    }

    /** @return base price plus options minus the bulk discount */
    public static PricingStrategy netLine() {
        return new CompositePricingStrategy(new BasePricing(), new OptionModifierPricing(), new BulkDiscountPricing());
    }

    /** @return the cart-level promo strategy */
    public static PricingStrategy promotion() {
        return new CompositePricingStrategy(new PromoCodePricing());
    }
}
