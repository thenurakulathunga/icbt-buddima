package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.databinding.ItemProductBinding;
import com.printxpress.app.databinding.ItemProductRowBinding;
import com.printxpress.app.ui.components.ProductCard;

/**
 * Product cards for grids (Product list) and horizontal rows (Home).
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class ProductAdapter extends BaseListAdapter<Product, ViewBinding> {

    /** Whether items are fixed-width row cards. */
    private final boolean row;

    /**
     * Creates the adapter.
     *
     * @param row true for fixed-width row cards
     */
    public ProductAdapter(boolean row) {
        super(Product::getProductId, p -> p.getName() + p.getBasePrice() + p.getImageRes());
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
        return row ? ItemProductRowBinding.inflate(inflater, parent, false) : ItemProductBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a product.
     *
     * @param binding  the row binding
     * @param item     the product
     * @param position the position
     */
    @Override
    protected void bind(ViewBinding binding, Product item, int position) {
        ((ProductCard) binding.getRoot()).bind(item);
    }
}
