package com.printxpress.app.core.validation;

/**
 * Strategy contract for validating one text value.
 * <p>
 * Pattern role: Strategy. Rules are pure Java so they can be unit-tested on the JVM. By
 * convention every rule except the "required" rules treats a blank value as valid, so optional
 * fields only need the format rule and required fields add {@code RequiredRule} first.
 * </p>
 *
 * @since 1.0
 */
public interface ValidationRule {

    /**
     * Validates a value.
     *
     * @param value the raw input, may be null
     * @return the outcome
     */
    ValidationResult validate(String value);

    /**
     * Checks whether a value is null or only whitespace.
     *
     * @param value the value
     * @return true when blank
     */
    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
