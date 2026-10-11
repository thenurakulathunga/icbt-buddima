package com.printxpress.app.data.model.enums;

/**
 * Category of an in-app notification, stored in {@code notifications.type}.
 *
 * @since 1.0
 */
public enum NotificationType {
    /** About an order's progress. */
    ORDER,
    /** About a promotion or offer. */
    PROMO,
    /** General account or app messages. */
    SYSTEM;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the type, or {@link #SYSTEM} for unknown text
     */
    public static NotificationType from(String value) {
        try {
            return value == null ? SYSTEM : valueOf(value);
        } catch (IllegalArgumentException e) {
            return SYSTEM;
        }
    }
}
