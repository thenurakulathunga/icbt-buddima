package com.printxpress.app.data.model;

import com.printxpress.app.data.model.enums.TicketStatus;
import java.time.LocalDateTime;

/**
 * A support request raised by a customer (table {@code support_tickets}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class SupportTicket {

    /** The primary key. */
    private long ticketId;
    /** The owner. */
    private long userId;
    /** The related order, may be null. */
    private Long orderId;
    /** The subject. */
    private String subject;
    /** The message. */
    private String message;
    /** The attachment path, may be null. */
    private String attachmentPath;
    /** The status. */
    private TicketStatus status = TicketStatus.OPEN;
    /** The creation time. */
    private LocalDateTime createdAt;
    /** The related order number (joined, read-only). */
    private String orderNumber;

    /** Creates an empty SupportTicket; fields are filled by setters or the DAO. */
    public SupportTicket() {
    }

    /** @return the primary key */
    public long getTicketId() {
        return ticketId;
    }

    /**
     * Sets the primary key.
     *
     * @param ticketId the new value
     */
    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    /** @return the owner */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the owner.
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

    /** @return the subject */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the new value
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new value
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return the attachment path, may be null */
    public String getAttachmentPath() {
        return attachmentPath;
    }

    /**
     * Sets the attachment path, may be null.
     *
     * @param attachmentPath the new value
     */
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    /** @return the status */
    public TicketStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new value
     */
    public void setStatus(TicketStatus status) {
        this.status = status;
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

    /** @return the related order number (joined, read-only) */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the related order number (joined, read-only).
     *
     * @param orderNumber the new value
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
