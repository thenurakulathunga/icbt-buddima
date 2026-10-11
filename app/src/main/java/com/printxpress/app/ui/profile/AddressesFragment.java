package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.databinding.FragmentAddressesBinding;
import com.printxpress.app.ui.adapters.AddressAdapter;
import com.printxpress.app.ui.components.AppDialog;
import com.printxpress.app.ui.components.SpacingItemDecoration;

/**
 * Manage addresses (S32, FR-1.6): list, add, edit and delete with confirmation.
 *
 * @since 1.0
 */
public class AddressesFragment extends BaseFragment<FragmentAddressesBinding> {

    /** Screen state. */
    private AddressesViewModel viewModel;
    /** The list adapter. */
    private final AddressAdapter adapter = new AddressAdapter(false);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public AddressesFragment() {
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
    protected FragmentAddressesBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAddressesBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(AddressesViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listAddresses.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listAddresses.setAdapter(adapter);
    }

    /** Renders the list states. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getAddresses(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.getData().isEmpty()) {
                binding.stateLayout.showEmpty(R.drawable.ic_location, R.string.empty_addresses_title,
                        R.string.empty_addresses_message, 0, null);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires add, edit and delete. */
    @Override
    protected void initListeners() {
        binding.bottomBar.setOnButtonClick(() -> openForm(0));
        adapter.setOnItemClickListener((a, p) -> openForm(a.getAddressId()));
        adapter.setOnEdit(a -> openForm(a.getAddressId()));
        adapter.setOnDelete(a -> new AppDialog.Builder(requireContext()).icon(R.drawable.ic_delete)
                .title(R.string.delete_address_title).message(R.string.delete_address_message)
                .positive(R.string.action_delete, () -> viewModel.delete(a)).negative(R.string.action_cancel)
                .destructive(true).show());
    }

    /**
     * Opens the address form.
     *
     * @param addressId the address to edit, or 0 for a new one
     */
    private void openForm(long addressId) {
        Bundle args = new Bundle();
        args.putLong(Constants.ARG_ADDRESS_ID, addressId);
        navigate(R.id.addressFormFragment, args);
    }

    /** Reloads when returning from the form. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
