package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.databinding.ViewProductCardBinding;

/**
 * Product tile with image, name, starting price and turnaround (FR-2.2): Home and Product list.
 * The starting price is the minimum order quantity times the unit price.
 *
 * @since 1.0
 */
public class ProductCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewProductCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public ProductCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewProductCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a product.
     *
     * @param product the product
     */
    public void bind(Product product) {
        binding.imgProduct.setArtwork(product.getImageRes(), ArtworkImageView.iconForCategory(product.getCategoryImageRes()));
        binding.txtName.setText(product.getName());
        String from = CurrencyFormatter.format(product.getBasePrice().times(product.getMinQty()));
        binding.txtPrice.setText(getContext().getString(R.string.price_from, from));
        binding.txtTurnaround.setText(getResources().getQuantityString(R.plurals.turnaround_days,
                product.getTurnaroundDays(), product.getTurnaroundDays()));
        setContentDescription(product.getName() + ", " + binding.txtPrice.getText());
    }
}
