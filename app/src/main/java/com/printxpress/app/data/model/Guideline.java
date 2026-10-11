package com.printxpress.app.data.model;

import com.printxpress.app.data.model.enums.GuidelineTopic;

/**
 * A print file guideline (table {@code guidelines}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Guideline {

    /** The primary key. */
    private long guidelineId;
    /** The topic. */
    private GuidelineTopic topic = GuidelineTopic.FILE_FORMAT;
    /** The title. */
    private String title;
    /** The full text. */
    private String content;
    /** The illustration drawable name, may be null. */
    private String imageRes;
    /** The display order. */
    private int sortOrder;

    /** Creates an empty Guideline; fields are filled by setters or the DAO. */
    public Guideline() {
    }

    /** @return the primary key */
    public long getGuidelineId() {
        return guidelineId;
    }

    /**
     * Sets the primary key.
     *
     * @param guidelineId the new value
     */
    public void setGuidelineId(long guidelineId) {
        this.guidelineId = guidelineId;
    }

    /** @return the topic */
    public GuidelineTopic getTopic() {
        return topic;
    }

    /**
     * Sets the topic.
     *
     * @param topic the new value
     */
    public void setTopic(GuidelineTopic topic) {
        this.topic = topic;
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

    /** @return the full text */
    public String getContent() {
        return content;
    }

    /**
     * Sets the full text.
     *
     * @param content the new value
     */
    public void setContent(String content) {
        this.content = content;
    }

    /** @return the illustration drawable name, may be null */
    public String getImageRes() {
        return imageRes;
    }

    /**
     * Sets the illustration drawable name, may be null.
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
}
