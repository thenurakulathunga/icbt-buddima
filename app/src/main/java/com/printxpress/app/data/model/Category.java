package com.printxpress.app.data.model;

/**
 * A product category such as Business Cards (table {@code categories}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Category {

    /** The primary key. */
    private long categoryId;
    /** The category name. */
    private String name;
    /** The short description. */
    private String description;
    /** The icon drawable name. */
    private String iconRes;
    /** The image drawable name. */
    private String imageRes;
    /** The display order. */
    private int sortOrder;
    /** The number of active products (computed, read-only). */
    private int productCount;

    /** Creates an empty Category; fields are filled by setters or the DAO. */
    public Category() {
    }

    /** @return the primary key */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the primary key.
     *
     * @param categoryId the new value
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /** @return the category name */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the short description */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the short description.
     *
     * @param description the new value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the icon drawable name */
    public String getIconRes() {
        return iconRes;
    }

    /**
     * Sets the icon drawable name.
     *
     * @param iconRes the new value
     */
    public void setIconRes(String iconRes) {
        this.iconRes = iconRes;
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

    /** @return the display order */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the display order.
     *
     * @param sortOrder the new value
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /** @return the number of active products (computed, read-only) */
    public int getProductCount() {
        return productCount;
    }

    /**
     * Sets the number of active products (computed, read-only).
     *
     * @param productCount the new value
     */
    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
