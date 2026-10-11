package com.printxpress.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.OrderItem;
import com.printxpress.app.data.model.OrderItemOption;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.data.model.enums.PaymentMethod;
import com.printxpress.app.databinding.FragmentOrderDetailBinding;
import com.printxpress.app.domain.order.OrderState;
import com.printxpress.app.domain.pricing.PriceBreakdown;
import com.printxpress.app.ui.components.InfoBanner;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;

import java.util.ArrayList;
import java.util.List;

/**
 * Order detail and tracking (S18, FR-3.2, FR-3.6). Refreshes whenever a new notification arrives,
 * i.e. whenever the simulated shop moves the order on.
 *
 * @since 1.0
 */
public class OrderDetailFragment extends BaseFragment<FragmentOrderDetailBinding> {

    /** Screen state, shared with the sheets. */
    private OrderDetailViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public OrderDetailFragment() {
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
    protected FragmentOrderDetailBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOrderDetailBinding.inflate(inflater, container, false);
    }

    /**
     * Loads the order.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(OrderDetailViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        viewModel.start(requireArguments().getLong(Constants.ARG_ORDER_ID));
    }

    /** Renders the order and reacts to results. */
    @Override
    protected void initObservers() {
        observe(viewModel.getOrder(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else {
                render(r.getData());
                binding.stateLayout.showContent();
            }
        });
        observe(activityViewModel(MainViewModel.class).getUnreadCount(), count -> viewModel.refresh());
        observeMessages(viewModel);
        observe(viewModel.getDone(), e -> {
            if (Integer.valueOf(R.string.action_reorder).equals(e.peekContent()) && e.getContentIfNotHandled() != null) {
                navigate(R.id.cartFragment, null);
            }
        });
    }

    /** Wires the actions. */
    @Override
    protected void initListeners() {
        binding.btnReschedule.setOnClickListener(v -> new RescheduleSheet().show(getChildFragmentManager()));
        binding.btnCancel.setOnClickListener(v -> new CancelSheet().show(getChildFragmentManager()));
        binding.btnReorder.setOnClickListener(v -> viewModel.reorder());
        binding.btnHelp.setOnClickListener(v -> ShellRoutes.contact(nav(),
                requireArguments().getLong(Constants.ARG_ORDER_ID)));
    }

    /**
     * Renders every section.
     *
     * @param o the order
     */
    private void render(Order o) {
        binding.topBar.setTitle(o.getOrderNumber());
        binding.txtNumber.setText(o.getOrderNumber());
        binding.txtPlaced.setText(getString(R.string.order_placed, DateTimeUtils.formatDisplay(o.getCreatedAt())));
        binding.badgeStatus.setStatus(o.getStatus());
        binding.timeline.bind(o.getHistory(), o.getStatus(), o.getFulfilmentType());
        renderBanner(o);
        renderItems(o);
        renderFulfilment(o);
        binding.priceSummary.bind(PriceBreakdown.ofOrder(o.getSubtotal(), o.getDiscount(), o.getDeliveryFee(),
                o.getPromoCode(), o.getFulfilmentType() == FulfilmentType.DELIVERY));
        OrderState state = viewModel.state();
        boolean finished = o.getStatus().isFinished();
        binding.actionsEditable.setVisibility(finished ? View.GONE : View.VISIBLE);
        binding.btnReschedule.setEnabled(state.canReschedule());
        binding.btnCancel.setEnabled(state.canCancel());
        binding.txtLocked.setVisibility(!finished && !state.canCancel() ? View.VISIBLE : View.GONE);
        binding.btnReorder.setVisibility(finished ? View.VISIBLE : View.GONE);
    }

    /**
     * Shows the banner that explains the current state.
     *
     * @param o the order
     */
    private void renderBanner(Order o) {
        InfoBanner banner = binding.banner;
        banner.setVisibility(View.VISIBLE);
        OrderStatus s = o.getStatus();
        if (s == OrderStatus.CANCELLED) {
            banner.setType(InfoBanner.Type.ERROR);
            banner.setMessage(getString(R.string.banner_cancelled, o.getCancelReason() == null ? "" : o.getCancelReason()));
        } else if (s == OrderStatus.COMPLETED) {
            banner.setType(InfoBanner.Type.SUCCESS);
            banner.setMessage(R.string.banner_completed);
        } else if (s == OrderStatus.READY_FOR_PICKUP) {
            banner.setType(InfoBanner.Type.SUCCESS);
            banner.setMessage(getString(R.string.banner_ready, o.getBranchName()));
        } else if (!viewModel.state().canCancel()) {
            banner.setType(InfoBanner.Type.WARNING);
            banner.setMessage(R.string.banner_printing);
        } else {
            banner.setVisibility(View.GONE);
        }
    }

    /**
     * Lists the order lines with their options.
     *
     * @param o the order
     */
    private void renderItems(Order o) {
        binding.tableItems.clear();
        for (OrderItem item : o.getItems()) {
            binding.tableItems.addRow(getString(R.string.item_line, item.getProductName(), item.getQuantity()),
                    CurrencyFormatter.format(item.getLineTotal()));
            List<String> specs = new ArrayList<>();
            for (OrderItemOption option : item.getOptions()) {
                specs.add(option.getValue());
            }
            String artwork = item.getDesignTitle() != null ? getString(R.string.design_file, item.getDesignTitle())
                    : getString(R.string.design_custom_text);
            specs.add(artwork);
            binding.tableItems.addRow(String.join(getString(R.string.specs_separator), specs));
        }
    }

    /**
     * Lists where, when, payment and notes.
     *
     * @param o the order
     */
    private void renderFulfilment(Order o) {
        boolean delivery = o.getFulfilmentType() == FulfilmentType.DELIVERY;
        binding.tableFulfilment.clear();
        binding.tableFulfilment.addRow(getString(R.string.review_method),
                getString(delivery ? R.string.fulfilment_delivery : R.string.fulfilment_pickup));
        binding.tableFulfilment.addRow(getString(R.string.review_where),
                delivery ? nullToEmpty(o.getAddressSummary()) : nullToEmpty(o.getBranchName()));
        binding.tableFulfilment.addRow(getString(R.string.review_when),
                DateTimeUtils.formatDisplay(o.getScheduledDate()) + "\n" + o.getScheduledSlot());
        binding.tableFulfilment.addRow(getString(R.string.order_payment_method),
                getString(o.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY ? R.string.payment_cod : R.string.payment_pickup));
        if (o.getNotes() != null) {
            binding.tableFulfilment.addRow(getString(R.string.order_notes), o.getNotes());
        }
    }

    /**
     * Converts null to an empty string.
     *
     * @param text the text
     * @return the text or ""
     */
    private static String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}
