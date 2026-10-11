package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;

/**
 * A printable product (table {@code products}). {@code basePrice} is the price of one unit before options and discounts.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Product {

    /** The primary key. */
    private long productId;
    /** The category foreign key. */
    private long categoryId;
    /** The product name. */
    private String name;
    /** The marketing description. */
    private String description;
    /** The price per unit. */
    private Money basePrice = Money.ZERO;
    /** The unit noun, e.g. card or banner. */
    private String priceUnit;
    /** The minimum order quantity. */
    private int minQty = 1;
    /** The maximum order quantity. */
    private int maxQty = 1;
    /** The quantity increment. */
    private int qtyStep = 1;
    /** The working days needed to print. */
    private int turnaroundDays = 2;
    /** The image drawable name. */
    private String imageRes;
    /** The whether the product is sold. */
    private boolean active = true;
    /** The category name (joined, read-only). */
    private String categoryName;
    /** The category image name used as a fallback (joined, read-only). */
    private String categoryImageRes;

    /** Creates an empty Product; fields are filled by setters or the DAO. */
    public Product() {
    }

    /** @return the primary key */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the primary key.
     *
     * @param productId the new value
     */
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /** @return the category foreign key */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category foreign key.
     *
     * @param categoryId the new value
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /** @return the product name */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the marketing description */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the marketing description.
     *
     * @param description the new value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the price per unit */
    public Money getBasePrice() {
        return basePrice;
    }

    /**
     * Sets the price per unit.
     *
     * @param basePrice the new value
     */
    public void setBasePrice(Money basePrice) {
        this.basePrice = basePrice;
    }

    /** @return the unit noun, e.g. card or banner */
    public String getPriceUnit() {
        return priceUnit;
    }

    /**
     * Sets the unit noun, e.g. card or banner.
     *
     * @param priceUnit the new value
     */
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    /** @return the minimum order quantity */
    public int getMinQty() {
        return minQty;
    }

    /**
     * Sets the minimum order quantity.
     *
     * @param minQty the new value
     */
    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }

    /** @return the maximum order quantity */
    public int getMaxQty() {
        return maxQty;
    }

    /**
     * Sets the maximum order quantity.
     *
     * @param maxQty the new value
     */
    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    /** @return the quantity increment */
    public int getQtyStep() {
        return qtyStep;
    }

    /**
     * Sets the quantity increment.
     *
     * @param qtyStep the new value
     */
    public void setQtyStep(int qtyStep) {
        this.qtyStep = qtyStep;
    }

    /** @return the working days needed to print */
    public int getTurnaroundDays() {
        return turnaroundDays;
    }

    /**
     * Sets the working days needed to print.
     *
     * @param turnaroundDays the new value
     */
    public void setTurnaroundDays(int turnaroundDays) {
        this.turnaroundDays = turnaroundDays;
    }

    /** @return the image drawable name */
    public String getImageRes() {
        return imageRes;
    }

    /**
     * Sets the image drawable name.
     *
     * @param imageRes the new value
     */
    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    /** @return the whether the product is sold */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the whether the product is sold.
     *
     * @param active the new value
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @return the category name (joined, read-only) */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the category name (joined, read-only).
     *
     * @param categoryName the new value
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /** @return the category image name used as a fallback (joined, read-only) */
    public String getCategoryImageRes() {
        return categoryImageRes;
    }

    /**
     * Sets the category image name used as a fallback (joined, read-only).
     *
     * @param categoryImageRes the new value
     */
    public void setCategoryImageRes(String categoryImageRes) {
        this.categoryImageRes = categoryImageRes;
    }
}
