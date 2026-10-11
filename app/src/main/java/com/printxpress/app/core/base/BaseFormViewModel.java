package com.printxpress.app.core.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.validation.FormValidator;
import com.printxpress.app.core.validation.ValidationResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base for ViewModels that own a form: keeps field values, validates one field when it loses
 * focus and every field on submit (docs/05 §9 rule 2), and publishes errors for {@code FormBinder}.
 * <p>
 * Pattern role: Template Method. Subclasses register their rules in {@link #validator} and call
 * {@link #validateAll()} before submitting; values survive rotation because they live here.
 * </p>
 *
 * @since 1.0
 */
public abstract class BaseFormViewModel extends BaseViewModel {

    /** For subclasses only. */
    protected BaseFormViewModel() {
        super();
    }

    /**
     * Errors to show, with a flag telling the screen whether to focus the first one.
     */
    public static final class FieldErrors {

        /** Field key to message resource id. */
        private final Map<String, Integer> errors;
        /** Whether to move focus to the first error. */
        private final boolean focusFirst;

        /**
         * Creates the value.
         *
         * @param errors     field key to message
         * @param focusFirst whether to focus the first error
         */
        FieldErrors(Map<String, Integer> errors, boolean focusFirst) {
            this.errors = Collections.unmodifiableMap(new LinkedHashMap<>(errors));
            this.focusFirst = focusFirst;
        }

        /** @return field key to message resource id */
        public Map<String, Integer> getErrors() {
            return errors;
        }

        /** @return whether to focus the first error */
        public boolean isFocusFirst() {
            return focusFirst;
        }
    }

    /** The form's rules. */
    protected final FormValidator validator = new FormValidator();
    /** Current field values. */
    private final Map<String, String> values = new HashMap<>();
    /** Current errors. */
    private final Map<String, Integer> errors = new LinkedHashMap<>();
    /** Published errors. */
    private final MutableLiveData<FieldErrors> fieldErrors = new MutableLiveData<>();

    /** @return errors to render */
    public LiveData<FieldErrors> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Stores a typed value.
     *
     * @param key   the field key
     * @param value the text
     */
    public void setValue(String key, String value) {
        values.put(key, value);
        if (errors.remove(key) != null) {
            fieldErrors.setValue(new FieldErrors(errors, false));
        }
    }

    /**
     * Returns a stored value.
     *
     * @param key the field key
     * @return the text, never null
     */
    public String value(String key) {
        String v = values.get(key);
        return v == null ? "" : v;
    }

    /**
     * Validates one field (on focus loss) and publishes the result.
     *
     * @param key the field key
     */
    public void validateField(String key) {
        ValidationResult result = validator.validate(key, values.get(key));
        if (result.isValid()) {
            errors.remove(key);
        } else {
            errors.put(key, result.getErrorRes());
        }
        fieldErrors.setValue(new FieldErrors(errors, false));
    }

    /**
     * Validates every field and publishes the errors with focus on the first one.
     *
     * @return true when the form is valid
     */
    protected boolean validateAll() {
        errors.clear();
        errors.putAll(validator.validateAll(values));
        fieldErrors.setValue(new FieldErrors(errors, true));
        return errors.isEmpty();
    }

    /**
     * Shows a server-side (repository) error on a field.
     *
     * @param key      the field key
     * @param errorRes the message
     */
    protected void showFieldError(String key, int errorRes) {
        errors.put(key, errorRes);
        fieldErrors.setValue(new FieldErrors(errors, true));
    }
}
