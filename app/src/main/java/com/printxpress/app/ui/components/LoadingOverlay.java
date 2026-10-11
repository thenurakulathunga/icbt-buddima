package com.printxpress.app.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.core.content.ContextCompat;

import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewLoadingOverlayBinding;

/**
 * Full-screen blocking progress used while a transaction commits, e.g. placing an order.
 * Not cancellable, so the user cannot submit twice.
 *
 * @since 1.0
 */
public final class LoadingOverlay {

    /** The backing dialog. */
    private final Dialog dialog;

    /**
     * Creates the overlay.
     *
     * @param context an activity context
     */
    public LoadingOverlay(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(ViewLoadingOverlayBinding.inflate(LayoutInflater.from(context)).getRoot());
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.px_transparent)));
        }
    }

    /** Shows the overlay if hidden. */
    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /** Hides the overlay if shown. */
    public void hide() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Shows or hides the overlay.
     *
     * @param visible true to show
     */
    public void setVisible(boolean visible) {
        if (visible) {
            show();
        } else {
            hide();
        }
    }
}
