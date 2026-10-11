package com.printxpress.app.data.model;

/**
 * A sample design in the gallery (table {@code gallery_items}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class GalleryItem {

    /** The primary key. */
    private long galleryId;
    /** The category foreign key. */
    private long categoryId;
    /** The title. */
    private String title;
    /** The description. */
    private String description;
    /** The image drawable name. */
    private String imageRes;
    /** The whether it is a seasonal design. */
    private boolean seasonal;
    /** The category name (joined, read-only). */
    private String categoryName;
    /** The category image name (joined, read-only). */
    private String categoryImageRes;

    /** Creates an empty GalleryItem; fields are filled by setters or the DAO. */
    public GalleryItem() {
    }

    /** @return the primary key */
    public long getGalleryId() {
        return galleryId;
    }

    /**
     * Sets the primary key.
     *
     * @param galleryId the new value
     */
    public void setGalleryId(long galleryId) {
        this.galleryId = galleryId;
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

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the description */
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

    /** @return the whether it is a seasonal design */
    public boolean isSeasonal() {
        return seasonal;
    }

    /**
     * Sets the whether it is a seasonal design.
     *
     * @param seasonal the new value
     */
    public void setSeasonal(boolean seasonal) {
        this.seasonal = seasonal;
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

    /** @return the category image name (joined, read-only) */
    public String getCategoryImageRes() {
        return categoryImageRes;
    }

    /**
     * Sets the category image name (joined, read-only).
     *
     * @param categoryImageRes the new value
     */
    public void setCategoryImageRes(String categoryImageRes) {
        this.categoryImageRes = categoryImageRes;
    }
}
