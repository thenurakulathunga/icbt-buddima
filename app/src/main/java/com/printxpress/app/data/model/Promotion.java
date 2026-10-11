package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.enums.DiscountType;

import java.time.LocalDate;

/**
 * A seasonal offer with a promo code (table {@code promotions}).
 * <p>
 * Encapsulates the date-window and minimum-order checks of BR-5 so every caller applies the same
 * rules. A null category means the code applies to every category.
 * </p>
 *
 * @since 1.0
 */
public class Promotion {

    /** The primary key. */
    private long promoId;
    /** The targeted category, or null for all categories. */
    private Long categoryId;
    /** The headline. */
    private String title;
    /** The description and terms. */
    private String description;
    /** The upper-case promo code. */
    private String promoCode;
    /** How the discount is applied. */
    private DiscountType discountType = DiscountType.PERCENT;
    /** Percentage or rupee value of the discount. */
    private double discountValue;
    /** Minimum eligible subtotal. */
    private Money minOrderValue = Money.ZERO;
    /** First valid day, inclusive. */
    private LocalDate startDate;
    /** Last valid day, inclusive. */
    private LocalDate endDate;
    /** Banner drawable name. */
    private String imageRes;
    /** Whether the promotion is switched on. */
    private boolean active = true;
    /** The targeted category name (joined, read-only). */
    private String categoryName;

    /** Creates an empty promotion; fields are filled by setters or the DAO. */
    public Promotion() {
    }

    /**
     * Checks the active flag and the date window.
     *
     * @param date the day to check
     * @return true when the code can be used on that day
     */
    public boolean isValidOn(LocalDate date) {
        return active && date != null && startDate != null && endDate != null
                && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Checks the minimum order value.
     *
     * @param eligibleAmount the amount the promotion would apply to
     * @return true when the amount meets the minimum
     */
    public boolean meetsMinimum(Money eligibleAmount) {
        return eligibleAmount != null && eligibleAmount.isAtLeast(minOrderValue);
    }

    /**
     * Checks whether a product category is targeted.
     *
     * @param productCategoryId the category of a cart line
     * @return true when the promotion covers that category
     */
    public boolean appliesToCategory(long productCategoryId) {
        return categoryId == null || categoryId == productCategoryId;
    }

    /** @return the primary key */
    public long getPromoId() {
        return promoId;
    }

    /**
     * Sets the primary key.
     *
     * @param promoId the new value
     */
    public void setPromoId(long promoId) {
        this.promoId = promoId;
    }

    /** @return the targeted category, or null for all */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the targeted category.
     *
     * @param categoryId the category, or null for all
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /** @return the headline */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the headline.
     *
     * @param title the new value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the description and terms */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the upper-case promo code */
    public String getPromoCode() {
        return promoCode;
    }

    /**
     * Sets the promo code.
     *
     * @param promoCode the new value
     */
    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    /** @return how the discount is applied */
    public DiscountType getDiscountType() {
        return discountType;
    }

    /**
     * Sets the discount type.
     *
     * @param discountType the new value
     */
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    /** @return the percentage or rupee value */
    public double getDiscountValue() {
        return discountValue;
    }

    /**
     * Sets the discount value.
     *
     * @param discountValue the new value
     */
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    /** @return the minimum eligible subtotal */
    public Money getMinOrderValue() {
        return minOrderValue;
    }

    /**
     * Sets the minimum eligible subtotal.
     *
     * @param minOrderValue the new value
     */
    public void setMinOrderValue(Money minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    /** @return the first valid day */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the first valid day.
     *
     * @param startDate the new value
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** @return the last valid day */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the last valid day.
     *
     * @param endDate the new value
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /** @return the banner drawable name */
    public String getImageRes() {
        return imageRes;
    }

    /**
     * Sets the banner drawable name.
     *
     * @param imageRes the new value
     */
    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    /** @return whether the promotion is switched on */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active flag.
     *
     * @param active the new value
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @return the targeted category name, may be null */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the targeted category name.
     *
     * @param categoryName the new value
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
