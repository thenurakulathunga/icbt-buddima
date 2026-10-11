package com.printxpress.app.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.databinding.FragmentOrderSuccessBinding;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * Order success (S16, FR-2.13): confirms the order number (PX-YYMMDD-NNNN) and offers tracking or
 * more shopping. Back returns to Home rather than to the finished checkout.
 *
 * @since 1.0
 */
public class OrderSuccessFragment extends BaseFragment<FragmentOrderSuccessBinding> {

    /** Screen state. */
    private OrderSuccessViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public OrderSuccessFragment() {
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
    protected FragmentOrderSuccessBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOrderSuccessBinding.inflate(inflater, container, false);
    }

    /**
     * Loads the order and redirects Back to Home.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(OrderSuccessViewModel.class);
        viewModel.start(requireArguments().getLong(Constants.ARG_ORDER_ID));
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goHome();
                    }
                });
    }

    /** Renders the order. */
    @Override
    protected void initObservers() {
        observe(viewModel.getOrder(), r -> {
            if (r.isSuccess()) {
                Order o = r.getData();
                binding.txtOrderNumber.setText(o.getOrderNumber());
                int res = o.getFulfilmentType() == FulfilmentType.DELIVERY
                        ? R.string.order_schedule_delivery : R.string.order_schedule_pickup;
                binding.txtSchedule.setText(getString(res, DateTimeUtils.formatDisplay(o.getScheduledDate()),
                        o.getScheduledSlot()));
                binding.txtTotal.setText(CurrencyFormatter.format(o.getTotal()));
            }
        });
    }

    /** Wires the buttons. */
    @Override
    protected void initListeners() {
        binding.btnTrack.setOnClickListener(v -> ShellRoutes.orderDetailFromSuccess(nav(),
                requireArguments().getLong(Constants.ARG_ORDER_ID)));
        binding.btnContinue.setOnClickListener(v -> goHome());
    }

    /** Returns to Home, clearing the finished flow. */
    private void goHome() {
        if (!nav().popBackStack(R.id.homeFragment, false)) {
            nav().navigate(R.id.homeFragment);
        }
    }
}
