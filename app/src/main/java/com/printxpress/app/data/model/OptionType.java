package com.printxpress.app.data.model;

/**
 * A specification dimension such as Size or Paper Type (table {@code option_types}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class OptionType {

    /** The primary key. */
    private long optionTypeId;
    /** The type name. */
    private String name;
    /** The display order. */
    private int sortOrder;

    /** Creates an empty OptionType; fields are filled by setters or the DAO. */
    public OptionType() {
    }

    /** @return the primary key */
    public long getOptionTypeId() {
        return optionTypeId;
    }

    /**
     * Sets the primary key.
     *
     * @param optionTypeId the new value
     */
    public void setOptionTypeId(long optionTypeId) {
        this.optionTypeId = optionTypeId;
    }

    /** @return the type name */
    public String getName() {
        return name;
    }

    /**
     * Sets the type name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
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
