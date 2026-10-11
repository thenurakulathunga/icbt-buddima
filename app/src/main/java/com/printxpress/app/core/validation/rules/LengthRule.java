package com.printxpress.app.core.validation.rules;

/**
 * Checks that the trimmed length lies within an inclusive range.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class LengthRule extends AbstractRule {

    /** Minimum length, inclusive. */
    private final int min;
    /** Maximum length, inclusive. */
    private final int max;

    /**
     * Creates the rule.
     *
     * @param min      the minimum length
     * @param max      the maximum length
     * @param errorRes message shown when the length is out of range
     */
    public LengthRule(int min, int max, int errorRes) {
        super(errorRes);
        this.min = min;
        this.max = max;
    }

    /**
     * Checks the length in code points.
     *
     * @param value the trimmed input
     * @return true when {@code min <= length <= max}
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        int length = value.codePointCount(0, value.length());
        return length >= min && length <= max;
    }
}
