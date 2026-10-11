package com.printxpress.app.data.model.enums;

/**
 * How a promotion's value is applied, stored in {@code promotions.discount_type}.
 *
 * @since 1.0
 */
public enum DiscountType {
    /** Percentage off the eligible amount. */
    PERCENT,
    /** Fixed rupee amount off, capped at the eligible amount. */
    FIXED;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the type, or {@link #PERCENT} for unknown text
     */
    public static DiscountType from(String value) {
        return FIXED.name().equals(value) ? FIXED : PERCENT;
    }
}
