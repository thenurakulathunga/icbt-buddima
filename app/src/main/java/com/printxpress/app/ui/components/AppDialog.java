package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.printxpress.app.R;

/**
 * Consistent confirmation and information dialogs (docs/05 §9 rule 4: destructive actions always
 * confirm).
 * <p>Pattern role: Builder over {@link MaterialAlertDialogBuilder}.</p>
 *
 * @since 1.0
 */
public final class AppDialog {

    /** Hidden constructor: use {@link Builder}. */
    private AppDialog() {
    }

    /**
     * Fluent builder for dialogs.
     */
    public static final class Builder {

        /** The context. */
        private final Context context;
        /** Icon, 0 for none. */
        private int icon;
        /** Title text. */
        private CharSequence title;
        /** Message text. */
        private CharSequence message;
        /** Positive button text. */
        private CharSequence positiveText;
        /** Positive action. */
        private Runnable positiveAction;
        /** Negative button text. */
        private CharSequence negativeText;
        /** Whether the positive action is destructive (red). */
        private boolean destructive;
        /** Whether back and outside taps dismiss the dialog. */
        private boolean cancelable = true;

        /**
         * Starts a builder.
         *
         * @param context the context
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the icon.
         *
         * @param iconRes the icon drawable
         * @return this builder
         */
        public Builder icon(@DrawableRes int iconRes) {
            this.icon = iconRes;
            return this;
        }

        /**
         * Sets the title.
         *
         * @param titleRes the title
         * @return this builder
         */
        public Builder title(@StringRes int titleRes) {
            this.title = context.getString(titleRes);
            return this;
        }

        /**
         * Sets a prepared title.
         *
         * @param text the title
         * @return this builder
         */
        public Builder title(CharSequence text) {
            this.title = text;
            return this;
        }

        /**
         * Sets the message.
         *
         * @param messageRes the message
         * @return this builder
         */
        public Builder message(@StringRes int messageRes) {
            this.message = context.getString(messageRes);
            return this;
        }

        /**
         * Sets a prepared message.
         *
         * @param text the message
         * @return this builder
         */
        public Builder message(CharSequence text) {
            this.message = text;
            return this;
        }

        /**
         * Sets the positive button.
         *
         * @param textRes the label
         * @param action  run when tapped, may be null
         * @return this builder
         */
        public Builder positive(@StringRes int textRes, Runnable action) {
            this.positiveText = context.getString(textRes);
            this.positiveAction = action;
            return this;
        }

        /**
         * Sets the negative button that just closes the dialog.
         *
         * @param textRes the label
         * @return this builder
         */
        public Builder negative(@StringRes int textRes) {
            this.negativeText = context.getString(textRes);
            return this;
        }

        /**
         * Marks the positive action as destructive.
         *
         * @param value true for a red positive button
         * @return this builder
         */
        public Builder destructive(boolean value) {
            this.destructive = value;
            return this;
        }

        /**
         * Sets whether the dialog can be dismissed without a choice.
         *
         * @param value true to allow dismissal
         * @return this builder
         */
        public Builder cancelable(boolean value) {
            this.cancelable = value;
            return this;
        }

        /**
         * Builds and shows the dialog.
         *
         * @return the dialog
         */
        public AlertDialog show() {
            int theme = destructive ? R.style.ThemeOverlay_PrintXpress_Dialog_Destructive
                    : R.style.ThemeOverlay_PrintXpress_Dialog;
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, theme)
                    .setTitle(title).setMessage(message).setCancelable(cancelable);
            if (icon != 0) {
                builder.setIcon(icon);
            }
            if (positiveText != null) {
                builder.setPositiveButton(positiveText, (DialogInterface d, int w) -> {
                    if (positiveAction != null) {
                        positiveAction.run();
                    }
                });
            }
            if (negativeText != null) {
                builder.setNegativeButton(negativeText, (d, w) -> d.dismiss());
            }
            return builder.show();
        }
    }
}
