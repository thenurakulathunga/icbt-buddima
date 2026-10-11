package com.printxpress.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentOrdersBinding;
import com.printxpress.app.ui.adapters.OrderAdapter;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

/**
 * My orders tab (S17, FR-3.1): Active and Past orders with status badges; tap to track.
 *
 * @since 1.0
 */
public class OrdersFragment extends BaseFragment<FragmentOrdersBinding> {

    /** Screen state. */
    private OrdersViewModel viewModel;
    /** Order list. */
    private final OrderAdapter adapter = new OrderAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public OrdersFragment() {
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
    protected FragmentOrdersBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOrdersBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the tabs and list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(OrdersViewModel.class);
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
        binding.listOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listOrders.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listOrders.setAdapter(adapter);
        Integer tab = viewModel.getTab().getValue();
        TabLayout.Tab current = binding.tabs.getTabAt(tab == null ? 0 : tab);
        if (current != null) {
            current.select();
        }
    }

    /** Renders the list states. */
    @Override
    protected void initObservers() {
        observe(viewModel.getOrders(), r -> {
            binding.swipeRefresh.setRefreshing(false);
            if (r.isLoading() && adapter.getItemCount() == 0) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.isSuccess()) {
                showList(r.getData() == null || r.getData().isEmpty());
                adapter.submitList(r.getData());
            }
        });
    }

    /**
     * Shows the list, the empty state or the guest state.
     *
     * @param empty whether there are no orders
     */
    private void showList(boolean empty) {
        if (!viewModel.isLoggedIn()) {
            binding.stateLayout.showEmpty(R.drawable.ic_nav_orders, R.string.guest_title, R.string.guest_orders_message,
                    R.string.action_sign_in, () -> startActivity(AuthActivity.returning(requireContext())));
        } else if (empty) {
            boolean past = Integer.valueOf(OrdersViewModel.TAB_PAST).equals(viewModel.getTab().getValue());
            binding.stateLayout.showEmpty(R.drawable.ic_package,
                    past ? R.string.empty_past_title : R.string.empty_active_title,
                    past ? R.string.empty_past_message : R.string.empty_active_message,
                    R.string.action_start_order, () -> nav().navigate(R.id.catalogFragment));
        } else {
            binding.stateLayout.showContent();
        }
    }

    /** Wires tabs, refresh and taps. */
    @Override
    protected void initListeners() {
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.submitList(null);
                viewModel.selectTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewModel.refresh();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(viewModel::refresh);
        adapter.setOnItemClickListener((order, p) -> ShellRoutes.orderDetail(nav(), order.getOrderId()));
    }

    /** Reloads when shown (orders progress in the background). */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
