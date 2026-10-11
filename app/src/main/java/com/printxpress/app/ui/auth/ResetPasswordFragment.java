package com.printxpress.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.databinding.FragmentResetPasswordBinding;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

/**
 * Reset password screen (S05).
 *
 * @since 1.0
 */
public class ResetPasswordFragment extends BaseFragment<FragmentResetPasswordBinding> {

    /** The form state. */
    private ResetPasswordViewModel viewModel;
    /** Field to input binding. */
    private final FormBinder form = new FormBinder();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ResetPasswordFragment() {
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
    protected FragmentResetPasswordBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentResetPasswordBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the fields.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ResetPasswordViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        form.bind(ResetPasswordViewModel.FIELD_EMAIL, binding.inputEmail, viewModel::setValue, viewModel::validateField)
                .bind(ResetPasswordViewModel.FIELD_PHONE, binding.inputPhone, viewModel::setValue, viewModel::validateField)
                .bind(ResetPasswordViewModel.FIELD_PASSWORD, binding.inputPassword, (key, text) -> {
                    viewModel.setValue(key, text);
                    binding.strengthMeter.update(text);
                }, viewModel::validateField)
                .bind(ResetPasswordViewModel.FIELD_CONFIRM, binding.inputConfirm, viewModel::setValue,
                        viewModel::validateField);
    }

    /** Observes errors, loading and success. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFieldErrors(), errors -> form.showErrors(errors.getErrors(), errors.isFocusFirst()));
        observe(viewModel.getLoading(), binding.btnReset::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getDone(), event -> {
            if (event.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.reset_success);
                nav().navigateUp();
            }
        });
    }

    /** Wires the submit button. */
    @Override
    protected void initListeners() {
        binding.btnReset.setOnClickListener(v -> {
            KeyboardUtils.hide(v);
            viewModel.reset();
        });
    }
}
