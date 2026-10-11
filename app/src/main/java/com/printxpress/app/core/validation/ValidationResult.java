package com.printxpress.app.core.validation;

/**
 * Immutable outcome of a validation: valid, or invalid with a string resource id to show.
 *
 * @since 1.0
 */
public final class ValidationResult {

    /** Shared valid result. */
    private static final ValidationResult VALID = new ValidationResult(true, 0);

    /** Whether the value passed. */
    private final boolean valid;
    /** Error message resource id, 0 when valid. */
    private final int errorRes;

    /**
     * Creates a result.
     *
     * @param valid    whether the value passed
     * @param errorRes the message resource id when invalid
     */
    private ValidationResult(boolean valid, int errorRes) {
        this.valid = valid;
        this.errorRes = errorRes;
    }

    /** @return the shared valid result */
    public static ValidationResult valid() {
        return VALID;
    }

    /**
     * Creates an invalid result.
     *
     * @param errorRes the message resource id
     * @return the invalid result
     */
    public static ValidationResult invalid(int errorRes) {
        return new ValidationResult(false, errorRes);
    }

    /** @return true when the value passed */
    public boolean isValid() {
        return valid;
    }

    /** @return the error message resource id, 0 when valid */
    public int getErrorRes() {
        return errorRes;
    }
}
