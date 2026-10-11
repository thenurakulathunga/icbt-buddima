package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;

/**
 * A customer delivery address (table {@code addresses}); the district decides the delivery fee.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Address {

    /** The primary key. */
    private long addressId;
    /** The owner. */
    private long userId;
    /** The district foreign key. */
    private long districtId;
    /** The label such as Home or Work. */
    private String label;
    /** The recipient name. */
    private String recipientName;
    /** The recipient mobile number. */
    private String recipientPhone;
    /** The first address line. */
    private String line1;
    /** The optional second address line. */
    private String line2;
    /** The city. */
    private String city;
    /** The five-digit postal code. */
    private String postalCode;
    /** The whether this is the default address. */
    private boolean defaultAddress;
    /** The district name (joined, read-only). */
    private String districtName;
    /** The district delivery fee (joined, read-only). */
    private Money deliveryFee = Money.ZERO;

    /** Creates an empty Address; fields are filled by setters or the DAO. */
    public Address() {
    }

    /** @return the primary key */
    public long getAddressId() {
        return addressId;
    }

    /**
     * Sets the primary key.
     *
     * @param addressId the new value
     */
    public void setAddressId(long addressId) {
        this.addressId = addressId;
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

    /** @return the district foreign key */
    public long getDistrictId() {
        return districtId;
    }

    /**
     * Sets the district foreign key.
     *
     * @param districtId the new value
     */
    public void setDistrictId(long districtId) {
        this.districtId = districtId;
    }

    /** @return the label such as Home or Work */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label such as Home or Work.
     *
     * @param label the new value
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /** @return the recipient name */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Sets the recipient name.
     *
     * @param recipientName the new value
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /** @return the recipient mobile number */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * Sets the recipient mobile number.
     *
     * @param recipientPhone the new value
     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    /** @return the first address line */
    public String getLine1() {
        return line1;
    }

    /**
     * Sets the first address line.
     *
     * @param line1 the new value
     */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /** @return the optional second address line */
    public String getLine2() {
        return line2;
    }

    /**
     * Sets the optional second address line.
     *
     * @param line2 the new value
     */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /** @return the city */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the new value
     */
    public void setCity(String city) {
        this.city = city;
    }

    /** @return the five-digit postal code */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the five-digit postal code.
     *
     * @param postalCode the new value
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** @return the whether this is the default address */
    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    /**
     * Sets the whether this is the default address.
     *
     * @param defaultAddress the new value
     */
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    /** @return the district name (joined, read-only) */
    public String getDistrictName() {
        return districtName;
    }

    /**
     * Sets the district name (joined, read-only).
     *
     * @param districtName the new value
     */
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /** @return the district delivery fee (joined, read-only) */
    public Money getDeliveryFee() {
        return deliveryFee;
    }

    /**
     * Sets the district delivery fee (joined, read-only).
     *
     * @param deliveryFee the new value
     */
    public void setDeliveryFee(Money deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
