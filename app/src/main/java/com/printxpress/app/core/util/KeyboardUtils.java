package com.printxpress.app.core.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Shows and hides the soft keyboard.
 *
 * @since 1.0
 */
public final class KeyboardUtils {

    /** Hidden constructor: utility class. */
    private KeyboardUtils() {
    }

    /**
     * Hides the keyboard attached to a view's window.
     *
     * @param view any view in the window
     */
    public static void hide(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Focuses a view and shows the keyboard for it.
     *
     * @param view the view to edit
     */
    public static void show(View view) {
        if (view == null) {
            return;
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
