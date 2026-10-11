package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.User;

/**
 * Registration, login, local password reset, logout and account deletion (FR-1.1–1.3, 1.9, 1.10).
 * <p>Pattern role: Repository (abstraction consumed by ViewModels).</p>
 *
 * @since 1.0
 */
public interface AuthRepository {

    /**
     * Creates an account after re-checking uniqueness (BR-1) and signs it in.
     *
     * @param fullName the full name
     * @param email    the email, may be blank when a phone is given
     * @param phone    the mobile number, may be blank when an email is given
     * @param password the password; the caller should clear the array afterwards
     * @return the new user
     */
    LiveData<Resource<User>> register(String fullName, String email, String phone, char[] password);

    /**
     * Signs in with an email or mobile number.
     *
     * @param identifier the email or mobile number
     * @param password   the password
     * @param remember   whether to keep the session after restart
     * @return the signed-in user
     */
    LiveData<Resource<User>> login(String identifier, char[] password, boolean remember);

    /**
     * Resets a password after matching the registered email and phone pair (FR-1.9).
     *
     * @param email       the registered email
     * @param phone       the registered mobile number
     * @param newPassword the new password
     * @return true on success
     */
    LiveData<Resource<Boolean>> resetPassword(String email, String phone, char[] newPassword);

    /** Ends the session. */
    void logout();

    /**
     * Deletes the signed-in account, its cascaded rows and its files (FR-1.10).
     *
     * @return true on success
     */
    LiveData<Resource<Boolean>> deleteAccount();

    /** @return the signed-in user id, or -1 */
    long currentUserId();

    /** @return true when a user is signed in */
    boolean isLoggedIn();

    /** @return observable signed-in user id (-1 when signed out) */
    LiveData<Long> observeUserId();
}
