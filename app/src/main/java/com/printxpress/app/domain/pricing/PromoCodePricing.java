package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.DiscountType;

/**
 * Applies a promo code to the cart subtotal (BR-5).
 * <p>
 * A percentage code discounts the eligible amount; a fixed code takes its value off, capped at the
 * eligible amount. Nothing is taken off when the minimum order value is not met. Date validity is
 * checked earlier by {@link PromoEligibility}.
 * </p>
 * <p>Pattern role: Concrete Strategy.</p>
 *
 * @since 1.0
 */
public final class PromoCodePricing implements PricingStrategy {

    /** Creates a stateless instance. */
    public PromoCodePricing() {
        super();
    }

    /**
     * Subtracts the promo discount.
     *
     * @param context the promotion and eligible amount
     * @param current the subtotal
     * @return the subtotal after the promo
     */
    @Override
    public Money apply(PricingContext context, Money current) {
        return current.minus(discountFor(context.getPromotion(), context.getPromoEligibleAmount()));
    }

    /**
     * Calculates the discount a promotion gives on an eligible amount.
     *
     * @param promotion the promotion, may be null
     * @param eligible  the amount it may discount
     * @return the discount, zero when not applicable
     */
    public static Money discountFor(Promotion promotion, Money eligible) {
        if (promotion == null || eligible == null || !eligible.isPositive() || !promotion.meetsMinimum(eligible)) {
            return Money.ZERO;
        }
        if (promotion.getDiscountType() == DiscountType.FIXED) {
            return Money.of(promotion.getDiscountValue()).min(eligible);
        }
        return eligible.percent(promotion.getDiscountValue()).min(eligible);
    }
}
