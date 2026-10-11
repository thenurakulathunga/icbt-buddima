package com.printxpress.app.data.model;

import java.time.LocalDateTime;

/**
 * Artwork saved by a customer (table {@code designs}): an uploaded file, custom text, or both.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Design {

    /** The primary key. */
    private long designId;
    /** The owner. */
    private long userId;
    /** The title shown in Saved designs. */
    private String title;
    /** The app-private file path, may be null. */
    private String filePath;
    /** The file MIME type, may be null. */
    private String mimeType;
    /** The file size in bytes. */
    private long fileSizeBytes;
    /** The custom text, may be null. */
    private String customText;
    /** The creation time. */
    private LocalDateTime createdAt;

    /** Creates an empty Design; fields are filled by setters or the DAO. */
    public Design() {
    }

    /** @return the primary key */
    public long getDesignId() {
        return designId;
    }

    /**
     * Sets the primary key.
     *
     * @param designId the new value
     */
    public void setDesignId(long designId) {
        this.designId = designId;
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

    /** @return the title shown in Saved designs */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title shown in Saved designs.
     *
     * @param title the new value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the app-private file path, may be null */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the app-private file path, may be null.
     *
     * @param filePath the new value
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /** @return the file MIME type, may be null */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the file MIME type, may be null.
     *
     * @param mimeType the new value
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** @return the file size in bytes */
    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    /**
     * Sets the file size in bytes.
     *
     * @param fileSizeBytes the new value
     */
    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    /** @return the custom text, may be null */
    public String getCustomText() {
        return customText;
    }

    /**
     * Sets the custom text, may be null.
     *
     * @param customText the new value
     */
    public void setCustomText(String customText) {
        this.customText = customText;
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
}
