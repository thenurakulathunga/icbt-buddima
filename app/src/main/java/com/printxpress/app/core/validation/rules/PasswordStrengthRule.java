package com.printxpress.app.core.validation.rules;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enforces BR-2: at least 8 characters with an upper-case letter, a lower-case letter, a digit
 * and a symbol. Also exposes the missing criteria and a score for {@code PasswordStrengthMeter}.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class PasswordStrengthRule extends AbstractRule {

    /** Minimum password length. */
    public static final int MIN_LENGTH = 8;

    /** The individual strength criteria. */
    public enum Criterion {
        /** At least {@link #MIN_LENGTH} characters. */
        LENGTH,
        /** Contains an upper-case letter. */
        UPPER,
        /** Contains a lower-case letter. */
        LOWER,
        /** Contains a digit. */
        DIGIT,
        /** Contains a symbol, i.e. anything that is not a letter, digit or space. */
        SYMBOL
    }

    /**
     * Creates the rule.
     *
     * @param errorRes message shown for a weak password
     */
    public PasswordStrengthRule(int errorRes) {
        super(errorRes);
    }

    /**
     * Passes when no criterion is missing.
     *
     * @param value the trimmed input
     * @return true for a strong password
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        return missing(value).isEmpty();
    }

    /**
     * Lists the criteria a password does not meet.
     *
     * @param password the password, may be null
     * @return the missing criteria
     */
    public static Set<Criterion> missing(String password) {
        Set<Criterion> missing = EnumSet.allOf(Criterion.class);
        if (password == null) {
            return missing;
        }
        if (password.length() >= MIN_LENGTH) {
            missing.remove(Criterion.LENGTH);
        }
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                missing.remove(Criterion.UPPER);
            } else if (Character.isLowerCase(c)) {
                missing.remove(Criterion.LOWER);
            } else if (Character.isDigit(c)) {
                missing.remove(Criterion.DIGIT);
            } else if (!Character.isWhitespace(c)) {
                missing.remove(Criterion.SYMBOL);
            }
        }
        return missing;
    }

    /**
     * Scores a password from 0 (empty) to 5 (every criterion met).
     *
     * @param password the password
     * @return the number of criteria met
     */
    public static int score(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        return Criterion.values().length - missing(password).size();
    }
}
