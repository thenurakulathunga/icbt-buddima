package com.printxpress.app.core.common;

/**
 * Wrapper for LiveData values that must be consumed only once, such as navigation commands or
 * snackbar messages, so they are not replayed after a configuration change.
 *
 * @param <T> the content type
 * @since 1.0
 */
public class Event<T> {

    /** The wrapped content. */
    private final T content;
    /** Whether the content has already been handled. */
    private boolean handled;

    /**
     * Creates an event.
     *
     * @param content the content to deliver once
     */
    public Event(T content) {
        this.content = content;
    }

    /**
     * Returns the content the first time it is requested and null afterwards.
     *
     * @return the content, or null when already handled
     */
    public T getContentIfNotHandled() {
        if (handled) {
            return null;
        }
        handled = true;
        return content;
    }

    /**
     * Returns the content even if it has already been handled.
     *
     * @return the content
     */
    public T peekContent() {
        return content;
    }

    /** @return true when the content has been consumed */
    public boolean isHandled() {
        return handled;
    }
}
