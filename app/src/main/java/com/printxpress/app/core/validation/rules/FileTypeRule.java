package com.printxpress.app.core.validation.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Checks an upload's MIME type against an allow-list (BR-10: PDF, PNG, JPEG).
 * The validated value is the MIME type string.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class FileTypeRule extends AbstractRule {

    /** Accepted MIME types in lower case. */
    private final Set<String> accepted = new HashSet<>();

    /**
     * Creates the rule.
     *
     * @param accepted the accepted MIME types
     * @param errorRes message shown for any other type
     */
    public FileTypeRule(Collection<String> accepted, int errorRes) {
        super(errorRes);
        for (String type : accepted) {
            this.accepted.add(type.toLowerCase(Locale.US));
        }
    }

    /**
     * Looks up the type.
     *
     * @param value the MIME type
     * @return true when accepted
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        return accepted.contains(value.toLowerCase(Locale.US));
    }
}
