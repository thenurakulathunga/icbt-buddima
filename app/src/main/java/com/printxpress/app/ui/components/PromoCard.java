package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.DrawableResolver;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.databinding.ViewPromoCardBinding;

import java.util.function.Consumer;

/**
 * Promotion card on the promo gradient (or its banner image when added) with a tap-to-copy code
 * chip (FR-4.5, TC-56). Home carousel and Offers list.
 *
 * @since 1.0
 */
public class PromoCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewPromoCardBinding binding;
    /** Code copy callback, may be null. */
    private Consumer<String> onCodeClick;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PromoCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewPromoCardBinding.inflate(LayoutInflater.from(context), this);
        setStrokeWidth(0);
        setShapeAppearanceModel(getShapeAppearanceModel().withCornerSize(getResources().getDimension(R.dimen.radius_xl)));
    }

    /**
     * Shows a promotion.
     *
     * @param promotion the promotion
     */
    public void bind(Promotion promotion) {
        binding.txtTitle.setText(promotion.getTitle());
        binding.txtDescription.setText(promotion.getDescription());
        binding.chipCode.setText(promotion.getPromoCode());
        binding.chipCode.setContentDescription(getContext().getString(R.string.promo_code_label, promotion.getPromoCode())
                + ", " + getContext().getString(R.string.copy_code_hint));
        boolean hasImage = DrawableResolver.exists(getContext(), promotion.getImageRes());
        binding.imgBanner.setImageResource(hasImage
                ? DrawableResolver.resolve(getContext(), promotion.getImageRes()) : R.drawable.bg_gradient_promo);
        binding.imgDecor.setVisibility(hasImage ? GONE : VISIBLE);
        binding.chipCode.setOnClickListener(v -> {
            if (onCodeClick != null) {
                onCodeClick.accept(promotion.getPromoCode());
            }
        });
        setContentDescription(promotion.getTitle());
    }

    /**
     * Registers the code chip callback.
     *
     * @param listener receives the promo code
     */
    public void setOnCodeClick(Consumer<String> listener) {
        this.onCodeClick = listener;
    }
}
