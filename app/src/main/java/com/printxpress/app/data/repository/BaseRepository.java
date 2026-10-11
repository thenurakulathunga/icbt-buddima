package com.printxpress.app.data.repository;

import android.database.SQLException;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.BuildConfig;
import com.printxpress.app.R;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;

import java.util.concurrent.Callable;

/**
 * Shared plumbing for repositories: runs blocking work on the disk executor and publishes a
 * {@link Resource} stream (loading, then success or error).
 * <p>
 * Pattern role: Template Method for "run a task and wrap the result". Errors are logged without
 * personal data and only in debug builds (docs/02 §7.9).
 * </p>
 *
 * @since 1.0
 */
public abstract class BaseRepository {

    /** Log tag. */
    private static final String TAG = "PxRepository";

    /** Executors for background and main-thread work. */
    protected final AppExecutors executors;

    /**
     * Creates the repository.
     *
     * @param executors the executors
     */
    protected BaseRepository(AppExecutors executors) {
        this.executors = executors;
    }

    /**
     * Runs a task in the background and exposes its outcome.
     *
     * @param task the blocking work
     * @param <T>  the result type
     * @return a stream that starts in LOADING and ends in SUCCESS or ERROR
     */
    protected <T> LiveData<Resource<T>> execute(Callable<T> task) {
        MutableLiveData<Resource<T>> live = new MutableLiveData<>(Resource.loading());
        executors.diskIO().execute(() -> live.postValue(run(task)));
        return live;
    }

    /**
     * Runs a task on the current thread and wraps the outcome; used by {@link #execute(Callable)}
     * and by workers that are already on a background thread.
     *
     * @param task the blocking work
     * @param <T>  the result type
     * @return the wrapped outcome
     */
    protected <T> Resource<T> run(Callable<T> task) {
        try {
            return Resource.success(task.call());
        } catch (RepositoryException e) {
            return Resource.error(e.getMessageRes());
        } catch (SQLException e) {
            logError("Database error", e);
            return Resource.error(R.string.err_generic);
        } catch (Exception e) {
            logError("Unexpected error", e);
            return Resource.error(R.string.err_generic);
        }
    }

    /**
     * Throws a rule failure when a condition does not hold.
     *
     * @param condition  the rule
     * @param messageRes the message when it fails
     * @throws RepositoryException when the condition is false
     */
    protected static void require(boolean condition, int messageRes) {
        if (!condition) {
            throw new RepositoryException(messageRes);
        }
    }

    /**
     * Logs an error in debug builds only, with the exception class but no user data.
     *
     * @param message a fixed description
     * @param e       the exception
     */
    private static void logError(String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message + ": " + e.getClass().getSimpleName(), e);
        }
    }
}
