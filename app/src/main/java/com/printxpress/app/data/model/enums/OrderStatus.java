package com.printxpress.app.data.model.enums;

/**
 * Order lifecycle stored in {@code orders.status}. Allowed transitions and actions are decided
 * by the {@code OrderState} classes, not by this enum.
 *
 * @since 1.0
 */
public enum OrderStatus {
    /** Order placed, awaiting confirmation. */
    PENDING,
    /** Order accepted by the shop. */
    CONFIRMED,
    /** Artwork being checked and prepared. */
    PROCESSING,
    /** On the press; cancel and reschedule are no longer allowed. */
    PRINTING,
    /** Printed and waiting at the branch. */
    READY_FOR_PICKUP,
    /** Printed and on the way to the customer. */
    OUT_FOR_DELIVERY,
    /** Collected or delivered. */
    COMPLETED,
    /** Cancelled by the customer. */
    CANCELLED;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the status, or {@link #PENDING} for unknown text
     */
    public static OrderStatus from(String value) {
        try {
            return value == null ? PENDING : valueOf(value);
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }

    /** @return true for COMPLETED and CANCELLED */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }
}
