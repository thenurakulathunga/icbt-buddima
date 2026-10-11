package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.StringRes;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.printxpress.app.R;

/**
 * Button with a built-in spinner for every submit action (docs/06 B).
 * <p>
 * While loading, the label is hidden, a spinner shows in its place and the button is disabled,
 * which prevents double submission. {@code px_style} picks filled, tonal, outlined or text.
 * </p>
 *
 * @since 1.0
 */
public class LoadingButton extends FrameLayout {

    /** The inner button. */
    private final MaterialButton button;
    /** The spinner. */
    private final CircularProgressIndicator progress;
    /** The label kept while loading. */
    private CharSequence label;
    /** Whether the caller enabled the button. */
    private boolean requestedEnabled = true;
    /** Whether the spinner is showing. */
    private boolean loading;

    /**
     * Creates the button.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);
        int style;
        int icon;
        try {
            label = a.getString(R.styleable.LoadingButton_px_text);
            style = a.getInt(R.styleable.LoadingButton_px_style, 0);
            icon = a.getResourceId(R.styleable.LoadingButton_px_icon, 0);
        } finally {
            a.recycle();
        }
        button = (MaterialButton) LayoutInflater.from(context).inflate(layoutFor(style), this, false);
        button.setText(label);
        if (icon != 0) {
            button.setIconResource(icon);
        }
        addView(button);
        progress = new CircularProgressIndicator(context);
        progress.setIndeterminate(true);
        progress.setIndicatorSize(getResources().getDimensionPixelSize(R.dimen.icon_md));
        progress.setVisibility(GONE);
        ColorStateList tint = style == 0 ? ColorStateList.valueOf(resolveOnPrimary()) : button.getTextColors();
        progress.setIndicatorColor(tint.getDefaultColor());
        addView(progress, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    }

    /**
     * Maps px_style to the inner button layout.
     *
     * @param style the enum value
     * @return the layout id
     */
    private static int layoutFor(int style) {
        switch (style) {
            case 1:
                return R.layout.view_button_tonal;
            case 2:
                return R.layout.view_button_outlined;
            case 3:
                return R.layout.view_button_text;
            default:
                return R.layout.view_button_filled;
        }
    }

    /** @return the on-primary colour from the theme */
    private int resolveOnPrimary() {
        android.util.TypedValue value = new android.util.TypedValue();
        getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, value, true);
        return value.data;
    }

    /**
     * Shows or hides the spinner.
     *
     * @param value true while working
     */
    public void setLoading(boolean value) {
        loading = value;
        button.setText(value ? "" : label);
        button.setEnabled(!value && requestedEnabled);
        progress.setVisibility(value ? VISIBLE : GONE);
        button.setContentDescription(value ? getContext().getString(R.string.state_loading) : label);
    }

    /** @return true while the spinner shows */
    public boolean isLoading() {
        return loading;
    }

    /**
     * Sets the label.
     *
     * @param textRes the label
     */
    public void setText(@StringRes int textRes) {
        setText(getContext().getString(textRes));
    }

    /**
     * Sets a prepared label.
     *
     * @param text the label
     */
    public void setText(CharSequence text) {
        label = text;
        if (!loading) {
            button.setText(text);
        }
    }

    /**
     * Enables or disables the button.
     *
     * @param enabled true to allow taps
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        requestedEnabled = enabled;
        button.setEnabled(enabled && !loading);
    }

    /**
     * Forwards clicks to the inner button.
     *
     * @param listener the listener
     */
    @Override
    public void setOnClickListener(OnClickListener listener) {
        button.setOnClickListener(listener);
    }

    /** @return the inner button, for tests and accessibility */
    public MaterialButton getButton() {
        return button;
    }
}
