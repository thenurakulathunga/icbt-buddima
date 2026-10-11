package com.printxpress.app.core.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base for every screen ViewModel: shared loading flag, one-shot messages, and safe
 * subscription to repository streams.
 * <p>
 * Pattern role: Template Method + Observer. {@link #subscribe(LiveData, Consumer)} observes a
 * repository result until it finishes, keeps the loading flag in sync, and detaches everything
 * in {@link #onCleared()} so no observer leaks after the screen is gone.
 * </p>
 *
 * @since 1.0
 */
public abstract class BaseViewModel extends ViewModel {

    /** Whether a blocking operation is running. */
    protected final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    /** One-shot message resource ids for snackbars. */
    protected final MutableLiveData<Event<Integer>> message = new MutableLiveData<>();
    /** Active subscriptions to remove on clear. */
    private final List<Subscription<?>> subscriptions = new ArrayList<>();

    /** For subclasses only. */
    protected BaseViewModel() {
        super();
    }

    /** @return whether a blocking operation is running */
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    /** @return one-shot messages to show */
    public LiveData<Event<Integer>> getMessage() {
        return message;
    }

    /**
     * Sets the loading flag.
     *
     * @param value true while working
     */
    protected void setLoading(boolean value) {
        loading.setValue(value);
    }

    /**
     * Posts a one-shot message.
     *
     * @param messageRes the string resource id
     */
    protected void postMessage(int messageRes) {
        message.setValue(new Event<>(messageRes));
    }

    /**
     * Posts an error message.
     *
     * @param messageRes the string resource id
     */
    protected void postError(int messageRes) {
        postMessage(messageRes);
    }

    /**
     * Observes a repository stream until it leaves the loading state.
     *
     * @param source      the repository stream
     * @param handler     receives every emission, including loading
     * @param showLoading whether to drive the shared loading flag
     * @param <T>         the payload type
     */
    protected <T> void subscribe(LiveData<Resource<T>> source, Consumer<Resource<T>> handler, boolean showLoading) {
        Subscription<T> subscription = new Subscription<>(source);
        subscription.observer = resource -> {
            if (showLoading) {
                loading.setValue(resource.isLoading());
            }
            handler.accept(resource);
            if (!resource.isLoading()) {
                subscription.dispose();
                subscriptions.remove(subscription);
            }
        };
        subscriptions.add(subscription);
        source.observeForever(subscription.observer);
    }

    /**
     * Observes a repository stream without touching the shared loading flag.
     *
     * @param source  the repository stream
     * @param handler receives every emission
     * @param <T>     the payload type
     */
    protected <T> void subscribe(LiveData<Resource<T>> source, Consumer<Resource<T>> handler) {
        subscribe(source, handler, false);
    }

    /**
     * Pipes a repository stream into a screen's state LiveData.
     *
     * @param source the repository stream
     * @param target the screen state
     * @param <T>    the payload type
     */
    protected <T> void load(LiveData<Resource<T>> source, MutableLiveData<Resource<T>> target) {
        subscribe(source, target::setValue);
    }

    /** Detaches every remaining subscription. */
    @Override
    protected void onCleared() {
        for (Subscription<?> subscription : new ArrayList<>(subscriptions)) {
            subscription.dispose();
        }
        subscriptions.clear();
    }

    /**
     * Pairs a source with its observer so it can be removed later.
     *
     * @param <T> the payload type
     */
    private static final class Subscription<T> {

        /** The observed stream. */
        private final LiveData<Resource<T>> source;
        /** The attached observer. */
        private Observer<Resource<T>> observer;

        /**
         * Creates a subscription.
         *
         * @param source the observed stream
         */
        Subscription(LiveData<Resource<T>> source) {
            this.source = source;
        }

        /** Removes the observer from the source. */
        void dispose() {
            if (observer != null) {
                source.removeObserver(observer);
            }
        }
    }
}
