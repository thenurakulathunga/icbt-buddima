package com.printxpress.app.ui.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.RegexRule;
import com.printxpress.app.core.validation.rules.RequiredIfEmptyRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.UserRepository;

/**
 * Edit profile form (S31, FR-1.4): name, email, phone and photo, with the same rules as
 * registration and a repository uniqueness check (TC-14).
 *
 * @since 1.0
 */
public class EditProfileViewModel extends BaseFormViewModel {

    /** Field key: name. */
    public static final String FIELD_NAME = "name";
    /** Field key: email. */
    public static final String FIELD_EMAIL = "email";
    /** Field key: phone. */
    public static final String FIELD_PHONE = "phone";

    /** Session. */
    private final AuthRepository auth;
    /** Profile. */
    private final UserRepository users;
    /** Loaded user. */
    private final MutableLiveData<Resource<User>> user = new MutableLiveData<>();
    /** One-shot saved signal. */
    private final MutableLiveData<Event<Boolean>> saved = new MutableLiveData<>();
    /** Whether the user was loaded. */
    private boolean loaded;

    /**
     * Creates the ViewModel.
     *
     * @param auth  auth repository
     * @param users user repository
     */
    public EditProfileViewModel(AuthRepository auth, UserRepository users) {
        this.auth = auth;
        this.users = users;
        validator.field(FIELD_NAME, new RequiredRule(R.string.err_name_invalid),
                new LengthRule(3, 60, R.string.err_name_invalid), new RegexRule(Constants.REGEX_NAME, R.string.err_name_invalid));
        validator.field(FIELD_EMAIL, new RequiredIfEmptyRule(() -> value(FIELD_PHONE), R.string.err_contact_required),
                new EmailRule(R.string.err_email_invalid));
        validator.field(FIELD_PHONE, new RequiredIfEmptyRule(() -> value(FIELD_EMAIL), R.string.err_contact_required),
                new SriLankanPhoneRule(R.string.err_phone_invalid));
    }

    /** Loads the user once and pre-fills the fields. */
    public void start() {
        if (loaded) {
            return;
        }
        loaded = true;
        subscribe(users.getUser(auth.currentUserId()), r -> {
            if (r.isSuccess()) {
                User u = r.getData();
                setValue(FIELD_NAME, u.getFullName());
                setValue(FIELD_EMAIL, u.getEmail() == null ? "" : u.getEmail());
                setValue(FIELD_PHONE, u.getPhone() == null ? "" : u.getPhone());
            }
            user.setValue(r);
        });
    }

    /** @return the loaded user */
    public LiveData<Resource<User>> getUser() {
        return user;
    }

    /** @return fires once after saving */
    public LiveData<Event<Boolean>> getSaved() {
        return saved;
    }

    /** Validates and saves the profile. */
    public void save() {
        if (!validateAll()) {
            return;
        }
        subscribe(users.updateProfile(auth.currentUserId(), value(FIELD_NAME), value(FIELD_EMAIL), value(FIELD_PHONE)), r -> {
            if (r.isSuccess()) {
                saved.setValue(new Event<>(true));
            } else if (r.isError()) {
                int error = r.getMessageRes();
                if (error == R.string.err_email_taken) {
                    showFieldError(FIELD_EMAIL, error);
                } else if (error == R.string.err_phone_taken) {
                    showFieldError(FIELD_PHONE, error);
                } else {
                    postError(error);
                }
            }
        }, true);
    }

    /**
     * Replaces the profile photo.
     *
     * @param image the picked image
     */
    public void updatePhoto(Uri image) {
        subscribe(users.updateAvatar(auth.currentUserId(), image), r -> {
            if (r.isSuccess()) {
                user.setValue(r);
                postMessage(R.string.photo_updated);
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }
}
