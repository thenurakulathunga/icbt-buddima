package com.printxpress.app.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.R;
import com.printxpress.app.core.notification.NotificationHelper;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.model.enums.NotificationType;
import com.printxpress.app.di.AppContainer;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Announces seasonal promotions once each (FR-4.3): runs daily and at start-up, records an
 * in-app notification and posts a system notification when the customer allows promo alerts
 * (FR-4.6, TC-54).
 *
 * @since 1.0
 */
public class PromotionWorker extends Worker {

    /** Unique name of the daily job. */
    private static final String DAILY = "promotions_daily";
    /** Unique name of the start-up job. */
    private static final String NOW = "promotions_now";

    /**
     * Creates the worker.
     *
     * @param context the context
     * @param params  the work parameters
     */
    public PromotionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Schedules the daily job and runs one check now.
     *
     * @param context any context
     */
    public static void schedule(Context context) {
        WorkManager wm = WorkManager.getInstance(context);
        wm.enqueueUniquePeriodicWork(DAILY, ExistingPeriodicWorkPolicy.KEEP,
                new PeriodicWorkRequest.Builder(PromotionWorker.class, 1, TimeUnit.DAYS).build());
        wm.enqueueUniqueWork(NOW, ExistingWorkPolicy.REPLACE, new OneTimeWorkRequest.Builder(PromotionWorker.class).build());
    }

    /**
     * Announces promotions that are running today and were not announced yet.
     *
     * @return success
     */
    @NonNull
    @Override
    public Result doWork() {
        AppContainer c = ((PrintXpressApp) getApplicationContext()).getContainer();
        SessionManager session = c.getSession();
        if (!session.isLoggedIn()) {
            return Result.success();
        }
        User user = c.getUserRepository().findUserBlocking(session.getUserId());
        if (user == null) {
            return Result.success();
        }
        Context ctx = getApplicationContext();
        for (Promotion promo : c.getPromotionRepository().findActiveBlocking(LocalDate.now())) {
            if (!session.claimPromoAnnouncement(user.getUserId(), promo.getPromoId())) {
                continue;
            }
            String title = ctx.getString(R.string.notif_promo_title, promo.getTitle());
            String message = ctx.getString(R.string.notif_promo_msg, promo.getPromoCode(), promo.getDescription());
            AppNotification n = new AppNotification();
            n.setUserId(user.getUserId());
            n.setPromoId(promo.getPromoId());
            n.setType(NotificationType.PROMO);
            n.setTitle(title);
            n.setMessage(message);
            c.getNotificationRepository().addBlocking(n);
            if (user.isNotifyPromos()) {
                NotificationHelper.getInstance(ctx).showPromo(promo.getPromoId(), title, message);
            }
        }
        return Result.success();
    }
}
