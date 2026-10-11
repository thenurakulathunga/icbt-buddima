package com.printxpress.app.ui.auth;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.databinding.FragmentLoginBinding;
import com.printxpress.app.ui.components.FormBinder;

/**
 * Login screen (S03). Renders the form and forwards input to {@link LoginViewModel}.
 *
 * @since 1.0
 */
public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    /** The form state. */
    private LoginViewModel viewModel;
    /** Field to input binding. */
    private final FormBinder form = new FormBinder();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public LoginFragment() {
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
    protected FragmentLoginBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    /**
     * Configures the toggle and binds fields.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(LoginViewModel.class);
        binding.toggleMode.setOptions(R.string.login_tab_email, R.string.login_tab_phone);
        binding.toggleMode.setSelectedIndex(viewModel.isPhoneMode() ? 1 : 0);
        applyMode(viewModel.isPhoneMode());
        form.bind(LoginViewModel.FIELD_IDENTIFIER, binding.inputIdentifier, viewModel::setValue, viewModel::validateField)
                .bind(LoginViewModel.FIELD_PASSWORD, binding.inputPassword,
                        (key, text) -> viewModel.setValue(key, text.isEmpty() ? "" : "*"), null);
    }

    /** Observes errors, loading and success. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFieldErrors(), errors -> form.showErrors(errors.getErrors(), errors.isFocusFirst()));
        observe(viewModel.getLoading(), binding.btnLogin::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getSignedIn(), event -> {
            if (event.getContentIfNotHandled() != null) {
                ((AuthActivity) requireActivity()).onAuthenticated();
            }
        });
    }

    /** Wires buttons and the mode toggle. */
    @Override
    protected void initListeners() {
        binding.toggleMode.setOnSelectionChanged(index -> {
            viewModel.setPhoneMode(index == 1);
            applyMode(index == 1);
            binding.inputIdentifier.setText("");
            binding.inputIdentifier.clearError();
        });
        binding.btnLogin.setOnClickListener(v -> {
            KeyboardUtils.hide(v);
            viewModel.login(binding.inputPassword.getChars(), binding.checkRemember.isChecked());
        });
        binding.btnForgot.setOnClickListener(v -> navigate(R.id.action_login_to_reset, null));
        binding.btnRegister.setOnClickListener(v -> navigate(R.id.action_login_to_register, null));
        binding.btnGuest.setOnClickListener(v -> ((AuthActivity) requireActivity()).continueAsGuest());
    }

    /**
     * Updates the identifier field for email or mobile.
     *
     * @param phone true for mobile
     */
    private void applyMode(boolean phone) {
        binding.inputIdentifier.setLabel(getString(phone ? R.string.label_phone : R.string.label_email));
        binding.inputIdentifier.getEditText().setHint(phone ? R.string.hint_phone : R.string.hint_email);
        binding.inputIdentifier.getEditText().setInputType(phone ? InputType.TYPE_CLASS_PHONE
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }
}
