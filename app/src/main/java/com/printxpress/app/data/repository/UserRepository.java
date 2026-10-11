package com.printxpress.app.data.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.User;

/**
 * Profile, password and notification preferences of the signed-in user (FR-1.4, 1.5, 4.6).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface UserRepository {

    /**
     * Loads a user.
     *
     * @param userId the user
     * @return the user
     */
    LiveData<Resource<User>> getUser(long userId);

    /**
     * Updates name, email and phone after re-checking uniqueness.
     *
     * @param userId   the user
     * @param fullName the new name
     * @param email    the new email, may be blank when a phone remains
     * @param phone    the new phone, may be blank when an email remains
     * @return the updated user
     */
    LiveData<Resource<User>> updateProfile(long userId, String fullName, String email, String phone);

    /**
     * Copies a picked image into private storage and sets it as the profile photo.
     *
     * @param userId the user
     * @param image  the picked image URI
     * @return the updated user
     */
    LiveData<Resource<User>> updateAvatar(long userId, Uri image);

    /**
     * Changes the password after verifying the current one.
     *
     * @param userId  the user
     * @param current the current password
     * @param next    the new password
     * @return true on success
     */
    LiveData<Resource<Boolean>> changePassword(long userId, char[] current, char[] next);

    /**
     * Saves notification preferences.
     *
     * @param userId the user
     * @param orders order notifications on or off
     * @param promos promotion notifications on or off
     * @return true on success
     */
    LiveData<Resource<Boolean>> updatePreferences(long userId, boolean orders, boolean promos);

    /**
     * Loads a user on the calling thread, for workers.
     *
     * @param userId the user
     * @return the user, or null
     */
    User findUserBlocking(long userId);
}
