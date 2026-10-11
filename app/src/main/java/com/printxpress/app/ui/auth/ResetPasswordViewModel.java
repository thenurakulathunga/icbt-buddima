package com.printxpress.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.MatchRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.repository.AuthRepository;

/**
 * Local password reset (S05, FR-1.9): the registered email and mobile number must match the same
 * account on this device before a new, strong password is accepted (no OTP; docs/01 assumption 3).
 *
 * @since 1.0
 */
public class ResetPasswordViewModel extends BaseFormViewModel {

    /** Field key: email. */
    public static final String FIELD_EMAIL = "email";
    /** Field key: phone. */
    public static final String FIELD_PHONE = "phone";
    /** Field key: new password. */
    public static final String FIELD_PASSWORD = "password";
    /** Field key: confirmation. */
    public static final String FIELD_CONFIRM = "confirm";

    /** Authentication. */
    private final AuthRepository auth;
    /** One-shot success signal. */
    private final MutableLiveData<Event<Boolean>> done = new MutableLiveData<>();

    /**
     * Creates the ViewModel and its rules.
     *
     * @param auth the auth repository
     */
    public ResetPasswordViewModel(AuthRepository auth) {
        this.auth = auth;
        validator.field(FIELD_EMAIL, new RequiredRule(R.string.err_email_invalid), new EmailRule(R.string.err_email_invalid));
        validator.field(FIELD_PHONE, new RequiredRule(R.string.err_phone_invalid),
                new SriLankanPhoneRule(R.string.err_phone_invalid));
        validator.field(FIELD_PASSWORD, new RequiredRule(R.string.err_password_required),
                new PasswordStrengthRule(R.string.err_password_weak));
        validator.field(FIELD_CONFIRM, new RequiredRule(R.string.err_password_mismatch),
                new MatchRule(() -> value(FIELD_PASSWORD), R.string.err_password_mismatch));
    }

    /** @return fires once after the password is reset */
    public LiveData<Event<Boolean>> getDone() {
        return done;
    }

    /** Validates and resets the password. */
    public void reset() {
        if (!validateAll()) {
            return;
        }
        subscribe(auth.resetPassword(value(FIELD_EMAIL), value(FIELD_PHONE), value(FIELD_PASSWORD).toCharArray()),
                resource -> {
                    if (resource.isSuccess()) {
                        done.setValue(new Event<>(true));
                    } else if (resource.isError()) {
                        postError(resource.getMessageRes());
                    }
                }, true);
    }
}
