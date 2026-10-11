package com.printxpress.app.core.validation.rules;

import com.printxpress.app.core.validation.ValidationResult;

/**
 * Fails when the value is null, empty or whitespace only.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class RequiredRule extends AbstractRule {

    /**
     * Creates the rule.
     *
     * @param errorRes message shown when the value is missing
     */
    public RequiredRule(int errorRes) {
        super(errorRes);
    }

    /** @return always invalid, because a required value is missing */
    @Override
    protected ValidationResult validateBlank() {
        return fail();
    }

    /**
     * Any non-blank value passes.
     *
     * @param value the trimmed input
     * @return true
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        return true;
    }
}
