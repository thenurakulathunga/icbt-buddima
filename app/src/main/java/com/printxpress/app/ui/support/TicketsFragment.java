package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentTicketsBinding;
import com.printxpress.app.ui.adapters.TicketAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * My tickets (S29, FR-5.3): requests sent to the support team, newest first.
 *
 * @since 1.0
 */
public class TicketsFragment extends BaseFragment<FragmentTicketsBinding> {

    /** Screen state. */
    private TicketsViewModel viewModel;
    /** The rows. */
    private final TicketAdapter adapter = new TicketAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public TicketsFragment() {
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
    protected FragmentTicketsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentTicketsBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(TicketsViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listTickets.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listTickets.setAdapter(adapter);
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observe(viewModel.getTickets(), r -> {
            if (r.isLoading() && adapter.getItemCount() == 0) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.isSuccess() && r.getData().isEmpty()) {
                binding.stateLayout.showEmpty(R.drawable.ic_ticket, R.string.empty_tickets_title,
                        R.string.empty_tickets_message, R.string.menu_contact, () -> ShellRoutes.contact(nav(), 0));
            } else if (r.isSuccess()) {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Reloads, e.g. after sending a ticket. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
