package com.printxpress.app.core.validation.rules;

/**
 * Checks an upload's size in bytes: more than zero and at most the limit (BR-10: 25 MB).
 * The validated value is the size as decimal text.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class FileSizeRule extends AbstractRule {

    /** Largest accepted size in bytes. */
    private final long maxBytes;

    /**
     * Creates the rule.
     *
     * @param maxBytes the largest accepted size
     * @param errorRes message shown for empty or oversized files
     */
    public FileSizeRule(long maxBytes, int errorRes) {
        super(errorRes);
        this.maxBytes = maxBytes;
    }

    /**
     * Parses and compares the size.
     *
     * @param value the size in bytes
     * @return true when {@code 0 < size <= maxBytes}
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        try {
            long size = Long.parseLong(value);
            return size > 0 && size <= maxBytes;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
