package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.security.PasswordHasher;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.core.storage.FileStorageManager;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.dao.UserDao;
import com.printxpress.app.data.model.User;

import java.util.Locale;

/**
 * SQLite-backed {@link AuthRepository}.
 * <p>
 * Re-validates every rule on the background thread even though the ViewModel already did, so a
 * bypassed UI can never store an invalid or duplicate account. Passwords are hashed with PBKDF2
 * and a fresh salt; the plain password is never stored or logged.
 * </p>
 *
 * @since 1.0
 */
public class AuthRepositoryImpl extends BaseRepository implements AuthRepository {

    /** User table access. */
    private final UserDao userDao;
    /** Password hashing. */
    private final PasswordHasher hasher;
    /** Session storage. */
    private final SessionManager session;
    /** File cleanup on account deletion. */
    private final FileStorageManager files;

    /**
     * Creates the repository.
     *
     * @param executors background executors
     * @param userDao   user DAO
     * @param hasher    password hasher
     * @param session   session manager
     * @param files     file storage
     */
    public AuthRepositoryImpl(AppExecutors executors, UserDao userDao, PasswordHasher hasher,
                              SessionManager session, FileStorageManager files) {
        super(executors);
        this.userDao = userDao;
        this.hasher = hasher;
        this.session = session;
        this.files = files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<User>> register(String fullName, String email, String phone, char[] password) {
        return execute(() -> {
            String cleanEmail = normaliseEmail(email);
            String cleanPhone = ValidationRule.isBlank(phone) ? null : SriLankanPhoneRule.normalize(phone);
            require(cleanEmail != null || cleanPhone != null, R.string.err_contact_required);
            require(cleanEmail == null || new EmailRule(0).validate(cleanEmail).isValid(), R.string.err_email_invalid);
            require(cleanPhone == null || new SriLankanPhoneRule(0).validate(cleanPhone).isValid(),
                    R.string.err_phone_invalid);
            require(new PasswordStrengthRule(0).validate(new String(password)).isValid(), R.string.err_password_weak);
            require(cleanEmail == null || !userDao.isEmailTaken(cleanEmail, -1), R.string.err_email_taken);
            require(cleanPhone == null || !userDao.isPhoneTaken(cleanPhone, -1), R.string.err_phone_taken);
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(cleanEmail);
            user.setPhone(cleanPhone);
            String salt = hasher.generateSalt();
            user.setPasswordSalt(salt);
            user.setPasswordHash(hasher.hash(password, salt));
            userDao.insert(user);
            session.login(user.getUserId(), true);
            return user;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<User>> login(String identifier, char[] password, boolean remember) {
        return execute(() -> {
            User user = userDao.findByIdentifier(normaliseIdentifier(identifier));
            boolean ok = user != null && hasher.verify(password, user.getPasswordSalt(), user.getPasswordHash());
            require(ok, R.string.err_invalid_credentials);
            session.login(user.getUserId(), remember);
            return user;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> resetPassword(String email, String phone, char[] newPassword) {
        return execute(() -> {
            User user = userDao.findByIdentifier(normaliseEmail(email));
            String cleanPhone = SriLankanPhoneRule.normalize(phone);
            require(user != null && cleanPhone != null && cleanPhone.equals(user.getPhone()),
                    R.string.err_reset_mismatch);
            require(new PasswordStrengthRule(0).validate(new String(newPassword)).isValid(), R.string.err_password_weak);
            String salt = hasher.generateSalt();
            return userDao.updatePassword(user.getUserId(), hasher.hash(newPassword, salt), salt);
        });
    }

    /** Ends the session. */
    @Override
    public void logout() {
        session.logout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> deleteAccount() {
        return execute(() -> {
            long userId = session.getUserId();
            require(userId != SessionManager.NO_USER, R.string.err_not_signed_in);
            boolean deleted = userDao.deleteById(userId);
            files.deleteAll();
            session.clearUserData();
            return deleted;
        });
    }

    /** @return the signed-in user id, or -1 */
    @Override
    public long currentUserId() {
        return session.getUserId();
    }

    /** @return true when signed in */
    @Override
    public boolean isLoggedIn() {
        return session.isLoggedIn();
    }

    /** @return observable signed-in user id */
    @Override
    public LiveData<Long> observeUserId() {
        return session.observeUserId();
    }

    /**
     * Trims and lower-cases an email; blank becomes null.
     *
     * @param email the raw email
     * @return the normalised email or null
     */
    static String normaliseEmail(String email) {
        return ValidationRule.isBlank(email) ? null : email.trim().toLowerCase(Locale.US);
    }

    /**
     * Normalises a login identifier: emails are lower-cased, phones converted to 07XXXXXXXX.
     *
     * @param identifier the raw identifier
     * @return the lookup key
     */
    static String normaliseIdentifier(String identifier) {
        if (identifier == null) {
            return "";
        }
        String trimmed = identifier.trim();
        return trimmed.contains("@") ? trimmed.toLowerCase(Locale.US) : SriLankanPhoneRule.normalize(trimmed);
    }
}
