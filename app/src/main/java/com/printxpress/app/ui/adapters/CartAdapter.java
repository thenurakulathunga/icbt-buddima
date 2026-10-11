package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.databinding.ItemCartBinding;
import com.printxpress.app.domain.pricing.PriceCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Cart lines with quantity stepper, edit and remove; read-only on the review screen.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class CartAdapter extends BaseListAdapter<CartItem, ItemCartBinding> {

    /** Prices each line. */
    private final PriceCalculator calculator;
    /** Whether controls are hidden. */
    private final boolean readOnly;
    /** Quantity errors by cart line. */
    private final Map<Long, Boolean> quantityErrors = new HashMap<>();
    /** Quantity callback. */
    private BiConsumer<CartItem, Integer> onQuantity;
    /** Edit callback. */
    private Consumer<CartItem> onEdit;
    /** Remove callback. */
    private Consumer<CartItem> onRemove;

    /**
     * Creates the adapter.
     *
     * @param calculator price calculator
     * @param readOnly   true for the review screen
     */
    public CartAdapter(PriceCalculator calculator, boolean readOnly) {
        super(CartItem::getCartItemId, c -> c.getQuantity() + "|" + c.getOptions().size() + "|" + c.getDesignId()
                + "|" + c.getCustomText());
        this.calculator = calculator;
        this.readOnly = readOnly;
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemCartBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemCartBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a cart line.
     *
     * @param binding  the row binding
     * @param item     the line
     * @param position the position
     */
    @Override
    protected void bind(ItemCartBinding binding, CartItem item, int position) {
        binding.card.setOnQuantityChanged(null);
        binding.card.bind(item, calculator.quoteLine(item.getProduct(), item.getOptions(), item.getQuantity()));
        binding.card.setReadOnly(readOnly);
        Product p = item.getProduct();
        binding.card.setQuantityError(Boolean.TRUE.equals(quantityErrors.get(item.getCartItemId()))
                ? binding.getRoot().getContext().getString(R.string.err_qty_range, p.getMinQty(), p.getMaxQty(),
                p.getQtyStep()) : null);
        binding.card.setOnQuantityChanged(qty -> {
            if (onQuantity != null && qty != item.getQuantity()) {
                onQuantity.accept(item, qty);
            }
        });
        binding.card.setOnEdit(() -> {
            if (onEdit != null) {
                onEdit.accept(item);
            }
        });
        binding.card.setOnRemove(() -> {
            if (onRemove != null) {
                onRemove.accept(item);
            }
        });
    }

    /**
     * Shows or clears a quantity error on a line.
     *
     * @param cartItemId the line
     * @param error      true to show
     */
    public void setQuantityError(long cartItemId, boolean error) {
        quantityErrors.put(cartItemId, error);
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getCartItemId() == cartItemId) {
                notifyItemChanged(i);
            }
        }
    }

    /**
     * Registers the quantity callback.
     *
     * @param listener receives the line and new quantity
     */
    public void setOnQuantity(BiConsumer<CartItem, Integer> listener) {
        this.onQuantity = listener;
    }

    /**
     * Registers the edit callback.
     *
     * @param listener receives the line
     */
    public void setOnEdit(Consumer<CartItem> listener) {
        this.onEdit = listener;
    }

    /**
     * Registers the remove callback.
     *
     * @param listener receives the line
     */
    public void setOnRemove(Consumer<CartItem> listener) {
        this.onRemove = listener;
    }
}
