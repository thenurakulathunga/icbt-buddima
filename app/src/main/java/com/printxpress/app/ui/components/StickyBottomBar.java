package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewStickyBottomBarBinding;

/**
 * Thumb-reachable bottom bar with a price on the left and the primary action on the right
 * (docs/05 §9 rule 1): Product detail, Configure, Cart, Checkout.
 *
 * @since 1.0
 */
public class StickyBottomBar extends LinearLayout {

    /** The inflated children. */
    private final ViewStickyBottomBarBinding binding;

    /**
     * Creates the bar.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public StickyBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setBackgroundColor(getContext().getColor(R.color.px_surface));
        binding = ViewStickyBottomBarBinding.inflate(LayoutInflater.from(context), this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyBottomBar);
        try {
            setLabel(a.getString(R.styleable.StickyBottomBar_px_label));
            binding.btnAction.setText(a.getString(R.styleable.StickyBottomBar_px_buttonText));
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the small label above the price; null hides the price area.
     *
     * @param label the label
     */
    public void setLabel(CharSequence label) {
        binding.txtLabel.setText(label);
        binding.priceBox.setVisibility(label == null ? GONE : VISIBLE);
        LayoutParams params = (LayoutParams) binding.btnAction.getLayoutParams();
        params.setMarginStart(label == null ? 0 : getResources().getDimensionPixelSize(R.dimen.space_md));
        binding.btnAction.setLayoutParams(params);
    }

    /**
     * Sets the price text.
     *
     * @param price the formatted price
     */
    public void setPrice(CharSequence price) {
        binding.txtPrice.setText(price);
    }

    /**
     * Sets the button label.
     *
     * @param textRes the label
     */
    public void setButtonText(@StringRes int textRes) {
        binding.btnAction.setText(textRes);
    }

    /**
     * Shows or hides the button spinner.
     *
     * @param loading true while working
     */
    public void setLoading(boolean loading) {
        binding.btnAction.setLoading(loading);
    }

    /**
     * Enables or disables the button.
     *
     * @param enabled true to allow taps
     */
    public void setButtonEnabled(boolean enabled) {
        binding.btnAction.setEnabled(enabled);
    }

    /**
     * Registers the button action.
     *
     * @param listener run on tap
     */
    public void setOnButtonClick(Runnable listener) {
        binding.btnAction.setOnClickListener(v -> listener.run());
    }

    /** @return the action button, for tests */
    public LoadingButton getButton() {
        return binding.btnAction;
    }
}
