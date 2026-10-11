package com.printxpress.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.validation.rules.MatchRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.UserRepository;

/**
 * Change password form (S31, FR-1.5, TC-18): current password is verified by the repository,
 * the new one must meet BR-2 and be confirmed.
 *
 * @since 1.0
 */
public class ChangePasswordViewModel extends BaseFormViewModel {

    /** Field key: current password. */
    public static final String FIELD_CURRENT = "current";
    /** Field key: new password. */
    public static final String FIELD_NEW = "new";
    /** Field key: confirmation. */
    public static final String FIELD_CONFIRM = "confirm";

    /** Session. */
    private final AuthRepository auth;
    /** Profile. */
    private final UserRepository users;
    /** One-shot success. */
    private final MutableLiveData<Event<Boolean>> changed = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth  auth repository
     * @param users user repository
     */
    public ChangePasswordViewModel(AuthRepository auth, UserRepository users) {
        this.auth = auth;
        this.users = users;
        validator.field(FIELD_CURRENT, new RequiredRule(R.string.err_password_required));
        validator.field(FIELD_NEW, new RequiredRule(R.string.err_password_required),
                new PasswordStrengthRule(R.string.err_password_weak));
        validator.field(FIELD_CONFIRM, new RequiredRule(R.string.err_password_mismatch),
                new MatchRule(() -> value(FIELD_NEW), R.string.err_password_mismatch));
    }

    /** @return fires once after the password changed */
    public LiveData<Event<Boolean>> getChanged() {
        return changed;
    }

    /** Validates and changes the password. */
    public void submit() {
        if (!validateAll()) {
            return;
        }
        subscribe(users.changePassword(auth.currentUserId(), value(FIELD_CURRENT).toCharArray(),
                value(FIELD_NEW).toCharArray()), r -> {
            if (r.isSuccess()) {
                changed.setValue(new Event<>(true));
            } else if (r.isError()) {
                int error = r.getMessageRes();
                if (error == R.string.err_password_current) {
                    showFieldError(FIELD_CURRENT, error);
                } else if (error == R.string.err_password_same || error == R.string.err_password_weak) {
                    showFieldError(FIELD_NEW, error);
                } else {
                    postError(error);
                }
            }
        }, true);
    }
}
