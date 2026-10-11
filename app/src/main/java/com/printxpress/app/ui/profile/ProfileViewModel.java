package com.printxpress.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.UserRepository;

/**
 * Profile tab state (S30, FR-1.4): the signed-in user, or the guest state, and logging out.
 *
 * @since 1.0
 */
public class ProfileViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Profile. */
    private final UserRepository users;
    /** The signed-in user, null data for guests. */
    private final MutableLiveData<Resource<User>> user = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth  auth repository
     * @param users user repository
     */
    public ProfileViewModel(AuthRepository auth, UserRepository users) {
        this.auth = auth;
        this.users = users;
    }

    /** Reloads the user. */
    public void refresh() {
        long id = auth.currentUserId();
        if (id > 0) {
            load(users.getUser(id), user);
        } else {
            user.setValue(Resource.success(null));
        }
    }

    /** @return the user (null data when a guest) */
    public LiveData<Resource<User>> getUser() {
        return user;
    }

    /** Ends the session. */
    public void logout() {
        auth.logout();
        user.setValue(Resource.success(null));
    }
}
