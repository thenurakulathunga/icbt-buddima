package com.printxpress.app.data.model;

import com.printxpress.app.core.common.Money;
import java.util.ArrayList;
import java.util.List;

/**
 * One line of an order (table {@code order_items}); prices are snapshots taken when the order was placed.
 * <p>Encapsulated entity: private fields with getters and setters, mapped by its DAO.</p>
 *
 * @since 1.0
 */
public class OrderItem {

    /** The primary key. */
    private long orderItemId;
    /** The order foreign key. */
    private long orderId;
    /** The product foreign key. */
    private long productId;
    /** The design foreign key, may be null. */
    private Long designId;
    /** The quantity. */
    private int quantity;
    /** The unit price including options, before bulk discount. */
    private Money unitPrice = Money.ZERO;
    /** The line total after bulk discount. */
    private Money lineTotal = Money.ZERO;
    /** The custom text, may be null. */
    private String customText;
    /** The print instructions, may be null. */
    private String instructions;
    /** The chosen options. */
    private List<OrderItemOption> options = new ArrayList<>();
    /** The product name (joined, read-only). */
    private String productName;
    /** The product image name (joined, read-only). */
    private String productImageRes;
    /** The category image name (joined, read-only). */
    private String categoryImageRes;
    /** The design title (joined, read-only). */
    private String designTitle;
    /** The design file path (joined, read-only). */
    private String designFilePath;
    /** The product turnaround in days (joined, read-only). */
    private int turnaroundDays;
    /** The design MIME type (joined, read-only). */
    private String designMimeType;

    /** Creates an empty OrderItem; fields are filled by setters or the DAO. */
    public OrderItem() {
    }

    /** @return the primary key */
    public long getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the primary key.
     *
     * @param orderItemId the new value
     */
    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /** @return the order foreign key */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order foreign key.
     *
     * @param orderId the new value
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    /** @return the design foreign key, may be null */
    public Long getDesignId() {
        return designId;
    }

    /**
     * Sets the design foreign key, may be null.
     *
     * @param designId the new value
     */
    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    /** @return the quantity */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new value
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** @return the unit price including options, before bulk discount */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price including options, before bulk discount.
     *
     * @param unitPrice the new value
     */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    /** @return the line total after bulk discount */
    public Money getLineTotal() {
        return lineTotal;
    }

    /**
     * Sets the line total after bulk discount.
     *
     * @param lineTotal the new value
     */
    public void setLineTotal(Money lineTotal) {
        this.lineTotal = lineTotal;
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

    /** @return the print instructions, may be null */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the print instructions, may be null.
     *
     * @param instructions the new value
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /** @return the chosen options */
    public List<OrderItemOption> getOptions() {
        return options;
    }

    /**
     * Sets the chosen options.
     *
     * @param options the new value
     */
    public void setOptions(List<OrderItemOption> options) {
        this.options = options;
    }

    /** @return the product name (joined, read-only) */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name (joined, read-only).
     *
     * @param productName the new value
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /** @return the product image name (joined, read-only) */
    public String getProductImageRes() {
        return productImageRes;
    }

    /**
     * Sets the product image name (joined, read-only).
     *
     * @param productImageRes the new value
     */
    public void setProductImageRes(String productImageRes) {
        this.productImageRes = productImageRes;
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

    /** @return the design title (joined, read-only) */
    public String getDesignTitle() {
        return designTitle;
    }

    /**
     * Sets the design title (joined, read-only).
     *
     * @param designTitle the new value
     */
    public void setDesignTitle(String designTitle) {
        this.designTitle = designTitle;
    }

    /** @return the design file path (joined, read-only) */
    public String getDesignFilePath() {
        return designFilePath;
    }

    /**
     * Sets the design file path (joined, read-only).
     *
     * @param designFilePath the new value
     */
    public void setDesignFilePath(String designFilePath) {
        this.designFilePath = designFilePath;
    }

    /** @return the design MIME type (joined, read-only) */
    public String getDesignMimeType() {
        return designMimeType;
    }

    /**
     * Sets the design MIME type (joined, read-only).
     *
     * @param designMimeType the new value
     */
    public void setDesignMimeType(String designMimeType) {
        this.designMimeType = designMimeType;
    }

    /** @return the product turnaround in days (joined, read-only) */
    public int getTurnaroundDays() {
        return turnaroundDays;
    }

    /**
     * Sets the product turnaround in days.
     *
     * @param turnaroundDays the new value
     */
    public void setTurnaroundDays(int turnaroundDays) {
        this.turnaroundDays = turnaroundDays;
    }
}
