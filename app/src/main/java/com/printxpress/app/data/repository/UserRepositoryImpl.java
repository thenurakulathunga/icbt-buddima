package com.printxpress.app.data.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.security.PasswordHasher;
import com.printxpress.app.core.storage.FileStorageManager;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.dao.UserDao;
import com.printxpress.app.data.model.User;

/**
 * SQLite-backed {@link UserRepository}.
 *
 * @since 1.0
 */
public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    /** User table access. */
    private final UserDao userDao;
    /** Password hashing. */
    private final PasswordHasher hasher;
    /** Avatar storage. */
    private final FileStorageManager files;

    /**
     * Creates the repository.
     *
     * @param executors background executors
     * @param userDao   user DAO
     * @param hasher    password hasher
     * @param files     file storage
     */
    public UserRepositoryImpl(AppExecutors executors, UserDao userDao, PasswordHasher hasher, FileStorageManager files) {
        super(executors);
        this.userDao = userDao;
        this.hasher = hasher;
        this.files = files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<User>> getUser(long userId) {
        return execute(() -> existing(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<User>> updateProfile(long userId, String fullName, String email, String phone) {
        return execute(() -> {
            User user = existing(userId);
            String cleanEmail = AuthRepositoryImpl.normaliseEmail(email);
            String cleanPhone = ValidationRule.isBlank(phone) ? null : SriLankanPhoneRule.normalize(phone);
            require(cleanEmail != null || cleanPhone != null, R.string.err_contact_required);
            require(cleanEmail == null || new EmailRule(0).validate(cleanEmail).isValid(), R.string.err_email_invalid);
            require(cleanPhone == null || new SriLankanPhoneRule(0).validate(cleanPhone).isValid(),
                    R.string.err_phone_invalid);
            require(cleanEmail == null || !userDao.isEmailTaken(cleanEmail, userId), R.string.err_email_taken);
            require(cleanPhone == null || !userDao.isPhoneTaken(cleanPhone, userId), R.string.err_phone_taken);
            user.setFullName(fullName.trim());
            user.setEmail(cleanEmail);
            user.setPhone(cleanPhone);
            userDao.update(user);
            return user;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<User>> updateAvatar(long userId, Uri image) {
        return execute(() -> {
            User user = existing(userId);
            String oldPath = user.getProfileImagePath();
            user.setProfileImagePath(files.copyAvatar(image));
            userDao.update(user);
            files.delete(oldPath);
            return user;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> changePassword(long userId, char[] current, char[] next) {
        return execute(() -> {
            User user = existing(userId);
            require(hasher.verify(current, user.getPasswordSalt(), user.getPasswordHash()),
                    R.string.err_password_current);
            require(new PasswordStrengthRule(0).validate(new String(next)).isValid(), R.string.err_password_weak);
            require(!hasher.verify(next, user.getPasswordSalt(), user.getPasswordHash()), R.string.err_password_same);
            String salt = hasher.generateSalt();
            return userDao.updatePassword(userId, hasher.hash(next, salt), salt);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> updatePreferences(long userId, boolean orders, boolean promos) {
        return execute(() -> userDao.updatePreferences(userId, orders, promos));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserBlocking(long userId) {
        return userDao.findById(userId);
    }

    /**
     * Loads a user or fails with "not signed in".
     *
     * @param userId the user
     * @return the user
     */
    private User existing(long userId) {
        User user = userDao.findById(userId);
        require(user != null, R.string.err_not_signed_in);
        return user;
    }
}
