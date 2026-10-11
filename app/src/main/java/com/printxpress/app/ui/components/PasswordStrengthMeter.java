package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.printxpress.app.R;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Progress bar and label that show how many BR-2 criteria a password meets and which are missing
 * (Register, Change password; TC-06).
 *
 * @since 1.0
 */
public class PasswordStrengthMeter extends LinearLayout {

    /** The strength bar. */
    private final LinearProgressIndicator bar;
    /** The strength label and missing criteria. */
    private final TextView label;

    /**
     * Creates the meter.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PasswordStrengthMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        bar = new LinearProgressIndicator(context);
        bar.setMax(PasswordStrengthRule.Criterion.values().length);
        bar.setTrackThickness(getResources().getDimensionPixelSize(R.dimen.progress_height));
        bar.setTrackCornerRadius(getResources().getDimensionPixelSize(R.dimen.radius_xs));
        addView(bar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        label = new TextView(context);
        label.setTextAppearance(R.style.TextAppearance_PrintXpress_BodySmall);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_xxs);
        addView(label, params);
        update("");
    }

    /**
     * Re-scores a password.
     *
     * @param password the current password text
     */
    public void update(String password) {
        int score = PasswordStrengthRule.score(password);
        bar.setProgressCompat(score, true);
        int colour;
        int title;
        if (score == 0) {
            colour = R.color.px_outline;
            title = R.string.strength_empty;
        } else if (score <= 2) {
            colour = R.color.px_error_accent;
            title = R.string.strength_weak;
        } else if (score == 3) {
            colour = R.color.px_warning;
            title = R.string.strength_fair;
        } else if (score == 4) {
            colour = R.color.px_secondary;
            title = R.string.strength_good;
        } else {
            colour = R.color.px_success;
            title = R.string.strength_strong;
        }
        bar.setIndicatorColor(getContext().getColor(colour));
        bar.setTrackColor(getContext().getColor(R.color.px_surface_variant));
        String text = getContext().getString(title);
        Set<PasswordStrengthRule.Criterion> missing = PasswordStrengthRule.missing(password);
        if (score > 0 && !missing.isEmpty()) {
            text = text + " · " + getContext().getString(R.string.strength_missing, describe(missing));
        }
        label.setText(text);
        label.setTextColor(ColorStateList.valueOf(getContext().getColor(R.color.px_on_surface_variant)));
    }

    /**
     * Joins the missing criteria into a readable list.
     *
     * @param missing the missing criteria
     * @return e.g. "a number, a symbol"
     */
    private String describe(Set<PasswordStrengthRule.Criterion> missing) {
        List<String> parts = new ArrayList<>();
        for (PasswordStrengthRule.Criterion c : missing) {
            switch (c) {
                case LENGTH:
                    parts.add(getContext().getString(R.string.strength_length));
                    break;
                case UPPER:
                    parts.add(getContext().getString(R.string.strength_upper));
                    break;
                case LOWER:
                    parts.add(getContext().getString(R.string.strength_lower));
                    break;
                case DIGIT:
                    parts.add(getContext().getString(R.string.strength_digit));
                    break;
                default:
                    parts.add(getContext().getString(R.string.strength_symbol));
                    break;
            }
        }
        return String.join(getContext().getString(R.string.list_separator), parts);
    }
}
