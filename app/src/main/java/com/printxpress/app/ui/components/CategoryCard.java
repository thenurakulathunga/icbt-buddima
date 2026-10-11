package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.DrawableResolver;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.databinding.ViewCategoryCardBinding;

/**
 * Category tile with its Hugeicon on a tinted square, name and product count (Home row, Catalog grid).
 *
 * @since 1.0
 */
public class CategoryCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewCategoryCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public CategoryCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewCategoryCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a category.
     *
     * @param category the category
     */
    public void bind(Category category) {
        binding.imgIcon.setImageResource(DrawableResolver.resolve(getContext(), category.getIconRes(), "ic_layers"));
        binding.txtName.setText(category.getName());
        binding.txtCount.setText(getResources().getQuantityString(R.plurals.product_count,
                category.getProductCount(), category.getProductCount()));
        setContentDescription(category.getName());
    }
}
