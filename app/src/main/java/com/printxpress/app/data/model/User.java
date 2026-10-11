package com.printxpress.app.data.model;

import java.time.LocalDateTime;

/**
 * A registered customer (table {@code users}). Passwords are stored only as a PBKDF2 hash and salt.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class User {

    /** The primary key. */
    private long userId;
    /** The full name. */
    private String fullName;
    /** The email address, may be null when a phone is given. */
    private String email;
    /** The canonical mobile number (07XXXXXXXX), may be null when an email is given. */
    private String phone;
    /** The PBKDF2 hash as hex. */
    private String passwordHash;
    /** The per-user salt as hex. */
    private String passwordSalt;
    /** The app-private path of the profile photo, may be null. */
    private String profileImagePath;
    /** The whether order notifications are enabled. */
    private boolean notifyOrders = true;
    /** The whether promotion notifications are enabled. */
    private boolean notifyPromos = true;
    /** The creation time. */
    private LocalDateTime createdAt;
    /** The last update time. */
    private LocalDateTime updatedAt;

    /** Creates an empty User; fields are filled by setters or the DAO. */
    public User() {
    }

    /** @return the primary key */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the primary key.
     *
     * @param userId the new value
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** @return the full name */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName the new value
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return the email address, may be null when a phone is given */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address, may be null when a phone is given.
     *
     * @param email the new value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the canonical mobile number (07XXXXXXXX), may be null when an email is given */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the canonical mobile number (07XXXXXXXX), may be null when an email is given.
     *
     * @param phone the new value
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return the PBKDF2 hash as hex */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the PBKDF2 hash as hex.
     *
     * @param passwordHash the new value
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return the per-user salt as hex */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * Sets the per-user salt as hex.
     *
     * @param passwordSalt the new value
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /** @return the app-private path of the profile photo, may be null */
    public String getProfileImagePath() {
        return profileImagePath;
    }

    /**
     * Sets the app-private path of the profile photo, may be null.
     *
     * @param profileImagePath the new value
     */
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    /** @return the whether order notifications are enabled */
    public boolean isNotifyOrders() {
        return notifyOrders;
    }

    /**
     * Sets the whether order notifications are enabled.
     *
     * @param notifyOrders the new value
     */
    public void setNotifyOrders(boolean notifyOrders) {
        this.notifyOrders = notifyOrders;
    }

    /** @return the whether promotion notifications are enabled */
    public boolean isNotifyPromos() {
        return notifyPromos;
    }

    /**
     * Sets the whether promotion notifications are enabled.
     *
     * @param notifyPromos the new value
     */
    public void setNotifyPromos(boolean notifyPromos) {
        this.notifyPromos = notifyPromos;
    }

    /** @return the creation time */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation time.
     *
     * @param createdAt the new value
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** @return the last update time */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update time.
     *
     * @param updatedAt the new value
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
