package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.Promotion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable inputs shared by every {@link PricingStrategy}: unit price, quantity, option
 * modifiers, and, at cart level, the promotion and the amount it may discount.
 *
 * @since 1.0
 */
public final class PricingContext {

    /** Unit price before options. */
    private final Money unitPrice;
    /** Quantity ordered. */
    private final int quantity;
    /** Option modifiers as percentages. */
    private final List<Double> modifiers;
    /** Promotion to apply, may be null. */
    private final Promotion promotion;
    /** Portion of the subtotal the promotion may discount. */
    private final Money promoEligibleAmount;

    /**
     * Creates a context.
     *
     * @param unitPrice           unit price before options
     * @param quantity            quantity
     * @param modifiers           option modifier percentages
     * @param promotion           promotion, may be null
     * @param promoEligibleAmount amount the promotion may discount
     */
    private PricingContext(Money unitPrice, int quantity, List<Double> modifiers, Promotion promotion,
                           Money promoEligibleAmount) {
        this.unitPrice = unitPrice == null ? Money.ZERO : unitPrice;
        this.quantity = quantity;
        this.modifiers = Collections.unmodifiableList(new ArrayList<>(modifiers));
        this.promotion = promotion;
        this.promoEligibleAmount = promoEligibleAmount == null ? Money.ZERO : promoEligibleAmount;
    }

    /**
     * Creates a context for pricing one line.
     *
     * @param unitPrice unit price before options
     * @param quantity  quantity
     * @param modifiers option modifier percentages
     * @return the context
     */
    public static PricingContext forLine(Money unitPrice, int quantity, List<Double> modifiers) {
        return new PricingContext(unitPrice, quantity, modifiers == null ? Collections.emptyList() : modifiers,
                null, Money.ZERO);
    }

    /**
     * Creates a context for applying a promotion to a cart.
     *
     * @param promotion      the promotion
     * @param eligibleAmount the part of the subtotal it covers
     * @return the context
     */
    public static PricingContext forPromotion(Promotion promotion, Money eligibleAmount) {
        return new PricingContext(Money.ZERO, 0, Collections.emptyList(), promotion, eligibleAmount);
    }

    /** @return the unit price before options */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /** @return the quantity */
    public int getQuantity() {
        return quantity;
    }

    /** @return the option modifier percentages (unmodifiable) */
    public List<Double> getModifiers() {
        return modifiers;
    }

    /** @return the promotion, may be null */
    public Promotion getPromotion() {
        return promotion;
    }

    /** @return the amount the promotion may discount */
    public Money getPromoEligibleAmount() {
        return promoEligibleAmount;
    }
}
