package com.printxpress.app.data.model.enums;

/**
 * Offline payment options (docs/01 assumption 2), stored in {@code orders.payment_method}.
 *
 * @since 1.0
 */
public enum PaymentMethod {
    /** Pay when collecting at the branch. */
    PAY_AT_PICKUP,
    /** Pay the courier on delivery. */
    CASH_ON_DELIVERY;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the method, or {@link #PAY_AT_PICKUP} for unknown text
     */
    public static PaymentMethod from(String value) {
        return CASH_ON_DELIVERY.name().equals(value) ? CASH_ON_DELIVERY : PAY_AT_PICKUP;
    }

    /**
     * Returns the method that matches a fulfilment type.
     *
     * @param type the fulfilment type
     * @return pay at pickup for pickup, cash on delivery for delivery
     */
    public static PaymentMethod forFulfilment(FulfilmentType type) {
        return type == FulfilmentType.DELIVERY ? CASH_ON_DELIVERY : PAY_AT_PICKUP;
    }
}
