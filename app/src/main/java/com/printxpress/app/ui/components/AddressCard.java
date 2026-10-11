package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.databinding.ViewAddressCardBinding;

/**
 * Delivery address card (FR-1.6). In manage mode it shows edit and delete; in selectable mode
 * (checkout) it shows a check mark when chosen.
 *
 * @since 1.0
 */
public class AddressCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewAddressCardBinding binding;
    /** Whether the card is used for choosing. */
    private boolean selectable;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public AddressCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewAddressCardBinding.inflate(LayoutInflater.from(context), this);
        setCheckedIcon(null);
        setStrokeColor(getContext().getColorStateList(R.color.selector_selectable_stroke));
    }

    /**
     * Shows an address.
     *
     * @param address the address with district joined
     */
    public void bind(Address address) {
        binding.txtLabel.setText(address.getLabel());
        binding.txtDefault.setVisibility(address.isDefaultAddress() ? VISIBLE : GONE);
        binding.txtRecipient.setText(address.getRecipientName() + " · " + address.getRecipientPhone());
        StringBuilder lines = new StringBuilder(address.getLine1());
        if (address.getLine2() != null && !address.getLine2().trim().isEmpty()) {
            lines.append(", ").append(address.getLine2());
        }
        lines.append('\n').append(address.getCity()).append(' ').append(address.getPostalCode());
        binding.txtLines.setText(lines);
        binding.txtFee.setText(getContext().getString(R.string.address_fee, address.getDistrictName(),
                CurrencyFormatter.format(address.getDeliveryFee())));
        binding.imgIcon.setImageResource(getContext().getString(R.string.address_label_work)
                .equalsIgnoreCase(address.getLabel())
                ? R.drawable.ic_work_address : R.drawable.ic_home_address);
    }

    /**
     * Switches between manage mode and selectable mode.
     *
     * @param value true for checkout selection
     */
    public void setSelectable(boolean value) {
        selectable = value;
        setCheckable(value);
        binding.actions.setVisibility(value ? GONE : VISIBLE);
        binding.imgCheck.setVisibility(value && isChecked() ? VISIBLE : GONE);
        setStrokeWidth(getResources().getDimensionPixelSize(value ? R.dimen.stroke_focus : R.dimen.stroke_default));
    }

    /**
     * Checks the card in selectable mode.
     *
     * @param checked the new state
     */
    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (binding != null) {
            binding.imgCheck.setVisibility(selectable && checked ? VISIBLE : GONE);
        }
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
     * Registers the delete action.
     *
     * @param listener run on tap
     */
    public void setOnDelete(Runnable listener) {
        binding.btnDelete.setOnClickListener(v -> listener.run());
    }
}
