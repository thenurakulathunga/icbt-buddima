package com.printxpress.app.data.model;

/**
 * An allowed value of an option type for one product (table {@code product_options}). The modifier is a percentage added to the unit price.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class ProductOption {

    /** The primary key. */
    private long optionId;
    /** The product foreign key. */
    private long productId;
    /** The option type foreign key. */
    private long optionTypeId;
    /** The option value, e.g. A4. */
    private String value;
    /** The percentage added to the unit price, e.g. 40 for +40%. */
    private double priceModifier;
    /** The whether pre-selected. */
    private boolean defaultOption;
    /** The option type name (joined, read-only). */
    private String optionTypeName;
    /** The option type display order (joined, read-only). */
    private int optionTypeSort;

    /** Creates an empty ProductOption; fields are filled by setters or the DAO. */
    public ProductOption() {
    }

    /** @return the primary key */
    public long getOptionId() {
        return optionId;
    }

    /**
     * Sets the primary key.
     *
     * @param optionId the new value
     */
    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    /** @return the product foreign key */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the product foreign key.
     *
     * @param productId the new value
     */
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /** @return the option type foreign key */
    public long getOptionTypeId() {
        return optionTypeId;
    }

    /**
     * Sets the option type foreign key.
     *
     * @param optionTypeId the new value
     */
    public void setOptionTypeId(long optionTypeId) {
        this.optionTypeId = optionTypeId;
    }

    /** @return the option value, e.g. A4 */
    public String getValue() {
        return value;
    }

    /**
     * Sets the option value, e.g. A4.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** @return the percentage added to the unit price, e.g. 40 for +40% */
    public double getPriceModifier() {
        return priceModifier;
    }

    /**
     * Sets the percentage added to the unit price, e.g. 40 for +40%.
     *
     * @param priceModifier the new value
     */
    public void setPriceModifier(double priceModifier) {
        this.priceModifier = priceModifier;
    }

    /** @return the whether pre-selected */
    public boolean isDefaultOption() {
        return defaultOption;
    }

    /**
     * Sets the whether pre-selected.
     *
     * @param defaultOption the new value
     */
    public void setDefaultOption(boolean defaultOption) {
        this.defaultOption = defaultOption;
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

    /** @return the option type display order (joined, read-only) */
    public int getOptionTypeSort() {
        return optionTypeSort;
    }

    /**
     * Sets the option type display order (joined, read-only).
     *
     * @param optionTypeSort the new value
     */
    public void setOptionTypeSort(int optionTypeSort) {
        this.optionTypeSort = optionTypeSort;
    }
}
