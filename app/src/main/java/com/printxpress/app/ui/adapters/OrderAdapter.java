package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.databinding.ItemOrderBinding;

/**
 * Order cards for My orders.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class OrderAdapter extends BaseListAdapter<Order, ItemOrderBinding> {

    /** Creates the adapter. */
    public OrderAdapter() {
        super(Order::getOrderId, o -> o.getStatus() + "|" + o.getScheduledDate() + "|" + o.getScheduledSlot()
                + "|" + o.getTotal());
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemOrderBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemOrderBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds an order.
     *
     * @param binding  the row binding
     * @param item     the order
     * @param position the position
     */
    @Override
    protected void bind(ItemOrderBinding binding, Order item, int position) {
        binding.card.bind(item);
    }
}
