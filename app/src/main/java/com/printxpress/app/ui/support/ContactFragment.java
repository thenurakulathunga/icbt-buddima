package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.IntentUtils;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.databinding.FragmentContactBinding;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Contact support (S28, FR-5.3): one-tap call, email and WhatsApp, plus a validated ticket form
 * that can reference one of the user's orders. Guests can use the shortcuts but must sign in to
 * raise a ticket.
 *
 * @since 1.0
 */
public class ContactFragment extends BaseFragment<FragmentContactBinding> {

    /** Screen state. */
    private ContactViewModel viewModel;
    /** Field-to-input map. */
    private final FormBinder form = new FormBinder();
    /** Placeholder entry meaning "no specific order". */
    private Order noOrder;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ContactFragment() {
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
    protected FragmentContactBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentContactBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the form and loads the order picker.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ContactViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        noOrder = new Order();
        noOrder.setOrderNumber(getString(R.string.ticket_no_order));
        if (saved == null && viewModel.getRelatedOrderId() == 0) {
            viewModel.setRelatedOrder(requireArguments().getLong(Constants.ARG_ORDER_ID));
        }
        form.bind(ContactViewModel.FIELD_SUBJECT, binding.inputSubject, viewModel::setValue, viewModel::validateField)
                .bind(ContactViewModel.FIELD_MESSAGE, binding.inputMessage, viewModel::setValue, viewModel::validateField);
    }

    /** Shows the ticket form or the sign-in prompt, e.g. after returning from sign-in. */
    @Override
    public void onResume() {
        super.onResume();
        boolean signedIn = viewModel.isLoggedIn();
        binding.groupForm.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        binding.bottomBar.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        binding.btnSignIn.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        if (signedIn && viewModel.getOrderChoices().getValue() == null) {
            viewModel.loadOrders();
        }
    }

    /** Renders errors, the order picker and success. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFieldErrors(), e -> form.showErrors(e.getErrors(), e.isFocusFirst()));
        observe(viewModel.getLoading(), binding.bottomBar::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getOrderChoices(), orders -> {
            List<Order> choices = new ArrayList<>();
            choices.add(noOrder);
            choices.addAll(orders);
            binding.dropdownOrder.setItems(choices, Order::getOrderNumber);
            long related = viewModel.getRelatedOrderId();
            binding.dropdownOrder.<Order>selectWhere(o -> o.getOrderId() == related);
        });
        observe(viewModel.getSent(), e -> {
            if (e.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.ticket_sent);
                nav().navigateUp();
            }
        });
    }

    /** Wires the shortcuts, the picker and Send. */
    @Override
    protected void initListeners() {
        binding.btnCall.setOnClickListener(v -> launched(IntentUtils.dial(requireContext(), Constants.SUPPORT_PHONE)));
        binding.btnEmail.setOnClickListener(v -> launched(IntentUtils.email(requireContext(), Constants.SUPPORT_EMAIL,
                getString(R.string.contact_email_subject))));
        binding.btnWhatsApp.setOnClickListener(v -> launched(IntentUtils.whatsApp(requireContext(),
                Constants.SUPPORT_WHATSAPP, getString(R.string.contact_whatsapp_text))));
        binding.dropdownOrder.<Order>setOnItemSelected(o -> viewModel.setRelatedOrder(o.getOrderId()));
        binding.btnSignIn.setOnClickListener(v -> startActivity(AuthActivity.returning(requireContext())));
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            viewModel.submit();
        });
    }

    /**
     * Tells the user when no app could handle a shortcut.
     *
     * @param started whether an app was launched
     */
    private void launched(boolean started) {
        if (!started) {
            showMessage(R.string.err_no_app);
        }
    }
}
