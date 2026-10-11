package com.printxpress.app.data.model;

import com.printxpress.app.data.model.enums.FulfilmentType;

import java.time.LocalDate;

/**
 * Immutable checkout choices collected across the checkout screens. Prices are not included:
 * the repository recalculates them from the cart so the UI can never dictate a total.
 *
 * @since 1.0
 */
public final class CheckoutRequest {

    /** The customer. */
    private final long userId;
    /** Pickup or delivery. */
    private final FulfilmentType fulfilmentType;
    /** Pickup branch, or null. */
    private final Long branchId;
    /** Delivery address, or null. */
    private final Long addressId;
    /** Chosen date. */
    private final LocalDate date;
    /** Chosen slot label. */
    private final String slot;
    /** Typed promo code, may be null. */
    private final String promoCode;
    /** Optional notes. */
    private final String notes;

    /**
     * Creates a request.
     *
     * @param userId         the customer
     * @param fulfilmentType pickup or delivery
     * @param branchId       pickup branch, may be null
     * @param addressId      delivery address, may be null
     * @param date           chosen date
     * @param slot           chosen slot label
     * @param promoCode      promo code, may be null
     * @param notes          optional notes
     */
    public CheckoutRequest(long userId, FulfilmentType fulfilmentType, Long branchId, Long addressId, LocalDate date,
                           String slot, String promoCode, String notes) {
        this.userId = userId;
        this.fulfilmentType = fulfilmentType;
        this.branchId = branchId;
        this.addressId = addressId;
        this.date = date;
        this.slot = slot;
        this.promoCode = promoCode;
        this.notes = notes;
    }

    /** @return the customer */
    public long getUserId() {
        return userId;
    }

    /** @return pickup or delivery */
    public FulfilmentType getFulfilmentType() {
        return fulfilmentType;
    }

    /** @return the pickup branch, may be null */
    public Long getBranchId() {
        return branchId;
    }

    /** @return the delivery address, may be null */
    public Long getAddressId() {
        return addressId;
    }

    /** @return the chosen date */
    public LocalDate getDate() {
        return date;
    }

    /** @return the chosen slot label */
    public String getSlot() {
        return slot;
    }

    /** @return the promo code, may be null */
    public String getPromoCode() {
        return promoCode;
    }

    /** @return the notes, may be null */
    public String getNotes() {
        return notes;
    }
}
