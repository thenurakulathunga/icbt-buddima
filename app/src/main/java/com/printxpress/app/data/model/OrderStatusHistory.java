package com.printxpress.app.data.model;

import com.printxpress.app.data.model.enums.OrderStatus;
import java.time.LocalDateTime;

/**
 * One entry in an order status audit trail (table {@code order_status_history}), shown on the timeline.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class OrderStatusHistory {

    /** The primary key. */
    private long historyId;
    /** The order foreign key. */
    private long orderId;
    /** The status entered. */
    private OrderStatus status;
    /** The optional remark. */
    private String remark;
    /** The time of the change. */
    private LocalDateTime changedAt;

    /** Creates an empty OrderStatusHistory; fields are filled by setters or the DAO. */
    public OrderStatusHistory() {
    }

    /** @return the primary key */
    public long getHistoryId() {
        return historyId;
    }

    /**
     * Sets the primary key.
     *
     * @param historyId the new value
     */
    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    /** @return the order foreign key */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order foreign key.
     *
     * @param orderId the new value
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /** @return the status entered */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the status entered.
     *
     * @param status the new value
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /** @return the optional remark */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the optional remark.
     *
     * @param remark the new value
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /** @return the time of the change */
    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    /**
     * Sets the time of the change.
     *
     * @param changedAt the new value
     */
    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
