package com.printxpress.app.ui.components;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Connects a ViewModel's field keys to {@link PxTextInput} components.
 * <p>
 * The ViewModel owns validation ({@code FormValidator}) and publishes a map of field key to error
 * message; this binder renders the map, focuses the first invalid field (docs/05 §9 rule 2) and
 * forwards text changes and focus loss back to the ViewModel. It keeps views out of ViewModels.
 * </p>
 *
 * @since 1.0
 */
public final class FormBinder {

    /** Field key to input, in form order. */
    private final Map<String, PxTextInput> inputs = new LinkedHashMap<>();

    /** Creates a binder with no fields; add them with {@link #bind}. */
    public FormBinder() {
        super();
    }

    /**
     * Registers a field.
     *
     * @param key         the ViewModel field key
     * @param input       the input component
     * @param onChanged   receives (key, text) on every change, may be null
     * @param onFocusLost receives the key when the field loses focus, may be null
     * @return this binder
     */
    public FormBinder bind(String key, PxTextInput input, BiConsumer<String, String> onChanged,
                           java.util.function.Consumer<String> onFocusLost) {
        inputs.put(key, input);
        if (onChanged != null) {
            input.setOnTextChanged(text -> onChanged.accept(key, text));
        }
        if (onFocusLost != null) {
            input.setOnFocusLost(() -> onFocusLost.accept(key));
        }
        return this;
    }

    /**
     * Shows errors on their fields and clears the rest.
     *
     * @param errors field key to error message resource id
     * @param focus  whether to focus the first invalid field (on submit)
     */
    public void showErrors(Map<String, Integer> errors, boolean focus) {
        PxTextInput first = null;
        for (Map.Entry<String, PxTextInput> entry : inputs.entrySet()) {
            Integer error = errors == null ? null : errors.get(entry.getKey());
            entry.getValue().setError(error == null ? 0 : error);
            if (error != null && first == null) {
                first = entry.getValue();
            }
        }
        if (focus && first != null) {
            first.focus();
            first.getParent().requestChildFocus(first, first);
        }
    }

    /**
     * Shows or clears the error of one field (focus-lost validation).
     *
     * @param key      the field key
     * @param errorRes the message, or 0 to clear
     */
    public void showError(String key, int errorRes) {
        PxTextInput input = inputs.get(key);
        if (input != null) {
            input.setError(errorRes);
        }
    }

    /**
     * Collects the current text of every field.
     *
     * @return field key to text
     */
    public Map<String, String> values() {
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, PxTextInput> entry : inputs.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getText());
        }
        return values;
    }
}
