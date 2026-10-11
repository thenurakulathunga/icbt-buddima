package com.printxpress.app.data.model;

/**
 * A pickup branch (table {@code branches}).
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class Branch {

    /** The primary key. */
    private long branchId;
    /** The branch name. */
    private String name;
    /** The street address. */
    private String address;
    /** The branch phone. */
    private String phone;
    /** The opening hours text. */
    private String openingHours;

    /** Creates an empty Branch; fields are filled by setters or the DAO. */
    public Branch() {
    }

    /** @return the primary key */
    public long getBranchId() {
        return branchId;
    }

    /**
     * Sets the primary key.
     *
     * @param branchId the new value
     */
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    /** @return the branch name */
    public String getName() {
        return name;
    }

    /**
     * Sets the branch name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the street address */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the street address.
     *
     * @param address the new value
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return the branch phone */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the branch phone.
     *
     * @param phone the new value
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return the opening hours text */
    public String getOpeningHours() {
        return openingHours;
    }

    /**
     * Sets the opening hours text.
     *
     * @param openingHours the new value
     */
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
}
