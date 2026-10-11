package com.printxpress.app.core.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.printxpress.app.core.common.Constants;
import com.printxpress.app.ui.splash.SplashActivity;

/**
 * Builds the tap actions of system notifications: they open the app on the related order or
 * offer (FR-4.4, TC-52). The launcher routes through sign-in when needed and passes the extras on.
 *
 * @since 1.0
 */
public final class DeepLinkFactory {

    /** Hidden constructor: utility class. */
    private DeepLinkFactory() {
    }

    /**
     * Intent that opens an order's detail.
     *
     * @param context any context
     * @param orderId the order
     * @return the pending intent
     */
    public static PendingIntent forOrder(Context context, long orderId) {
        Intent intent = launch(context).putExtra(Constants.ARG_ORDER_ID, orderId);
        return PendingIntent.getActivity(context, (int) orderId, intent, flags());
    }

    /**
     * Intent that opens an offer's detail.
     *
     * @param context any context
     * @param promoId the promotion
     * @return the pending intent
     */
    public static PendingIntent forPromo(Context context, long promoId) {
        Intent intent = launch(context).putExtra(Constants.ARG_PROMO_ID, promoId);
        return PendingIntent.getActivity(context, (int) (100000 + promoId), intent, flags());
    }

    /**
     * Base launch intent.
     *
     * @param context any context
     * @return the intent
     */
    private static Intent launch(Context context) {
        return new Intent(context, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /** @return immutable, update-current flags */
    private static int flags() {
        return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
    }
}
