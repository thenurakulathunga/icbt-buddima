package com.printxpress.app.core.util;

import com.printxpress.app.core.common.Money;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats rupee amounts in the house style {@code Rs. 1,250.00} (docs/05 §9).
 *
 * @since 1.0
 */
public final class CurrencyFormatter {

    /** Currency prefix used across the app. */
    private static final String PREFIX = "Rs. ";
    /** Grouped two-decimal pattern. */
    private static final String PATTERN = "#,##0.00";

    /** Hidden constructor: utility class. */
    private CurrencyFormatter() {
    }

    /**
     * Formats an amount, e.g. {@code Rs. 1,250.00}; negative amounts get a leading minus.
     *
     * @param money the amount, null is treated as zero
     * @return the formatted text
     */
    public static String format(Money money) {
        Money value = money == null ? Money.ZERO : money;
        DecimalFormat format = new DecimalFormat(PATTERN, DecimalFormatSymbols.getInstance(Locale.US));
        String digits = format.format(value.getAmount().abs());
        return (value.isNegative() ? "- " : "") + PREFIX + digits;
    }

    /**
     * Formats a discount amount with a leading minus, e.g. {@code - Rs. 150.00}.
     *
     * @param discount the positive discount amount
     * @return the formatted text
     */
    public static String formatDiscount(Money discount) {
        Money value = discount == null ? Money.ZERO : discount;
        return value.isZero() ? format(value) : format(Money.ZERO.minus(value.nonNegative()));
    }
}
