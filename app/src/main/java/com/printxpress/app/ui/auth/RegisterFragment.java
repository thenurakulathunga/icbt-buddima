package com.printxpress.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.databinding.FragmentRegisterBinding;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

/**
 * Registration screen (S04). Inline validation on focus loss, full validation on submit.
 *
 * @since 1.0
 */
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> {

    /** The form state. */
    private RegisterViewModel viewModel;
    /** Field to input binding. */
    private final FormBinder form = new FormBinder();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public RegisterFragment() {
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
    protected FragmentRegisterBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the fields to the ViewModel.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(RegisterViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        form.bind(RegisterViewModel.FIELD_NAME, binding.inputName, viewModel::setValue, viewModel::validateField)
                .bind(RegisterViewModel.FIELD_EMAIL, binding.inputEmail, viewModel::setValue, viewModel::validateField)
                .bind(RegisterViewModel.FIELD_PHONE, binding.inputPhone, viewModel::setValue, viewModel::validateField)
                .bind(RegisterViewModel.FIELD_PASSWORD, binding.inputPassword, (key, text) -> {
                    viewModel.setValue(key, text);
                    binding.strengthMeter.update(text);
                }, viewModel::validateField)
                .bind(RegisterViewModel.FIELD_CONFIRM, binding.inputConfirm, viewModel::setValue, viewModel::validateField);
    }

    /** Observes errors, loading and success. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFieldErrors(), errors -> form.showErrors(errors.getErrors(), errors.isFocusFirst()));
        observe(viewModel.getTermsError(), show -> binding.txtTermsError.setVisibility(show ? View.VISIBLE : View.GONE));
        observe(viewModel.getLoading(), binding.btnRegister::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getRegistered(), event -> {
            if (event.getContentIfNotHandled() != null) {
                SnackbarHelper.success(binding.getRoot(), R.string.register_success);
                ((AuthActivity) requireActivity()).onAuthenticated();
            }
        });
    }

    /** Wires the buttons. */
    @Override
    protected void initListeners() {
        binding.checkTerms.setOnCheckedChangeListener((b, checked) -> {
            if (checked) {
                binding.txtTermsError.setVisibility(View.GONE);
            }
        });
        binding.btnRegister.setOnClickListener(v -> {
            KeyboardUtils.hide(v);
            viewModel.register(binding.checkTerms.isChecked());
        });
        binding.btnLogin.setOnClickListener(v -> nav().navigateUp());
    }
}
