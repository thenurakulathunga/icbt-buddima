package com.printxpress.app.core.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A rule made of other rules; the first failing child decides the result.
 * <p>
 * Pattern role: Composite. A field's full rule set and a single rule share the
 * {@link ValidationRule} interface, so callers never need to know which one they hold.
 * </p>
 *
 * @since 1.0
 */
public final class CompositeRule implements ValidationRule {

    /** Child rules in evaluation order. */
    private final List<ValidationRule> rules;

    /**
     * Creates a composite.
     *
     * @param rules child rules in evaluation order
     */
    public CompositeRule(ValidationRule... rules) {
        this.rules = new ArrayList<>(Arrays.asList(rules));
    }

    /**
     * Appends a child rule.
     *
     * @param rule the rule to add
     * @return this composite for chaining
     */
    public CompositeRule add(ValidationRule rule) {
        rules.add(rule);
        return this;
    }

    /** @return an unmodifiable view of the child rules */
    public List<ValidationRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Runs the child rules in order and stops at the first failure.
     *
     * @param value the raw input
     * @return the first failure, or valid
     */
    @Override
    public ValidationResult validate(String value) {
        for (ValidationRule rule : rules) {
            ValidationResult result = rule.validate(value);
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.valid();
    }
}
