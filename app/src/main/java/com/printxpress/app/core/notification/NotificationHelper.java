package com.printxpress.app.core.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.printxpress.app.R;

/**
 * Posts system notifications for order progress and promotions (FR-4.1–4.3).
 * <p>
 * Pattern role: Singleton. It checks the Android 13+ runtime permission before posting and never
 * crashes when it is denied; the in-app notification centre still records every event (TC-55).
 * </p>
 *
 * @since 1.0
 */
public final class NotificationHelper {

    /** The lazily created shared instance. */
    private static volatile NotificationHelper instance;

    /** Application context. */
    private final Context context;

    /**
     * Creates the helper.
     *
     * @param context any context; the application context is retained
     */
    private NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Returns the shared instance.
     *
     * @param context any context
     * @return the singleton
     */
    public static NotificationHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationHelper.class) {
                if (instance == null) {
                    instance = new NotificationHelper(context);
                }
            }
        }
        return instance;
    }

    /** @return true when the app may post notifications */
    public boolean canPost() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * Posts an order notification that opens the order.
     *
     * @param orderId the order
     * @param title   the title
     * @param message the message
     */
    public void showOrder(long orderId, String title, String message) {
        post((int) orderId, NotificationChannels.ORDERS, title, message, DeepLinkFactory.forOrder(context, orderId),
                R.drawable.ic_package);
    }

    /**
     * Posts a promotion notification that opens the offer.
     *
     * @param promoId the promotion
     * @param title   the title
     * @param message the message
     */
    public void showPromo(long promoId, String title, String message) {
        post((int) (100000 + promoId), NotificationChannels.PROMOTIONS, title, message,
                DeepLinkFactory.forPromo(context, promoId), R.drawable.ic_promo);
    }

    /**
     * Builds and posts a notification when allowed.
     *
     * @param id      the notification id
     * @param channel the channel
     * @param title   the title
     * @param message the message
     * @param tap     the tap action
     * @param icon    the small icon
     */
    private void post(int id, String channel, String title, String message, PendingIntent tap, int icon) {
        if (!canPost()) {
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(icon)
                .setColor(ContextCompat.getColor(context, R.color.px_launcher_background))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(tap)
                .setAutoCancel(true)
                .setPriority(NotificationChannels.ORDERS.equals(channel)
                        ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT);
        try {
            NotificationManagerCompat.from(context).notify(id, builder.build());
        } catch (SecurityException e) {
            return;
        }
    }
}
