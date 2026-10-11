package com.printxpress.app.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.Branch;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.databinding.FragmentCheckoutBinding;
import com.printxpress.app.ui.adapters.AddressAdapter;
import com.printxpress.app.ui.components.SelectableCardGroup;
import com.printxpress.app.ui.components.SpacingItemDecoration;

/**
 * Checkout – fulfilment and schedule (S14, FR-2.9, FR-2.10): pickup at a branch or delivery to an
 * address, then an allowed date and slot (BR-6; TC-35–TC-37).
 *
 * @since 1.0
 */
public class CheckoutFragment extends BaseFragment<FragmentCheckoutBinding> {

    /** Flow state shared with Review. */
    private CheckoutViewModel viewModel;
    /** Pickup / delivery radio group. */
    private SelectableCardGroup fulfilmentGroup;
    /** Selectable addresses. */
    private final AddressAdapter addressAdapter = new AddressAdapter(true);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public CheckoutFragment() {
        super();
    }

    /**
     * Inflates the layout.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @return the binding
     */
    @Override
    protected FragmentCheckoutBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentCheckoutBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the cards and address list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = graphViewModel(R.id.nav_checkout, CheckoutViewModel.class);
        viewModel.start();
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        fulfilmentGroup = SelectableCardGroup.of(binding.cardPickup, binding.cardDelivery);
        binding.listAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listAddresses.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_sm)));
        binding.listAddresses.setAdapter(addressAdapter);
    }

    /** Renders the fulfilment, lists, schedule and errors. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFulfilment(), type -> {
            boolean delivery = type == FulfilmentType.DELIVERY;
            fulfilmentGroup.select(delivery ? 1 : 0, false);
            binding.sectionPickup.setVisibility(delivery ? View.GONE : View.VISIBLE);
            binding.sectionDelivery.setVisibility(delivery ? View.VISIBLE : View.GONE);
        });
        observe(viewModel.getBranches(), r -> {
            if (r.isSuccess()) {
                binding.dropBranch.setItems(r.getData(), Branch::getName);
                Branch chosen = viewModel.getBranch().getValue();
                if (chosen != null) {
                    binding.dropBranch.<Branch>selectWhere(b -> b.getBranchId() == chosen.getBranchId());
                }
            }
        });
        observe(viewModel.getBranch(), b -> {
            binding.txtBranchDetails.setVisibility(b == null ? View.GONE : View.VISIBLE);
            if (b != null) {
                binding.txtBranchDetails.setText(getString(R.string.branch_details, b.getAddress(), b.getPhone(),
                        b.getOpeningHours()));
            }
        });
        observe(viewModel.getAddresses(), r -> {
            if (r.isSuccess()) {
                addressAdapter.submitList(r.getData());
            }
        });
        observe(viewModel.getAddress(), a -> addressAdapter.setSelectedId(a == null ? -1 : a.getAddressId()));
        observe(viewModel.getDays(), days -> {
            binding.bannerTurnaround.setMessage(getResources().getQuantityString(R.plurals.checkout_turnaround_note,
                    viewModel.getTurnaround(), viewModel.getTurnaround()));
            binding.dateSlotPicker.setDays(days, viewModel.availability());
            binding.dateSlotPicker.setSelection(viewModel.getDate(), viewModel.getSlot());
        });
        observe(viewModel.getPrice(), p -> binding.bottomBar.setPrice(CurrencyFormatter.format(p.getTotal())));
        observe(viewModel.getStepErrors(), this::showErrors);
        observe(viewModel.getToReview(), e -> {
            if (e.getContentIfNotHandled() != null) {
                navigate(R.id.action_checkout_to_review, null);
            }
        });
    }

    /** Wires the controls. */
    @Override
    protected void initListeners() {
        fulfilmentGroup.setOnSelectionChanged(index ->
                viewModel.setFulfilment(index == 1 ? FulfilmentType.DELIVERY : FulfilmentType.PICKUP));
        binding.dropBranch.<Branch>setOnItemSelected(viewModel::selectBranch);
        addressAdapter.setOnItemClickListener((a, p) -> viewModel.selectAddress(a));
        binding.headerAddress.setOnActionClick(() -> {
            Bundle args = new Bundle();
            args.putLong(Constants.ARG_ADDRESS_ID, 0);
            navigate(R.id.addressFormFragment, args);
        });
        binding.dateSlotPicker.setOnSelectionChangedListener(viewModel::setSchedule);
        binding.bottomBar.setOnButtonClick(viewModel::continueToReview);
    }

    /**
     * Shows the step errors.
     *
     * @param errors {where, date, slot}
     */
    private void showErrors(int[] errors) {
        boolean delivery = viewModel.getFulfilment().getValue() == FulfilmentType.DELIVERY;
        binding.dropBranch.setError(delivery ? 0 : errors[0]);
        binding.txtAddressError.setText(errors[0] == 0 ? null : getString(errors[0]));
        binding.txtAddressError.setVisibility(delivery && errors[0] != 0 ? View.VISIBLE : View.GONE);
        binding.dateSlotPicker.setDateError(errors[1]);
        binding.dateSlotPicker.setSlotError(errors[2]);
    }

    /** Reloads addresses after returning from the address form. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.reloadAddresses();
    }
}
