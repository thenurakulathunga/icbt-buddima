package com.printxpress.app.core.validation.rules;

import com.printxpress.app.core.validation.ValidationResult;
import com.printxpress.app.core.validation.ValidationRule;

import java.util.function.Supplier;

/**
 * Makes a field required only when a partner field is empty, for example email is required when
 * no phone number is given (BR-1 needs at least one of them).
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class RequiredIfEmptyRule extends AbstractRule {

    /** Supplies the partner field's current value. */
    private final Supplier<String> partner;

    /**
     * Creates the rule.
     *
     * @param partner  supplies the other field's value
     * @param errorRes message shown when both fields are empty
     */
    public RequiredIfEmptyRule(Supplier<String> partner, int errorRes) {
        super(errorRes);
        this.partner = partner;
    }

    /** @return invalid when the partner field is also blank */
    @Override
    protected ValidationResult validateBlank() {
        return ValidationRule.isBlank(partner.get()) ? fail() : ValidationResult.valid();
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
