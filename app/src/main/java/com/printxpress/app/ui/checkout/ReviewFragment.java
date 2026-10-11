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
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.Branch;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.databinding.FragmentReviewBinding;
import com.printxpress.app.ui.adapters.CartAdapter;
import com.printxpress.app.ui.components.LoadingOverlay;
import com.printxpress.app.ui.components.SpacingItemDecoration;

/**
 * Checkout – review and pay (S15, FR-2.11, FR-2.12): items, where and when, promo code, payment
 * method (the only offline method matching the fulfilment), then Place order behind a blocking
 * overlay so the transaction cannot be submitted twice.
 *
 * @since 1.0
 */
public class ReviewFragment extends BaseFragment<FragmentReviewBinding> {

    /** Flow state shared with Checkout. */
    private CheckoutViewModel viewModel;
    /** Read-only lines. */
    private CartAdapter adapter;
    /** Blocking progress while placing the order. */
    private LoadingOverlay overlay;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ReviewFragment() {
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
    protected FragmentReviewBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentReviewBinding.inflate(inflater, container, false);
    }

    /**
     * Fills the summary.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = graphViewModel(R.id.nav_checkout, CheckoutViewModel.class);
        overlay = new LoadingOverlay(requireContext());
        adapter = new CartAdapter(viewModel.getCalculator(), true);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listItems.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_sm)));
        binding.listItems.setAdapter(adapter);
        binding.inputNotes.setText(viewModel.getNotes());
        showFulfilment();
    }

    /** Renders lines, promo, price and the result. */
    @Override
    protected void initObservers() {
        observe(viewModel.getItems(), r -> {
            if (r.isSuccess()) {
                adapter.submitList(r.getData());
            }
        });
        observe(viewModel.getPromotion(), promo -> {
            binding.btnRemovePromo.setVisibility(promo == null ? View.GONE : View.VISIBLE);
            binding.inputPromo.setEnabled(promo == null);
            binding.btnApply.setEnabled(promo == null);
            if (promo != null) {
                binding.inputPromo.setText(promo.getPromoCode());
                binding.inputPromo.setHelper(getString(R.string.promo_applied, promo.getPromoCode()));
            } else {
                binding.inputPromo.setHelper(null);
            }
        });
        observe(viewModel.getPromoError(), res -> binding.inputPromo.setError(res == null ? 0 : res));
        observe(viewModel.getPrice(), p -> {
            binding.priceSummary.bind(p);
            binding.bottomBar.setPrice(CurrencyFormatter.format(p.getTotal()));
        });
        observe(viewModel.getLoading(), busy -> {
            binding.btnApply.setLoading(false);
            overlay.setVisible(busy);
        });
        observeMessages(viewModel);
        observe(viewModel.getPlaced(), e -> {
            Order order = e.getContentIfNotHandled();
            if (order != null) {
                Bundle args = new Bundle();
                args.putLong(Constants.ARG_ORDER_ID, order.getOrderId());
                navigate(R.id.action_review_to_success, args);
            }
        });
    }

    /** Wires promo, notes and Place order. */
    @Override
    protected void initListeners() {
        binding.btnApply.setOnClickListener(v -> {
            KeyboardUtils.hide(v);
            viewModel.applyPromo(binding.inputPromo.getText());
        });
        binding.btnRemovePromo.setOnClickListener(v -> {
            binding.inputPromo.setText("");
            viewModel.removePromo();
        });
        binding.inputNotes.setOnTextChanged(viewModel::setNotes);
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            viewModel.placeOrder();
        });
    }

    /** Shows where and when, and the matching payment method. */
    private void showFulfilment() {
        boolean delivery = viewModel.getFulfilment().getValue() == FulfilmentType.DELIVERY;
        binding.tableFulfilment.clear();
        binding.tableFulfilment.addRow(getString(R.string.review_method),
                getString(delivery ? R.string.fulfilment_delivery : R.string.fulfilment_pickup));
        if (delivery) {
            Address a = viewModel.getAddress().getValue();
            binding.tableFulfilment.addRow(getString(R.string.review_where),
                    a == null ? "" : a.getLine1() + ", " + a.getCity());
        } else {
            Branch b = viewModel.getBranch().getValue();
            binding.tableFulfilment.addRow(getString(R.string.review_where), b == null ? "" : b.getName());
        }
        binding.tableFulfilment.addRow(getString(R.string.review_when),
                DateTimeUtils.formatDisplay(viewModel.getDate()) + "\n" + viewModel.getSlot());
        binding.cardPayPickup.setChecked(!delivery);
        binding.cardPayCod.setChecked(delivery);
        binding.cardPayPickup.setEnabled(!delivery);
        binding.cardPayCod.setEnabled(delivery);
        binding.cardPayPickup.setAlpha(delivery ? 0.5f : 1f);
        binding.cardPayCod.setAlpha(delivery ? 1f : 0.5f);
    }

    /** Hides the overlay when leaving. */
    @Override
    public void onDestroyView() {
        overlay.hide();
        super.onDestroyView();
    }
}
