package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

/**
 * Immutable price summary shown by {@code PriceSummaryView}: items, bulk discount, subtotal,
 * promo discount, delivery fee and total (FR-2.11).
 *
 * @since 1.0
 */
public final class PriceBreakdown {

    /** An all-zero breakdown. */
    public static final PriceBreakdown EMPTY =
            new PriceBreakdown(Money.ZERO, Money.ZERO, Money.ZERO, null, Money.ZERO, false);

    /** Items before bulk discount. */
    private final Money itemsGross;
    /** Total bulk discount. */
    private final Money bulkDiscount;
    /** Promo discount. */
    private final Money promoDiscount;
    /** Applied promo code, may be null. */
    private final String promoCode;
    /** Delivery fee. */
    private final Money deliveryFee;
    /** Whether a delivery line should be shown. */
    private final boolean showDelivery;

    /**
     * Creates a breakdown.
     *
     * @param itemsGross    items before bulk discount
     * @param bulkDiscount  total bulk discount
     * @param promoDiscount promo discount
     * @param promoCode     applied code, may be null
     * @param deliveryFee   delivery fee
     * @param showDelivery  whether to show the delivery line
     */
    public PriceBreakdown(Money itemsGross, Money bulkDiscount, Money promoDiscount, String promoCode,
                          Money deliveryFee, boolean showDelivery) {
        this.itemsGross = itemsGross;
        this.bulkDiscount = bulkDiscount;
        this.promoDiscount = promoDiscount;
        this.promoCode = promoCode;
        this.deliveryFee = deliveryFee;
        this.showDelivery = showDelivery;
    }

    /**
     * Creates a breakdown for a placed order from its stored amounts.
     *
     * @param subtotal    stored subtotal (after bulk)
     * @param discount    stored promo discount
     * @param deliveryFee stored delivery fee
     * @param promoCode   applied code, may be null
     * @param delivery    whether the order is delivered
     * @return the breakdown
     */
    public static PriceBreakdown ofOrder(Money subtotal, Money discount, Money deliveryFee, String promoCode,
                                         boolean delivery) {
        return new PriceBreakdown(subtotal, Money.ZERO, discount, promoCode, deliveryFee, delivery);
    }

    /** @return items before bulk discount */
    public Money getItemsGross() {
        return itemsGross;
    }

    /** @return total bulk discount */
    public Money getBulkDiscount() {
        return bulkDiscount;
    }

    /** @return items after bulk discount */
    public Money getSubtotal() {
        return itemsGross.minus(bulkDiscount).nonNegative();
    }

    /** @return the promo discount */
    public Money getPromoDiscount() {
        return promoDiscount;
    }

    /** @return the applied promo code, may be null */
    public String getPromoCode() {
        return promoCode;
    }

    /** @return the delivery fee */
    public Money getDeliveryFee() {
        return deliveryFee;
    }

    /** @return whether the delivery line should be shown */
    public boolean isShowDelivery() {
        return showDelivery;
    }

    /** @return subtotal minus promo plus delivery, never negative */
    public Money getTotal() {
        return getSubtotal().minus(promoDiscount).nonNegative().plus(deliveryFee);
    }
}
