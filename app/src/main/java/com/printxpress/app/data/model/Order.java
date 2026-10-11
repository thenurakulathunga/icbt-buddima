package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.data.model.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An order header with its lines and status history (tables {@code orders}, {@code order_items},
 * {@code order_status_history}).
 * <p>
 * Pattern role: Builder. Delivery orders need an address, pickup orders a branch, and a promo
 * is optional, so {@link Builder} assembles the valid combination. What the customer may do
 * with an order (cancel, reschedule) is decided by {@code OrderState}, not by this class.
 * </p>
 *
 * @since 1.0
 */
public class Order {

    /** The primary key. */
    private long orderId;
    /** The public order number, PX-YYMMDD-NNNN. */
    private String orderNumber;
    /** The owner. */
    private long userId;
    /** The delivery address, or null for pickup. */
    private Long addressId;
    /** The pickup branch, or null for delivery. */
    private Long branchId;
    /** The applied promotion, may be null. */
    private Long promoId;
    /** The current status. */
    private OrderStatus status = OrderStatus.PENDING;
    /** Pickup or delivery. */
    private FulfilmentType fulfilmentType = FulfilmentType.PICKUP;
    /** The payment method. */
    private PaymentMethod paymentMethod = PaymentMethod.PAY_AT_PICKUP;
    /** The scheduled pickup or delivery date. */
    private LocalDate scheduledDate;
    /** The scheduled time slot label. */
    private String scheduledSlot;
    /** The subtotal after bulk discounts. */
    private Money subtotal = Money.ZERO;
    /** The promo discount. */
    private Money discount = Money.ZERO;
    /** The delivery fee. */
    private Money deliveryFee = Money.ZERO;
    /** The amount payable. */
    private Money total = Money.ZERO;
    /** Optional customer notes. */
    private String notes;
    /** The cancellation reason, set when cancelled. */
    private String cancelReason;
    /** The creation time. */
    private LocalDateTime createdAt;
    /** The last update time. */
    private LocalDateTime updatedAt;
    /** The order lines. */
    private List<OrderItem> items = new ArrayList<>();
    /** The status history, oldest first. */
    private List<OrderStatusHistory> history = new ArrayList<>();
    /** The pickup branch name (joined, read-only). */
    private String branchName;
    /** The one-line delivery address (joined, read-only). */
    private String addressSummary;
    /** The applied promo code (joined, read-only). */
    private String promoCode;
    /** The number of lines (computed, read-only). */
    private int itemCount;
    /** The first line's product name (joined, read-only). */
    private String firstProductName;
    /** The first line's image name (joined, read-only). */
    private String firstProductImageRes;

    /** Creates an empty order; prefer {@link Builder}. */
    public Order() {
    }

    /** @return the primary key */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the primary key.
     *
     * @param orderId the new value
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /** @return the public order number */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new value
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    /** @return the delivery address id, may be null */
    public Long getAddressId() {
        return addressId;
    }

    /**
     * Sets the delivery address id.
     *
     * @param addressId the new value
     */
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /** @return the pickup branch id, may be null */
    public Long getBranchId() {
        return branchId;
    }

    /**
     * Sets the pickup branch id.
     *
     * @param branchId the new value
     */
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    /** @return the promotion id, may be null */
    public Long getPromoId() {
        return promoId;
    }

    /**
     * Sets the promotion id.
     *
     * @param promoId the new value
     */
    public void setPromoId(Long promoId) {
        this.promoId = promoId;
    }

    /** @return the current status */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new value
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /** @return pickup or delivery */
    public FulfilmentType getFulfilmentType() {
        return fulfilmentType;
    }

    /**
     * Sets the fulfilment type.
     *
     * @param fulfilmentType the new value
     */
    public void setFulfilmentType(FulfilmentType fulfilmentType) {
        this.fulfilmentType = fulfilmentType;
    }

    /** @return the payment method */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new value
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /** @return the scheduled date */
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Sets the scheduled date.
     *
     * @param scheduledDate the new value
     */
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    /** @return the scheduled slot label */
    public String getScheduledSlot() {
        return scheduledSlot;
    }

    /**
     * Sets the scheduled slot.
     *
     * @param scheduledSlot the new value
     */
    public void setScheduledSlot(String scheduledSlot) {
        this.scheduledSlot = scheduledSlot;
    }

    /** @return the subtotal after bulk discounts */
    public Money getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal.
     *
     * @param subtotal the new value
     */
    public void setSubtotal(Money subtotal) {
        this.subtotal = subtotal;
    }

    /** @return the promo discount */
    public Money getDiscount() {
        return discount;
    }

    /**
     * Sets the promo discount.
     *
     * @param discount the new value
     */
    public void setDiscount(Money discount) {
        this.discount = discount;
    }

    /** @return the delivery fee */
    public Money getDeliveryFee() {
        return deliveryFee;
    }

    /**
     * Sets the delivery fee.
     *
     * @param deliveryFee the new value
     */
    public void setDeliveryFee(Money deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    /** @return the amount payable */
    public Money getTotal() {
        return total;
    }

    /**
     * Sets the amount payable.
     *
     * @param total the new value
     */
    public void setTotal(Money total) {
        this.total = total;
    }

    /** @return the customer notes, may be null */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the customer notes.
     *
     * @param notes the new value
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /** @return the cancellation reason, may be null */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * Sets the cancellation reason.
     *
     * @param cancelReason the new value
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
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

    /** @return the order lines (never null) */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Sets the order lines.
     *
     * @param items the new value; null becomes an empty list
     */
    public void setItems(List<OrderItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    /** @return the status history, oldest first (never null) */
    public List<OrderStatusHistory> getHistory() {
        return history;
    }

    /**
     * Sets the status history.
     *
     * @param history the new value; null becomes an empty list
     */
    public void setHistory(List<OrderStatusHistory> history) {
        this.history = history == null ? new ArrayList<>() : history;
    }

    /** @return the pickup branch name, may be null */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Sets the pickup branch name.
     *
     * @param branchName the new value
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /** @return the one-line delivery address, may be null */
    public String getAddressSummary() {
        return addressSummary;
    }

    /**
     * Sets the one-line delivery address.
     *
     * @param addressSummary the new value
     */
    public void setAddressSummary(String addressSummary) {
        this.addressSummary = addressSummary;
    }

    /** @return the applied promo code, may be null */
    public String getPromoCode() {
        return promoCode;
    }

    /**
     * Sets the applied promo code.
     *
     * @param promoCode the new value
     */
    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    /** @return the number of lines */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Sets the number of lines.
     *
     * @param itemCount the new value
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /** @return the first line's product name, may be null */
    public String getFirstProductName() {
        return firstProductName;
    }

    /**
     * Sets the first line's product name.
     *
     * @param firstProductName the new value
     */
    public void setFirstProductName(String firstProductName) {
        this.firstProductName = firstProductName;
    }

    /** @return the first line's image name, may be null */
    public String getFirstProductImageRes() {
        return firstProductImageRes;
    }

    /**
     * Sets the first line's image name.
     *
     * @param firstProductImageRes the new value
     */
    public void setFirstProductImageRes(String firstProductImageRes) {
        this.firstProductImageRes = firstProductImageRes;
    }

    /**
     * Step-by-step builder that enforces BR-8 (delivery needs an address, pickup a branch).
     */
    public static final class Builder {

        /** The order under construction. */
        private final Order order = new Order();

        /**
         * Starts a builder.
         *
         * @param userId the owner
         */
        public Builder(long userId) {
            order.userId = userId;
        }

        /**
         * Chooses pickup at a branch.
         *
         * @param branchId the branch
         * @return this builder
         */
        public Builder pickupAt(long branchId) {
            order.fulfilmentType = FulfilmentType.PICKUP;
            order.branchId = branchId;
            order.addressId = null;
            order.paymentMethod = PaymentMethod.PAY_AT_PICKUP;
            return this;
        }

        /**
         * Chooses home delivery to an address.
         *
         * @param addressId the address
         * @param fee       the delivery fee for the address's district
         * @return this builder
         */
        public Builder deliverTo(long addressId, Money fee) {
            order.fulfilmentType = FulfilmentType.DELIVERY;
            order.addressId = addressId;
            order.branchId = null;
            order.deliveryFee = fee;
            order.paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
            return this;
        }

        /**
         * Sets the schedule.
         *
         * @param date the date
         * @param slot the slot label
         * @return this builder
         */
        public Builder schedule(LocalDate date, String slot) {
            order.scheduledDate = date;
            order.scheduledSlot = slot;
            return this;
        }

        /**
         * Overrides the payment method.
         *
         * @param method the method
         * @return this builder
         */
        public Builder payment(PaymentMethod method) {
            order.paymentMethod = method;
            return this;
        }

        /**
         * Sets the applied promotion.
         *
         * @param promoId the promotion id, may be null
         * @return this builder
         */
        public Builder promotion(Long promoId) {
            order.promoId = promoId;
            return this;
        }

        /**
         * Sets the amounts.
         *
         * @param subtotal    subtotal after bulk discounts
         * @param discount    promo discount
         * @param deliveryFee delivery fee
         * @param total       amount payable
         * @return this builder
         */
        public Builder amounts(Money subtotal, Money discount, Money deliveryFee, Money total) {
            order.subtotal = subtotal;
            order.discount = discount;
            order.deliveryFee = deliveryFee;
            order.total = total;
            return this;
        }

        /**
         * Sets the order lines.
         *
         * @param items the lines
         * @return this builder
         */
        public Builder items(List<OrderItem> items) {
            order.setItems(new ArrayList<>(items));
            order.itemCount = items.size();
            return this;
        }

        /**
         * Sets optional notes.
         *
         * @param notes the notes
         * @return this builder
         */
        public Builder notes(String notes) {
            order.notes = notes;
            return this;
        }

        /**
         * Validates BR-8 and returns the order.
         *
         * @return the order
         * @throws IllegalStateException when fulfilment details, schedule or items are missing
         */
        public Order build() {
            boolean pickupOk = order.fulfilmentType == FulfilmentType.PICKUP && order.branchId != null;
            boolean deliveryOk = order.fulfilmentType == FulfilmentType.DELIVERY && order.addressId != null;
            if (!pickupOk && !deliveryOk) {
                throw new IllegalStateException("Pickup needs a branch and delivery needs an address");
            }
            if (order.scheduledDate == null || order.scheduledSlot == null) {
                throw new IllegalStateException("Schedule is required");
            }
            if (order.items.isEmpty()) {
                throw new IllegalStateException("An order needs at least one item");
            }
            return order;
        }
    }
}
