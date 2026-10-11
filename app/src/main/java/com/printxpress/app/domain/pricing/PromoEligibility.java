package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.Promotion;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Decides whether a promo code can be used on a cart and, if not, why (BR-5, TC-38, TC-39).
 * Pure Java so every outcome is unit-testable.
 *
 * @since 1.0
 */
public final class PromoEligibility {

    /** Code that is limited to a customer's first order. */
    public static final String FIRST_ORDER_CODE = "WELCOME10";

    /** Possible outcomes of a promo check. */
    public enum Result {
        /** The code can be applied. */
        OK,
        /** No promotion has this code. */
        NOT_FOUND,
        /** The code exists but is switched off or outside its dates. */
        EXPIRED,
        /** The eligible subtotal is below the minimum order value. */
        BELOW_MINIMUM,
        /** No cart line belongs to the targeted category. */
        NOT_APPLICABLE,
        /** The code is for first orders and the customer has ordered before. */
        FIRST_ORDER_ONLY
    }

    /** Hidden constructor: static checks only. */
    private PromoEligibility() {
    }

    /**
     * Normalises typed input to the stored upper-case form.
     *
     * @param raw the typed code
     * @return the trimmed upper-case code, or an empty string
     */
    public static String normalise(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.US);
    }

    /**
     * Checks a promotion against a cart.
     *
     * @param promotion      the promotion found by code, or null
     * @param today          the current date
     * @param eligibleAmount the part of the subtotal in targeted categories
     * @param previousOrders how many orders the customer has placed before
     * @return the outcome
     */
    public static Result check(Promotion promotion, LocalDate today, Money eligibleAmount, int previousOrders) {
        if (promotion == null) {
            return Result.NOT_FOUND;
        }
        if (!promotion.isValidOn(today)) {
            return Result.EXPIRED;
        }
        if (FIRST_ORDER_CODE.equals(promotion.getPromoCode()) && previousOrders > 0) {
            return Result.FIRST_ORDER_ONLY;
        }
        if (eligibleAmount == null || !eligibleAmount.isPositive()) {
            return Result.NOT_APPLICABLE;
        }
        if (!promotion.meetsMinimum(eligibleAmount)) {
            return Result.BELOW_MINIMUM;
        }
        return Result.OK;
    }
}
