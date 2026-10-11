package com.printxpress.app.core.validation.rules;

import java.util.function.IntSupplier;

/**
 * Checks that a whole number lies in an inclusive range; non-numeric input fails.
 * Bounds are suppliers so one rule can follow the selected product (BR-3).
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class RangeRule extends AbstractRule {

    /** Supplies the minimum, inclusive. */
    private final IntSupplier min;
    /** Supplies the maximum, inclusive. */
    private final IntSupplier max;

    /**
     * Creates a rule with fixed bounds.
     *
     * @param min      the minimum
     * @param max      the maximum
     * @param errorRes message shown when out of range or not a number
     */
    public RangeRule(int min, int max, int errorRes) {
        this(() -> min, () -> max, errorRes);
    }

    /**
     * Creates a rule with dynamic bounds.
     *
     * @param min      supplies the minimum
     * @param max      supplies the maximum
     * @param errorRes message shown when out of range or not a number
     */
    public RangeRule(IntSupplier min, IntSupplier max, int errorRes) {
        super(errorRes);
        this.min = min;
        this.max = max;
    }

    /**
     * Parses and range-checks the value.
     *
     * @param value the trimmed input
     * @return true for a whole number within the bounds
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        try {
            long number = Long.parseLong(value);
            return number >= min.getAsInt() && number <= max.getAsInt();
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
