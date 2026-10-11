package com.printxpress.app.core.validation.rules;

import java.util.function.Supplier;

/**
 * Checks that a value equals another field's value, e.g. confirm password.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class MatchRule extends AbstractRule {

    /** Supplies the value to match. */
    private final Supplier<String> other;

    /**
     * Creates the rule.
     *
     * @param other    supplies the value that must be matched
     * @param errorRes message shown on mismatch
     */
    public MatchRule(Supplier<String> other, int errorRes) {
        super(errorRes);
        this.other = other;
    }

    /**
     * Compares with the other value, trimmed on both sides and case-sensitive.
     *
     * @param value the trimmed input
     * @return true when equal
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        String target = other.get();
        return target != null && value.equals(target.trim());
    }
}
