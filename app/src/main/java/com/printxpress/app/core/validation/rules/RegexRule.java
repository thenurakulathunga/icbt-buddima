package com.printxpress.app.core.validation.rules;

import java.util.regex.Pattern;

/**
 * Checks the whole value against a regular expression.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule} and parent of the format rules.</p>
 *
 * @since 1.0
 */
public class RegexRule extends AbstractRule {

    /** Compiled pattern. */
    private final Pattern pattern;

    /**
     * Creates the rule.
     *
     * @param regex    the regular expression the full value must match
     * @param errorRes message shown on mismatch
     */
    public RegexRule(String regex, int errorRes) {
        super(errorRes);
        this.pattern = Pattern.compile(regex);
    }

    /**
     * Matches the value.
     *
     * @param value the trimmed input
     * @return true when the whole value matches
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        return pattern.matcher(value).matches();
    }
}
