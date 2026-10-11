package com.printxpress.app.core.validation.rules;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;

/**
 * Checks that an ISO date ({@code yyyy-MM-dd}) is on or after a minimum date, e.g. today plus
 * the turnaround days (BR-6).
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class MinDateRule extends AbstractRule {

    /** Supplies the earliest allowed date. */
    private final Supplier<LocalDate> min;

    /**
     * Creates the rule.
     *
     * @param min      supplies the earliest valid date
     * @param errorRes message shown for an early or malformed date
     */
    public MinDateRule(Supplier<LocalDate> min, int errorRes) {
        super(errorRes);
        this.min = min;
    }

    /**
     * Parses and compares the date.
     *
     * @param value the trimmed ISO date
     * @return true when the date is not before the minimum
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        try {
            return !LocalDate.parse(value).isBefore(min.get());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
