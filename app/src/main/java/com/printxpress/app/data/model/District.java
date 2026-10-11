package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;

/**
 * A Sri Lankan district with its home-delivery fee (table {@code districts}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class District {

    /** The primary key. */
    private long districtId;
    /** The district name. */
    private String name;
    /** The delivery fee to this district. */
    private Money deliveryFee = Money.ZERO;

    /** Creates an empty District; fields are filled by setters or the DAO. */
    public District() {
    }

    /** @return the primary key */
    public long getDistrictId() {
        return districtId;
    }

    /**
     * Sets the primary key.
     *
     * @param districtId the new value
     */
    public void setDistrictId(long districtId) {
        this.districtId = districtId;
    }

    /** @return the district name */
    public String getName() {
        return name;
    }

    /**
     * Sets the district name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the delivery fee to this district */
    public Money getDeliveryFee() {
        return deliveryFee;
    }

    /**
     * Sets the delivery fee to this district.
     *
     * @param deliveryFee the new value
     */
    public void setDeliveryFee(Money deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
