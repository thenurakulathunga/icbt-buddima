package com.printxpress.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.databinding.FragmentCartBinding;
import com.printxpress.app.ui.adapters.CartAdapter;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.components.AppDialog;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * Cart (S13, FR-2.8): change quantities, edit or remove lines, then check out.
 *
 * @since 1.0
 */
public class CartFragment extends BaseFragment<FragmentCartBinding> {

    /** Screen state. */
    private CartViewModel viewModel;
    /** Cart lines. */
    private CartAdapter adapter;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public CartFragment() {
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
    protected FragmentCartBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentCartBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(CartViewModel.class);
        adapter = new CartAdapter(viewModel.getCalculator(), false);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listItems.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listItems.setAdapter(adapter);
    }

    /** Renders the cart states. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getItems(), r -> {
            if (r.isLoading() && adapter.getItemCount() == 0) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.isSuccess()) {
                showItems(r.getData() == null || r.getData().isEmpty());
                adapter.submitList(r.getData());
            }
        });
        observe(viewModel.getPrice(), p -> {
            binding.priceSummary.bind(p);
            binding.bottomBar.setPrice(CurrencyFormatter.format(p.getTotal()));
        });
        observe(viewModel.getQuantityError(), e -> {
            long[] value = e.getContentIfNotHandled();
            if (value != null) {
                adapter.setQuantityError(value[0], value[1] == 1);
            }
        });
    }

    /**
     * Shows the list, the empty state or the guest state.
     *
     * @param empty whether there are no lines
     */
    private void showItems(boolean empty) {
        binding.bottomBar.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (!viewModel.isLoggedIn()) {
            binding.stateLayout.showEmpty(R.drawable.ic_cart, R.string.cart_guest_title, R.string.cart_guest_message,
                    R.string.action_sign_in, () -> startActivity(AuthActivity.returning(requireContext())));
        } else if (empty) {
            binding.stateLayout.showEmpty(R.drawable.ic_cart, R.string.empty_cart_title, R.string.empty_cart_message,
                    R.string.action_browse, () -> nav().navigate(R.id.catalogFragment));
        } else {
            binding.stateLayout.showContent();
        }
    }

    /** Wires line actions and checkout. */
    @Override
    protected void initListeners() {
        adapter.setOnQuantity(viewModel::updateQuantity);
        adapter.setOnEdit(item -> ShellRoutes.configure(nav(), item.getProductId(), item.getCartItemId()));
        adapter.setOnRemove(item -> new AppDialog.Builder(requireContext()).icon(R.drawable.ic_delete)
                .title(R.string.remove_item_title).message(R.string.remove_item_message)
                .positive(R.string.action_remove, () -> viewModel.remove(item)).negative(R.string.action_cancel)
                .destructive(true).show());
        binding.bottomBar.setOnButtonClick(() -> navigate(R.id.nav_checkout, null));
    }

    /** Reloads when returning. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
