package com.printxpress.app.core.validation.rules;

import com.printxpress.app.core.validation.ValidationResult;
import com.printxpress.app.core.validation.ValidationRule;

/**
 * Base class for rules: stores the error message and applies the "blank is valid" convention.
 * <p>
 * Pattern role: Template Method. {@link #validate(String)} fixes the algorithm (blank check, then
 * {@link #isSatisfiedBy(String)}) and subclasses supply only the specific test.
 * </p>
 *
 * @since 1.0
 */
public abstract class AbstractRule implements ValidationRule {

    /** Error message resource id returned on failure. */
    private final int errorRes;

    /**
     * Creates a rule.
     *
     * @param errorRes string resource id shown when the rule fails
     */
    protected AbstractRule(int errorRes) {
        this.errorRes = errorRes;
    }

    /**
     * Validates a value using the template: blank values are delegated to {@link #validateBlank()}.
     *
     * @param value the raw input
     * @return the outcome
     */
    @Override
    public final ValidationResult validate(String value) {
        if (ValidationRule.isBlank(value)) {
            return validateBlank();
        }
        return isSatisfiedBy(value.trim()) ? ValidationResult.valid() : fail();
    }

    /**
     * Decides the outcome for a blank value; optional by default.
     *
     * @return valid unless overridden by a "required" rule
     */
    protected ValidationResult validateBlank() {
        return ValidationResult.valid();
    }

    /**
     * Tests a non-blank, trimmed value.
     *
     * @param value the trimmed input
     * @return true when the value passes
     */
    protected abstract boolean isSatisfiedBy(String value);

    /** @return an invalid result carrying this rule's message */
    protected final ValidationResult fail() {
        return ValidationResult.invalid(errorRes);
    }

    /** @return the error message resource id */
    public int getErrorRes() {
        return errorRes;
    }
}
