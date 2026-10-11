package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.StringRes;

import com.printxpress.app.R;

/**
 * Error state with a retry button; an {@link EmptyStateView} specialised with the error icon and
 * default texts (inheritance keeps both states visually identical).
 *
 * @since 1.0
 */
public class ErrorStateView extends EmptyStateView {

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes, may be null
     */
    public ErrorStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(getContext().getColor(R.color.px_background));
        bind(R.drawable.ic_error, R.string.state_error_title, R.string.state_error_message, 0, null);
    }

    /**
     * Shows a message and a retry action.
     *
     * @param messageRes the message, or 0 for the default
     * @param onRetry    retry callback, may be null
     */
    public void show(@StringRes int messageRes, Runnable onRetry) {
        bind(R.drawable.ic_error, R.string.state_error_title,
                messageRes == 0 ? R.string.state_error_message : messageRes, R.string.action_retry, onRetry);
    }
}
