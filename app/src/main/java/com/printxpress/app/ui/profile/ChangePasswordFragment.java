package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.databinding.FragmentChangePasswordBinding;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

/**
 * Change password screen (FR-1.5).
 *
 * @since 1.0
 */
public class ChangePasswordFragment extends BaseFragment<FragmentChangePasswordBinding> {

    /** Form state. */
    private ChangePasswordViewModel viewModel;
    /** Field binding. */
    private final FormBinder form = new FormBinder();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ChangePasswordFragment() {
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
    protected FragmentChangePasswordBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentChangePasswordBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the fields.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ChangePasswordViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        form.bind(ChangePasswordViewModel.FIELD_CURRENT, binding.inputCurrent, viewModel::setValue, viewModel::validateField)
                .bind(ChangePasswordViewModel.FIELD_NEW, binding.inputNew, (key, text) -> {
                    viewModel.setValue(key, text);
                    binding.strengthMeter.update(text);
                }, viewModel::validateField)
                .bind(ChangePasswordViewModel.FIELD_CONFIRM, binding.inputConfirm, viewModel::setValue,
                        viewModel::validateField);
    }

    /** Observes errors and success. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFieldErrors(), e -> form.showErrors(e.getErrors(), e.isFocusFirst()));
        observe(viewModel.getLoading(), binding.bottomBar::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getChanged(), event -> {
            if (event.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.password_changed);
                nav().navigateUp();
            }
        });
    }

    /** Wires Save. */
    @Override
    protected void initListeners() {
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            viewModel.submit();
        });
    }
}
