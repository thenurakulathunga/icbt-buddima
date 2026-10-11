package com.printxpress.app.ui.components;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.google.android.material.textview.MaterialTextView;
import com.printxpress.app.R;

/**
 * The "PrintXpress" wordmark in Roboto Flex ExtraBold with the X in brand blue (docs/07 §1).
 * On brand-coloured backgrounds the whole wordmark can be drawn in white.
 *
 * @since 1.0
 */
public class BrandLogoView extends MaterialTextView {

    /**
     * Creates the wordmark.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public BrandLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        render(false);
        setContentDescription(context.getString(R.string.cd_logo));
    }

    /**
     * Renders the wordmark.
     *
     * @param onBrand true to draw everything in white for gradient backgrounds
     */
    public void render(boolean onBrand) {
        Context ctx = getContext();
        int accent = onBrand ? ctx.getColor(R.color.px_on_dark) : resolve(androidx.appcompat.R.attr.colorPrimary);
        SpannableStringBuilder text = new SpannableStringBuilder(ctx.getString(R.string.brand_print));
        int start = text.length();
        text.append(ctx.getString(R.string.brand_x));
        text.setSpan(new ForegroundColorSpan(accent), start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.append(ctx.getString(R.string.brand_press));
        if (onBrand) {
            setTextColor(ctx.getColor(R.color.px_on_dark));
        }
        setText(text);
    }

    /**
     * Reads a colour attribute.
     *
     * @param attr the attribute
     * @return the colour
     */
    private int resolve(int attr) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }
}
