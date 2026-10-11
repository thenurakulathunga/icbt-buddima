package com.printxpress.app.core.concurrency;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Global executor pools so that database and file work never runs on the main thread.
 * <p>
 * Pattern role: Singleton. A single-threaded disk executor serialises SQLite writes, which avoids
 * lock contention, while {@link #mainThread()} posts results back to the UI thread.
 * </p>
 *
 * @since 1.0
 */
public final class AppExecutors {

    /** The lazily created shared instance. */
    private static volatile AppExecutors instance;

    /** Executor for database and file I/O. */
    private final Executor diskIO;
    /** Executor that runs tasks on the Android main thread. */
    private final Executor mainThread;

    /**
     * Creates executors; tests can inject synchronous executors.
     *
     * @param diskIO     executor for disk work
     * @param mainThread executor for UI work
     */
    public AppExecutors(@NonNull Executor diskIO, @NonNull Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    /**
     * Returns the shared instance, creating it on first use.
     *
     * @return the singleton executors
     */
    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    ExecutorService disk = Executors.newSingleThreadExecutor();
                    instance = new AppExecutors(disk, new MainThreadExecutor());
                }
            }
        }
        return instance;
    }

    /** @return the executor for database and file work */
    public Executor diskIO() {
        return diskIO;
    }

    /** @return the executor that posts to the main thread */
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * Executor that posts runnables to the main looper.
     */
    private static final class MainThreadExecutor implements Executor {

        /** Handler bound to the main looper. */
        private final Handler handler = new Handler(Looper.getMainLooper());

        /**
         * Posts the command to the main thread.
         *
         * @param command the work to run
         */
        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}
