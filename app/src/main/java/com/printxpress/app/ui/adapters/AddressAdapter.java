package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.databinding.ItemAddressBinding;

import java.util.function.Consumer;

/**
 * Address cards in manage mode (Addresses screen) or selectable mode (Checkout).
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class AddressAdapter extends BaseListAdapter<Address, ItemAddressBinding> {

    /** Whether cards are selectable. */
    private final boolean selectable;
    /** Selected address id in selectable mode. */
    private long selectedId = -1;
    /** Edit callback. */
    private Consumer<Address> onEdit;
    /** Delete callback. */
    private Consumer<Address> onDelete;

    /**
     * Creates the adapter.
     *
     * @param selectable true for checkout selection
     */
    public AddressAdapter(boolean selectable) {
        super(Address::getAddressId, a -> a.getLabel() + a.getLine1() + a.getCity() + a.getPostalCode()
                + a.isDefaultAddress() + a.getDistrictId() + a.getRecipientName());
        this.selectable = selectable;
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemAddressBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemAddressBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds an address.
     *
     * @param binding  the row binding
     * @param item     the address
     * @param position the position
     */
    @Override
    protected void bind(ItemAddressBinding binding, Address item, int position) {
        binding.card.bind(item);
        binding.card.setSelectable(selectable);
        binding.card.setChecked(selectable && item.getAddressId() == selectedId);
        binding.card.setOnEdit(() -> {
            if (onEdit != null) {
                onEdit.accept(item);
            }
        });
        binding.card.setOnDelete(() -> {
            if (onDelete != null) {
                onDelete.accept(item);
            }
        });
    }

    /**
     * Marks the selected address.
     *
     * @param addressId the address id
     */
    public void setSelectedId(long addressId) {
        selectedId = addressId;
        notifyDataSetChanged();
    }

    /**
     * Registers the edit callback.
     *
     * @param listener receives the address
     */
    public void setOnEdit(Consumer<Address> listener) {
        this.onEdit = listener;
    }

    /**
     * Registers the delete callback.
     *
     * @param listener receives the address
     */
    public void setOnDelete(Consumer<Address> listener) {
        this.onDelete = listener;
    }
}
