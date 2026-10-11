package com.printxpress.app.data.model;

/**
 * A frequently asked question (table {@code faqs}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Faq {

    /** The primary key. */
    private long faqId;
    /** The category label. */
    private String category;
    /** The question. */
    private String question;
    /** The answer. */
    private String answer;
    /** The display order. */
    private int sortOrder;

    /** Creates an empty Faq; fields are filled by setters or the DAO. */
    public Faq() {
    }

    /** @return the primary key */
    public long getFaqId() {
        return faqId;
    }

    /**
     * Sets the primary key.
     *
     * @param faqId the new value
     */
    public void setFaqId(long faqId) {
        this.faqId = faqId;
    }

    /** @return the category label */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category label.
     *
     * @param category the new value
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /** @return the question */
    public String getQuestion() {
        return question;
    }

    /**
     * Sets the question.
     *
     * @param question the new value
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /** @return the answer */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer.
     *
     * @param answer the new value
     */
    public void setAnswer(String answer) {
        this.answer = answer;
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
