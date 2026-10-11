package com.printxpress.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.MatchRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.RegexRule;
import com.printxpress.app.core.validation.rules.RequiredIfEmptyRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.repository.AuthRepository;

/**
 * Registration form (S04, FR-1.1) with the full validation matrix of docs/01 §8: name, email or
 * phone (at least one, BR-1), strong password (BR-2), matching confirmation and accepted terms.
 * Uniqueness is re-checked by the repository and shown on the offending field.
 *
 * @since 1.0
 */
public class RegisterViewModel extends BaseFormViewModel {

    /** Field key: full name. */
    public static final String FIELD_NAME = "name";
    /** Field key: email. */
    public static final String FIELD_EMAIL = "email";
    /** Field key: phone. */
    public static final String FIELD_PHONE = "phone";
    /** Field key: password. */
    public static final String FIELD_PASSWORD = "password";
    /** Field key: confirm password. */
    public static final String FIELD_CONFIRM = "confirm";

    /** Authentication. */
    private final AuthRepository auth;
    /** One-shot success signal. */
    private final MutableLiveData<Event<Boolean>> registered = new MutableLiveData<>();
    /** Terms checkbox error. */
    private final MutableLiveData<Boolean> termsError = new MutableLiveData<>(false);

    /**
     * Creates the ViewModel and its rules.
     *
     * @param auth the auth repository
     */
    public RegisterViewModel(AuthRepository auth) {
        this.auth = auth;
        validator.field(FIELD_NAME, new RequiredRule(R.string.err_name_invalid),
                new LengthRule(3, 60, R.string.err_name_invalid), new RegexRule(Constants.REGEX_NAME, R.string.err_name_invalid));
        validator.field(FIELD_EMAIL, new RequiredIfEmptyRule(() -> value(FIELD_PHONE), R.string.err_contact_required),
                new EmailRule(R.string.err_email_invalid));
        validator.field(FIELD_PHONE, new RequiredIfEmptyRule(() -> value(FIELD_EMAIL), R.string.err_contact_required),
                new SriLankanPhoneRule(R.string.err_phone_invalid));
        validator.field(FIELD_PASSWORD, new RequiredRule(R.string.err_password_required),
                new PasswordStrengthRule(R.string.err_password_weak));
        validator.field(FIELD_CONFIRM, new RequiredRule(R.string.err_password_mismatch),
                new MatchRule(() -> value(FIELD_PASSWORD), R.string.err_password_mismatch));
    }

    /** @return fires once after the account is created */
    public LiveData<Event<Boolean>> getRegistered() {
        return registered;
    }

    /** @return whether the terms error is showing */
    public LiveData<Boolean> getTermsError() {
        return termsError;
    }

    /**
     * Validates everything and creates the account.
     *
     * @param termsAccepted whether the terms checkbox is ticked
     */
    public void register(boolean termsAccepted) {
        boolean valid = validateAll();
        termsError.setValue(!termsAccepted);
        if (!valid || !termsAccepted) {
            return;
        }
        subscribe(auth.register(value(FIELD_NAME), value(FIELD_EMAIL), value(FIELD_PHONE),
                value(FIELD_PASSWORD).toCharArray()), resource -> {
            if (resource.isSuccess()) {
                registered.setValue(new Event<>(true));
            } else if (resource.isError()) {
                int error = resource.getMessageRes();
                if (error == R.string.err_email_taken || error == R.string.err_email_invalid) {
                    showFieldError(FIELD_EMAIL, error);
                } else if (error == R.string.err_phone_taken || error == R.string.err_phone_invalid) {
                    showFieldError(FIELD_PHONE, error);
                } else {
                    postError(error);
                }
            }
        }, true);
    }
}
