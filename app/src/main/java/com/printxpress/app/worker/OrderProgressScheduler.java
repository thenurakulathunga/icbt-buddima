package com.printxpress.app.worker;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.printxpress.app.core.common.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Schedules the simulated back-office steps of an order (docs/01 assumption 1): one unique
 * WorkManager job per order that advances it by one status after a delay.
 *
 * @since 1.0
 */
public class OrderProgressScheduler {

    /** Delay between steps in demo mode, in seconds. */
    static final long DEMO_DELAY_SECONDS = 20;
    /** Delay between steps in normal mode, in minutes. */
    static final long NORMAL_DELAY_MINUTES = 10;
    /** Unique work name prefix. */
    private static final String WORK_PREFIX = "order_progress_";

    /** Application context. */
    private final Context context;

    /**
     * Creates the scheduler.
     *
     * @param context any context
     */
    public OrderProgressScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Schedules the next step of an order, replacing any pending one.
     *
     * @param orderId the order
     * @param demo    true for fast demo steps
     */
    public void scheduleNext(long orderId, boolean demo) {
        long delay = demo ? DEMO_DELAY_SECONDS : TimeUnit.MINUTES.toSeconds(NORMAL_DELAY_MINUTES);
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(OrderProgressWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setInputData(new Data.Builder().putLong(Constants.ARG_ORDER_ID, orderId).build())
                .addTag(WORK_PREFIX)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(WORK_PREFIX + orderId, ExistingWorkPolicy.REPLACE, request);
    }

    /**
     * Stops the simulation of an order (cancelled or completed).
     *
     * @param orderId the order
     */
    public void cancel(long orderId) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_PREFIX + orderId);
    }

    /** Stops every order simulation, e.g. when the account is deleted. */
    public void cancelAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_PREFIX);
    }
}
