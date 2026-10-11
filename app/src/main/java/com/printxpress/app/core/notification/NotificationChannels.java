package com.printxpress.app.core.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.printxpress.app.R;

/**
 * Creates the notification channels: orders (high importance) and promotions (default).
 * Safe to call on every start; the system ignores existing channels.
 *
 * @since 1.0
 */
public final class NotificationChannels {

    /** Channel id for order updates. */
    public static final String ORDERS = "orders";
    /** Channel id for promotions. */
    public static final String PROMOTIONS = "promotions";

    /** Hidden constructor: utility class. */
    private NotificationChannels() {
    }

    /**
     * Registers the channels on API 26+.
     *
     * @param context any context
     */
    public static void create(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }
        NotificationChannel orders = new NotificationChannel(ORDERS, context.getString(R.string.channel_orders),
                NotificationManager.IMPORTANCE_HIGH);
        orders.setDescription(context.getString(R.string.channel_orders_desc));
        NotificationChannel promos = new NotificationChannel(PROMOTIONS, context.getString(R.string.channel_promos),
                NotificationManager.IMPORTANCE_DEFAULT);
        promos.setDescription(context.getString(R.string.channel_promos_desc));
        manager.createNotificationChannel(orders);
        manager.createNotificationChannel(promos);
    }
}
