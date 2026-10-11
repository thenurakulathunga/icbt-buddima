package com.printxpress.app.core.validation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Validates a whole form: each named field owns a {@link CompositeRule}.
 * <p>
 * Pattern role: Composite + Strategy. ViewModels build one validator per form, validate single
 * fields when they lose focus and all fields on submit (docs/05 §9 rule 2). The result map keeps
 * the order in which fields were added, so the screen can focus the first error. The validator is
 * view-free; fragments map field keys to {@code PxTextInput} components to show the errors.
 * </p>
 *
 * @since 1.0
 */
public final class FormValidator {

    /** Field key to rule set, in insertion order. */
    private final Map<String, CompositeRule> fields = new LinkedHashMap<>();

    /** Creates a validator with no fields; add them with {@link #field}. */
    public FormValidator() {
        super();
    }

    /**
     * Registers a field and its rules; calling again for the same key appends rules.
     *
     * @param key   the field key
     * @param rules the rules, evaluated in order
     * @return this validator for chaining
     */
    public FormValidator field(String key, ValidationRule... rules) {
        CompositeRule composite = fields.get(key);
        if (composite == null) {
            composite = new CompositeRule();
            fields.put(key, composite);
        }
        for (ValidationRule rule : rules) {
            composite.add(rule);
        }
        return this;
    }

    /**
     * Validates one field.
     *
     * @param key   the field key
     * @param value the field value
     * @return the outcome; unknown keys are valid
     */
    public ValidationResult validate(String key, String value) {
        CompositeRule composite = fields.get(key);
        return composite == null ? ValidationResult.valid() : composite.validate(value);
    }

    /**
     * Validates every registered field.
     *
     * @param values field key to value; missing keys are validated as null
     * @return field key to error resource id for each failing field, in registration order
     */
    public Map<String, Integer> validateAll(Map<String, String> values) {
        Map<String, Integer> errors = new LinkedHashMap<>();
        for (Map.Entry<String, CompositeRule> entry : fields.entrySet()) {
            ValidationResult result = entry.getValue().validate(values.get(entry.getKey()));
            if (!result.isValid()) {
                errors.put(entry.getKey(), result.getErrorRes());
            }
        }
        return Collections.unmodifiableMap(errors);
    }

    /**
     * Convenience check for a whole form.
     *
     * @param values field key to value
     * @return true when every field is valid
     */
    public boolean isValid(Map<String, String> values) {
        return validateAll(values).isEmpty();
    }
}
