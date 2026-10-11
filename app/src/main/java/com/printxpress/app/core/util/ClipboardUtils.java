package com.printxpress.app.core.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Copies text such as promo codes to the clipboard (TC-56).
 *
 * @since 1.0
 */
public final class ClipboardUtils {

    /** Hidden constructor: utility class. */
    private ClipboardUtils() {
    }

    /**
     * Copies text.
     *
     * @param context the context
     * @param label   a user-visible label for the clip
     * @param text    the text
     * @return true when copied
     */
    public static boolean copy(Context context, String label, String text) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return false;
        }
        manager.setPrimaryClip(ClipData.newPlainText(label, text));
        return true;
    }
}
