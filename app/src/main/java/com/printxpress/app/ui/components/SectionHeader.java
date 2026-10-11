package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.button.MaterialButton;
import com.printxpress.app.R;

/**
 * Section title with an optional "See all" action (kit List Title).
 *
 * @since 1.0
 */
public class SectionHeader extends LinearLayout {

    /** The title. */
    private final TextView title;
    /** The action button. */
    private final MaterialButton action;

    /**
     * Creates the header.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public SectionHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        title = new TextView(context);
        title.setTextAppearance(R.style.TextAppearance_PrintXpress_TitleMedium);
        androidx.core.view.ViewCompat.setAccessibilityHeading(title, true);
        addView(title, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        action = (MaterialButton) inflate(context, R.layout.view_button_text, null);
        action.setVisibility(GONE);
        addView(action, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SectionHeader);
        try {
            title.setText(a.getString(R.styleable.SectionHeader_px_title));
            String actionText = a.getString(R.styleable.SectionHeader_px_actionText);
            if (actionText != null) {
                action.setText(actionText);
                action.setVisibility(VISIBLE);
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the title.
     *
     * @param titleRes the title
     */
    public void setTitle(@StringRes int titleRes) {
        title.setText(titleRes);
    }

    /**
     * Registers the action and shows it.
     *
     * @param listener run on tap
     */
    public void setOnActionClick(Runnable listener) {
        action.setVisibility(VISIBLE);
        action.setOnClickListener(v -> listener.run());
    }
}
