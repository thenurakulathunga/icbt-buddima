package com.printxpress.app.core.common;

/**
 * Lifecycle state of an asynchronous operation wrapped in a {@link Resource}.
 *
 * @since 1.0
 */
public enum Status {
    /** The operation is still running. */
    LOADING,
    /** The operation finished and produced data. */
    SUCCESS,
    /** The operation failed; the resource carries a message resource id. */
    ERROR
}
