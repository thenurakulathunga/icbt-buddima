package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.databinding.ItemCategoryBinding;
import com.printxpress.app.databinding.ItemCategoryRowBinding;
import com.printxpress.app.ui.components.CategoryCard;

/**
 * Category cards for the Catalog grid and the Home row.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class CategoryAdapter extends BaseListAdapter<Category, ViewBinding> {

    /** Whether items are fixed-width row cards. */
    private final boolean row;

    /**
     * Creates the adapter.
     *
     * @param row true for fixed-width row cards
     */
    public CategoryAdapter(boolean row) {
        super(Category::getCategoryId, c -> c.getName() + c.getProductCount());
        this.row = row;
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ViewBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return row ? ItemCategoryRowBinding.inflate(inflater, parent, false)
                : ItemCategoryBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a category.
     *
     * @param binding  the row binding
     * @param item     the category
     * @param position the position
     */
    @Override
    protected void bind(ViewBinding binding, Category item, int position) {
        ((CategoryCard) binding.getRoot()).bind(item);
    }
}
