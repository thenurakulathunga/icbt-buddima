package com.printxpress.app.data.repository;

/**
 * Signals a business-rule failure inside a repository, carrying the message to show.
 * <p>
 * Thrown on the background thread and converted to {@code Resource.error(messageRes)} by
 * {@link BaseRepository}, so it never reaches the UI as an exception.
 * </p>
 *
 * @since 1.0
 */
public class RepositoryException extends RuntimeException {

    /** String resource id of the user-facing message. */
    private final int messageRes;

    /**
     * Creates the exception.
     *
     * @param messageRes the message resource id
     */
    public RepositoryException(int messageRes) {
        super("Repository rule failed: " + messageRes);
        this.messageRes = messageRes;
    }

    /** @return the message resource id */
    public int getMessageRes() {
        return messageRes;
    }
}
