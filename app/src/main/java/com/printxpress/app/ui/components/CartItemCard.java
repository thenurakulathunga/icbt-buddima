package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.databinding.ViewCartItemCardBinding;
import com.printxpress.app.domain.pricing.LineQuote;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * One cart line: thumbnail, product, chosen specs, artwork, quantity stepper, line total, and
 * edit and remove actions (FR-2.8).
 *
 * @since 1.0
 */
public class CartItemCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewCartItemCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public CartItemCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewCartItemCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a line.
     *
     * @param item  the cart line with product, options and design attached
     * @param quote its price
     */
    public void bind(CartItem item, LineQuote quote) {
        Product product = item.getProduct();
        binding.imgThumb.setArtwork(product.getImageRes(), ArtworkImageView.iconForCategory(product.getCategoryImageRes()));
        binding.txtName.setText(product.getName());
        binding.txtSpecs.setText(specsOf(item.getOptions()));
        Design design = item.getDesign();
        boolean hasFile = design != null && design.getFilePath() != null;
        binding.txtArtwork.setText(hasFile ? getContext().getString(R.string.design_file, design.getTitle())
                : getContext().getString(R.string.design_custom_text));
        binding.txtArtwork.setCompoundDrawablesRelativeWithIntrinsicBounds(
                hasFile ? R.drawable.ic_image : R.drawable.ic_text, 0, 0, 0);
        binding.stepper.setRange(product.getMinQty(), product.getMaxQty(), product.getQtyStep());
        binding.stepper.setValue(item.getQuantity());
        binding.stepper.setError(null);
        binding.txtTotal.setText(CurrencyFormatter.format(quote.getLineTotal()));
    }

    /**
     * Joins option values, e.g. "3.5" x 2" · 350gsm Matte · Double sided".
     *
     * @param options the chosen options
     * @return the summary
     */
    public String specsOf(List<ProductOption> options) {
        List<String> values = new ArrayList<>();
        for (ProductOption option : options) {
            values.add(option.getValue());
        }
        return String.join(getContext().getString(R.string.specs_separator), values);
    }

    /**
     * Registers the quantity callback.
     *
     * @param listener receives the typed or stepped value, or {@link QuantityStepper#INVALID}
     */
    public void setOnQuantityChanged(IntConsumer listener) {
        binding.stepper.setOnValueChangedListener(listener);
    }

    /**
     * Shows a quantity error under the stepper.
     *
     * @param message the message, or null
     */
    public void setQuantityError(CharSequence message) {
        binding.stepper.setError(message);
    }

    /**
     * Registers the edit action.
     *
     * @param listener run on tap
     */
    public void setOnEdit(Runnable listener) {
        binding.btnEdit.setOnClickListener(v -> listener.run());
    }

    /**
     * Registers the remove action.
     *
     * @param listener run on tap
     */
    public void setOnRemove(Runnable listener) {
        binding.btnRemove.setOnClickListener(v -> listener.run());
    }

    /**
     * Hides the edit, remove and stepper controls for read-only summaries (checkout review).
     *
     * @param readOnly true to hide the controls
     */
    public void setReadOnly(boolean readOnly) {
        int visibility = readOnly ? GONE : VISIBLE;
        binding.btnEdit.setVisibility(visibility);
        binding.btnRemove.setVisibility(visibility);
        binding.stepper.setVisibility(readOnly ? INVISIBLE : VISIBLE);
    }
}
