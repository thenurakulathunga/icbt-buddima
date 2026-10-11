package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewSelectableOptionCardBinding;

/**
 * Checkable card with an icon, title and subtitle, used for Pickup vs Delivery and payment method.
 * Group several with {@link SelectableCardGroup} for single choice.
 *
 * @since 1.0
 */
public class SelectableOptionCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewSelectableOptionCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public SelectableOptionCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewSelectableOptionCardBinding.inflate(LayoutInflater.from(context), this);
        setCheckable(true);
        setCheckedIcon(null);
        setStrokeColor(getContext().getColorStateList(R.color.selector_selectable_stroke));
        setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_focus));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableOptionCard);
        try {
            binding.imgIcon.setImageResource(a.getResourceId(R.styleable.SelectableOptionCard_px_icon, R.drawable.ic_check));
            setTitle(a.getString(R.styleable.SelectableOptionCard_px_title));
            setSubtitle(a.getString(R.styleable.SelectableOptionCard_px_subtitle));
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the title.
     *
     * @param title the title
     */
    public void setTitle(CharSequence title) {
        binding.txtTitle.setText(title);
    }

    /**
     * Sets the subtitle; null hides it.
     *
     * @param subtitle the subtitle
     */
    public void setSubtitle(CharSequence subtitle) {
        binding.txtSubtitle.setText(subtitle);
        binding.txtSubtitle.setVisibility(subtitle == null ? GONE : VISIBLE);
    }

    /**
     * Checks or unchecks the card and updates the check mark.
     *
     * @param checked the new state
     */
    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (binding != null) {
            binding.imgCheck.setVisibility(checked ? VISIBLE : INVISIBLE);
        }
    }
}
