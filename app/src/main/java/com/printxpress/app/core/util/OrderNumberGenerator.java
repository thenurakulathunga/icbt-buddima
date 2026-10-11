package com.printxpress.app.core.util;

import com.printxpress.app.core.common.Constants;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Builds human-readable order numbers in the format {@code PX-YYMMDD-NNNN} (BR-11).
 *
 * @since 1.0
 */
public final class OrderNumberGenerator {

    /** Largest sequence that fits in four digits. */
    private static final int MAX_SEQUENCE = 9999;

    /** Hidden constructor: utility class. */
    private OrderNumberGenerator() {
    }

    /**
     * Generates an order number.
     *
     * @param date     the order date
     * @param sequence the 1-based order count for that day
     * @return e.g. {@code PX-261006-0001}
     * @throws IllegalArgumentException when the sequence is outside 1–9999
     */
    public static String generate(LocalDate date, int sequence) {
        if (sequence < 1 || sequence > MAX_SEQUENCE) {
            throw new IllegalArgumentException("sequence must be 1-" + MAX_SEQUENCE);
        }
        return String.format(Locale.US, "%s-%s-%04d", Constants.ORDER_NUMBER_PREFIX,
                date.format(DateTimeUtils.ORDER_NUMBER_DATE), sequence);
    }

    /**
     * Returns the prefix shared by all order numbers of one day, used to count them.
     *
     * @param date the order date
     * @return e.g. {@code PX-261006-}
     */
    public static String dayPrefix(LocalDate date) {
        return Constants.ORDER_NUMBER_PREFIX + "-" + date.format(DateTimeUtils.ORDER_NUMBER_DATE) + "-";
    }

    /**
     * Checks whether a string matches the order number format.
     *
     * @param orderNumber the candidate
     * @return true when it matches {@code PX-YYMMDD-NNNN}
     */
    public static boolean isValid(String orderNumber) {
        return orderNumber != null && orderNumber.matches("^PX-\\d{6}-\\d{4}$");
    }
}
