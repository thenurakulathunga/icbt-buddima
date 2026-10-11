package com.printxpress.app.core.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Builds and starts implicit intents for the dialer, email, WhatsApp, the browser and sharing.
 * <p>
 * Each method returns false when no app can handle the intent, so the caller can show a message.
 * </p>
 *
 * @since 1.0
 */
public final class IntentUtils {

    /** Hidden constructor: utility class. */
    private IntentUtils() {
    }

    /**
     * Opens the dialer with a number filled in (no CALL_PHONE permission needed).
     *
     * @param context the starting context
     * @param phone   the phone number
     * @return true when an app handled the intent
     */
    public static boolean dial(Context context, String phone) {
        return start(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
    }

    /**
     * Opens an email composer.
     *
     * @param context the starting context
     * @param to      the recipient
     * @param subject the subject line
     * @return true when an app handled the intent
     */
    public static boolean email(Context context, String to, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + to));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return start(context, intent);
    }

    /**
     * Opens a WhatsApp chat through the public wa.me link.
     *
     * @param context the starting context
     * @param number  the number in international form without the plus sign
     * @param message the pre-filled message
     * @return true when an app handled the intent
     */
    public static boolean whatsApp(Context context, String number, String message) {
        Uri uri = Uri.parse("https://wa.me/" + number).buildUpon().appendQueryParameter("text", message).build();
        return start(context, new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * Opens a web page.
     *
     * @param context the starting context
     * @param url     the address
     * @return true when an app handled the intent
     */
    public static boolean openUrl(Context context, String url) {
        return start(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * Opens the system notification settings of this app (app details on Android 7).
     *
     * @param context a context
     * @return true when the settings screen opened
     */
    public static boolean openNotificationSettings(Context context) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.getPackageName(), null));
        }
        return start(context, intent);
    }

    /**
     * Shares plain text through the system share sheet.
     *
     * @param context the starting context
     * @param chooserTitle the share sheet title
     * @param text    the text to share
     * @return true when an app handled the intent
     */
    public static boolean shareText(Context context, String chooserTitle, String text) {
        Intent send = new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text);
        return start(context, Intent.createChooser(send, chooserTitle));
    }

    /**
     * Shares a file exposed through a FileProvider URI.
     *
     * @param context      the starting context
     * @param chooserTitle the share sheet title
     * @param uri          the content URI
     * @param mimeType     the file type
     * @return true when an app handled the intent
     */
    public static boolean shareFile(Context context, String chooserTitle, Uri uri, String mimeType) {
        Intent send = new Intent(Intent.ACTION_SEND).setType(mimeType).putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return start(context, Intent.createChooser(send, chooserTitle));
    }

    /**
     * Starts an intent, catching the case where no app can handle it.
     *
     * @param context the starting context
     * @param intent  the intent
     * @return true when started
     */
    private static boolean start(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
