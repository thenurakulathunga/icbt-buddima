package com.printxpress.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.repository.AuthRepository;

import java.util.Arrays;

/**
 * Login form (S03, FR-1.2, FR-1.3): email or mobile toggle, password and remember me.
 * The identifier rule follows the selected mode.
 *
 * @since 1.0
 */
public class LoginViewModel extends BaseFormViewModel {

    /** Field key: email or phone. */
    public static final String FIELD_IDENTIFIER = "identifier";
    /** Field key: password. */
    public static final String FIELD_PASSWORD = "password";

    /** Authentication. */
    private final AuthRepository auth;
    /** One-shot success signal. */
    private final MutableLiveData<Event<Boolean>> signedIn = new MutableLiveData<>();
    /** Whether the phone mode is selected. */
    private boolean phoneMode;

    /**
     * Creates the ViewModel.
     *
     * @param auth the auth repository
     */
    public LoginViewModel(AuthRepository auth) {
        this.auth = auth;
        EmailRule email = new EmailRule(R.string.err_email_invalid);
        SriLankanPhoneRule phone = new SriLankanPhoneRule(R.string.err_phone_invalid);
        validator.field(FIELD_IDENTIFIER, new RequiredRule(R.string.err_identifier_required),
                value -> phoneMode ? phone.validate(value) : email.validate(value));
        validator.field(FIELD_PASSWORD, new RequiredRule(R.string.err_password_required));
    }

    /** @return fires once after a successful sign-in */
    public LiveData<Event<Boolean>> getSignedIn() {
        return signedIn;
    }

    /**
     * Switches between email and mobile sign-in.
     *
     * @param phone true for mobile
     */
    public void setPhoneMode(boolean phone) {
        phoneMode = phone;
    }

    /** @return true when mobile mode is selected */
    public boolean isPhoneMode() {
        return phoneMode;
    }

    /**
     * Validates and signs in.
     *
     * @param password the password characters, cleared afterwards
     * @param remember keep the session after restart
     */
    public void login(char[] password, boolean remember) {
        setValue(FIELD_PASSWORD, password.length == 0 ? "" : "*");
        if (!validateAll()) {
            Arrays.fill(password, '\0');
            return;
        }
        subscribe(auth.login(value(FIELD_IDENTIFIER), password, remember), resource -> {
            if (resource.isSuccess()) {
                Arrays.fill(password, '\0');
                signedIn.setValue(new Event<>(true));
            } else if (resource.isError()) {
                Arrays.fill(password, '\0');
                postError(resource.getMessageRes());
            }
        }, true);
    }
}
