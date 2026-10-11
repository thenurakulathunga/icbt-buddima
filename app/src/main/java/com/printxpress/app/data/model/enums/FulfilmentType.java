package com.printxpress.app.data.model.enums;

/**
 * How an order reaches the customer, stored in {@code orders.fulfilment_type}.
 *
 * @since 1.0
 */
public enum FulfilmentType {
    /** Collected at a branch. */
    PICKUP,
    /** Delivered to a saved address. */
    DELIVERY;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the type, or {@link #PICKUP} for unknown text
     */
    public static FulfilmentType from(String value) {
        return DELIVERY.name().equals(value) ? DELIVERY : PICKUP;
    }
}
