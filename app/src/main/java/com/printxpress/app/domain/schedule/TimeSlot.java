package com.printxpress.app.domain.schedule;

/**
 * Pickup and delivery time slots offered by {@code DateSlotPicker}. The label is what is stored
 * in {@code orders.scheduled_slot}.
 *
 * @since 1.0
 */
public enum TimeSlot {
    /** 09:00 to 12:00. */
    MORNING("09:00 - 12:00"),
    /** 12:00 to 15:00. */
    MIDDAY("12:00 - 15:00"),
    /** 15:00 to 18:00. */
    AFTERNOON("15:00 - 18:00");

    /** Stored and displayed label. */
    private final String label;

    /**
     * Creates a slot.
     *
     * @param label the label
     */
    TimeSlot(String label) {
        this.label = label;
    }

    /** @return the stored label */
    public String getLabel() {
        return label;
    }

    /**
     * Finds a slot by its stored label.
     *
     * @param label the label
     * @return the slot, or null when unknown
     */
    public static TimeSlot fromLabel(String label) {
        for (TimeSlot slot : values()) {
            if (slot.label.equals(label)) {
                return slot;
            }
        }
        return null;
    }
}
