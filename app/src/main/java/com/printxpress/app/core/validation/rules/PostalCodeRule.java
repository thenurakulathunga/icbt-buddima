package com.printxpress.app.core.validation.rules;

import com.printxpress.app.core.common.Constants;

/**
 * Checks a five-digit Sri Lankan postal code, e.g. 10300.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class PostalCodeRule extends RegexRule {

    /**
     * Creates the rule.
     *
     * @param errorRes message shown for an invalid code
     */
    public PostalCodeRule(int errorRes) {
        super(Constants.REGEX_POSTAL_CODE, errorRes);
    }
}
