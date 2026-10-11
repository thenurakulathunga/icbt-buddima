package com.printxpress.app.core.validation.rules;

/**
 * Checks email syntax: a local part, one {@code @}, and a domain with a dot and a 2+ letter TLD.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class EmailRule extends RegexRule {

    /** Pragmatic email pattern: no consecutive dots and no leading or trailing dot in either part. */
    private static final String EMAIL =
            "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";

    /**
     * Creates the rule.
     *
     * @param errorRes message shown for an invalid address
     */
    public EmailRule(int errorRes) {
        super(EMAIL, errorRes);
    }
}
