package com.printxpress.app.data.model;

import com.printxpress.app.data.model.enums.NotificationType;
import java.time.LocalDateTime;

/**
 * An entry in the in-app notification centre (table {@code notifications}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class AppNotification {

    /** The primary key. */
    private long notificationId;
    /** The recipient. */
    private long userId;
    /** The related order, may be null. */
    private Long orderId;
    /** The related promotion, may be null. */
    private Long promoId;
    /** The category. */
    private NotificationType type = NotificationType.SYSTEM;
    /** The title. */
    private String title;
    /** The message body. */
    private String message;
    /** The whether it has been read. */
    private boolean read;
    /** The creation time. */
    private LocalDateTime createdAt;

    /** Creates an empty AppNotification; fields are filled by setters or the DAO. */
    public AppNotification() {
    }

    /** @return the primary key */
    public long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the primary key.
     *
     * @param notificationId the new value
     */
    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    /** @return the recipient */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the recipient.
     *
     * @param userId the new value
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** @return the related order, may be null */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the related order, may be null.
     *
     * @param orderId the new value
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /** @return the related promotion, may be null */
    public Long getPromoId() {
        return promoId;
    }

    /**
     * Sets the related promotion, may be null.
     *
     * @param promoId the new value
     */
    public void setPromoId(Long promoId) {
        this.promoId = promoId;
    }

    /** @return the category */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the category.
     *
     * @param type the new value
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the message body */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message body.
     *
     * @param message the new value
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return the whether it has been read */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the whether it has been read.
     *
     * @param read the new value
     */
    public void setRead(boolean read) {
        this.read = read;
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
}
