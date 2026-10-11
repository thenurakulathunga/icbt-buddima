package com.printxpress.app.data.model;

/**
 * An option chosen for an order line with its price at the time of purchase (table {@code order_item_options}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class OrderItemOption {

    /** The order line foreign key. */
    private long orderItemId;
    /** The product option foreign key. */
    private long optionId;
    /** The modifier percentage when ordered. */
    private double priceModifierSnapshot;
    /** The option type name (joined, read-only). */
    private String optionTypeName;
    /** The option value (joined, read-only). */
    private String value;

    /** Creates an empty OrderItemOption; fields are filled by setters or the DAO. */
    public OrderItemOption() {
    }

    /** @return the order line foreign key */
    public long getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the order line foreign key.
     *
     * @param orderItemId the new value
     */
    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /** @return the product option foreign key */
    public long getOptionId() {
        return optionId;
    }

    /**
     * Sets the product option foreign key.
     *
     * @param optionId the new value
     */
    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    /** @return the modifier percentage when ordered */
    public double getPriceModifierSnapshot() {
        return priceModifierSnapshot;
    }

    /**
     * Sets the modifier percentage when ordered.
     *
     * @param priceModifierSnapshot the new value
     */
    public void setPriceModifierSnapshot(double priceModifierSnapshot) {
        this.priceModifierSnapshot = priceModifierSnapshot;
    }

    /** @return the option type name (joined, read-only) */
    public String getOptionTypeName() {
        return optionTypeName;
    }

    /**
     * Sets the option type name (joined, read-only).
     *
     * @param optionTypeName the new value
     */
    public void setOptionTypeName(String optionTypeName) {
        this.optionTypeName = optionTypeName;
    }

    /** @return the option value (joined, read-only) */
    public String getValue() {
        return value;
    }

    /**
     * Sets the option value (joined, read-only).
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
