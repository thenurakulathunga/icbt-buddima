package com.printxpress.app.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.core.common.Constants;

/**
 * Background job that plays the role of the print shop (docs/01 assumption 1): it advances one
 * order by one status through {@code OrderState}. Notifications and the next step are handled by
 * {@link OrderEventsHandler}, which observes the repository.
 *
 * @since 1.0
 */
public class OrderProgressWorker extends Worker {

    /**
     * Creates the worker.
     *
     * @param context the context
     * @param params  the work parameters
     */
    public OrderProgressWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Advances the order.
     *
     * @return success, or failure when no order id was given
     */
    @NonNull
    @Override
    public Result doWork() {
        long orderId = getInputData().getLong(Constants.ARG_ORDER_ID, -1);
        if (orderId <= 0) {
            return Result.failure();
        }
        PrintXpressApp app = (PrintXpressApp) getApplicationContext();
        app.getContainer().getOrderRepository().advanceBlocking(orderId);
        return Result.success();
    }
}
