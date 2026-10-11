package com.printxpress.app.data.model.enums;

/**
 * Support ticket progress, stored in {@code support_tickets.status}.
 *
 * @since 1.0
 */
public enum TicketStatus {
    /** Newly raised. */
    OPEN,
    /** Being handled by support staff. */
    IN_PROGRESS,
    /** Closed with an answer. */
    RESOLVED;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the status, or {@link #OPEN} for unknown text
     */
    public static TicketStatus from(String value) {
        try {
            return value == null ? OPEN : valueOf(value);
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
