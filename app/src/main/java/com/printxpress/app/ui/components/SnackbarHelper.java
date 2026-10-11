package com.printxpress.app.ui.components;

import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.printxpress.app.R;

/**
 * Themed snackbars for success, error and information (docs/05 §6.2 toast colours).
 *
 * @since 1.0
 */
public final class SnackbarHelper {

    /** Hidden constructor: utility class. */
    private SnackbarHelper() {
    }

    /**
     * Shows a success snackbar.
     *
     * @param anchor     any view in the window
     * @param messageRes the message
     */
    public static void success(View anchor, @StringRes int messageRes) {
        show(anchor, anchor.getContext().getString(messageRes), R.color.px_success_container,
                R.color.px_on_success_container);
    }

    /**
     * Shows an error snackbar.
     *
     * @param anchor     any view in the window
     * @param messageRes the message
     */
    public static void error(View anchor, @StringRes int messageRes) {
        show(anchor, anchor.getContext().getString(messageRes), R.color.px_error_container,
                R.color.px_on_error_container);
    }

    /**
     * Shows an informational snackbar.
     *
     * @param anchor     any view in the window
     * @param messageRes the message
     */
    public static void info(View anchor, @StringRes int messageRes) {
        info(anchor, anchor.getContext().getString(messageRes));
    }

    /**
     * Shows an informational snackbar with prepared text.
     *
     * @param anchor  any view in the window
     * @param message the message
     */
    public static void info(View anchor, CharSequence message) {
        show(anchor, message, R.color.px_primary_container, R.color.px_on_primary_container);
    }

    /**
     * Builds and shows a snackbar.
     *
     * @param anchor     any view in the window
     * @param message    the message
     * @param background container colour
     * @param foreground text colour
     */
    private static void show(View anchor, CharSequence message, @ColorRes int background, @ColorRes int foreground) {
        Snackbar bar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        bar.setBackgroundTint(ContextCompat.getColor(anchor.getContext(), background));
        bar.setTextColor(ContextCompat.getColor(anchor.getContext(), foreground));
        bar.setActionTextColor(ContextCompat.getColor(anchor.getContext(), foreground));
        bar.show();
    }
}
