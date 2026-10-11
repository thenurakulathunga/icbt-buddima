package com.printxpress.app.core.common;

/**
 * Immutable wrapper that pairs data with the status of the operation that produced it.
 * <p>
 * Repositories expose {@code LiveData<Resource<T>>} so screens can render loading, content and
 * error states from a single stream. Errors carry an Android string resource id rather than raw
 * text, which keeps messages localisable and free of personal data.
 * </p>
 *
 * @param <T> the payload type
 * @since 1.0
 */
public final class Resource<T> {

    /** The state of the operation. */
    private final Status status;
    /** The payload; present on success and optionally on error. */
    private final T data;
    /** String resource id describing the error, or 0 when there is none. */
    private final int messageRes;

    /**
     * Creates a resource; use the static factories instead.
     *
     * @param status     the operation state
     * @param data       the payload, may be null
     * @param messageRes the error message resource id, or 0
     */
    private Resource(Status status, T data, int messageRes) {
        this.status = status;
        this.data = data;
        this.messageRes = messageRes;
    }

    /**
     * Creates a loading resource.
     *
     * @param <T> the payload type
     * @return a resource in the {@link Status#LOADING} state
     */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, 0);
    }

    /**
     * Creates a successful resource.
     *
     * @param data the payload
     * @param <T>  the payload type
     * @return a resource in the {@link Status#SUCCESS} state
     */
    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, 0);
    }

    /**
     * Creates a failed resource.
     *
     * @param messageRes string resource id explaining the failure
     * @param <T>        the payload type
     * @return a resource in the {@link Status#ERROR} state
     */
    public static <T> Resource<T> error(int messageRes) {
        return new Resource<>(Status.ERROR, null, messageRes);
    }

    /**
     * Creates a failed resource that still carries data (for example stale content).
     *
     * @param messageRes string resource id explaining the failure
     * @param data       the payload to keep showing
     * @param <T>        the payload type
     * @return a resource in the {@link Status#ERROR} state
     */
    public static <T> Resource<T> error(int messageRes, T data) {
        return new Resource<>(Status.ERROR, data, messageRes);
    }

    /** @return the operation state */
    public Status getStatus() {
        return status;
    }

    /** @return the payload, may be null */
    public T getData() {
        return data;
    }

    /** @return the error message resource id, or 0 */
    public int getMessageRes() {
        return messageRes;
    }

    /** @return true when the state is {@link Status#LOADING} */
    public boolean isLoading() {
        return status == Status.LOADING;
    }

    /** @return true when the state is {@link Status#SUCCESS} */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /** @return true when the state is {@link Status#ERROR} */
    public boolean isError() {
        return status == Status.ERROR;
    }
}
