package com.printxpress.app.core.validation.rules;

import com.printxpress.app.core.common.Constants;

import java.util.regex.Pattern;

/**
 * Validates Sri Lankan mobile numbers in local (07XXXXXXXX) or international (+947XXXXXXXX) form.
 * Spaces and hyphens are ignored, so {@code 077 123 4567} is accepted.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class SriLankanPhoneRule extends AbstractRule {

    /** Compiled mobile pattern. */
    private static final Pattern MOBILE = Pattern.compile(Constants.REGEX_SL_MOBILE);

    /**
     * Creates the rule.
     *
     * @param errorRes message shown for an invalid number
     */
    public SriLankanPhoneRule(int errorRes) {
        super(errorRes);
    }

    /**
     * Matches the number with separators removed.
     *
     * @param value the trimmed input
     * @return true for a valid mobile number
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        return MOBILE.matcher(strip(value)).matches();
    }

    /**
     * Converts a valid number to the canonical local form {@code 07XXXXXXXX}, so the same phone
     * typed two ways is stored once (BR-1 uniqueness).
     *
     * @param phone the raw number, may be null
     * @return the canonical number, or the stripped input when it is not a valid mobile
     */
    public static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        String stripped = strip(phone.trim());
        if (!MOBILE.matcher(stripped).matches()) {
            return stripped;
        }
        return stripped.startsWith("+94") ? "0" + stripped.substring(3) : stripped;
    }

    /**
     * Removes spaces and hyphens.
     *
     * @param value the input
     * @return the input without separators
     */
    private static String strip(String value) {
        return value.replace(" ", "").replace("-", "");
    }
}
