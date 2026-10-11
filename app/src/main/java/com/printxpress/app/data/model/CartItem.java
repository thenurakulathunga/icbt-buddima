package com.printxpress.app.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A configured product waiting in the cart (tables {@code cart_items} + {@code cart_item_options}).
 * <p>
 * Pattern role: Builder. A cart item has many optional parts (design, custom text, instructions,
 * options), so {@link Builder} keeps construction readable. BR-9 is enforced by
 * {@link #hasArtwork()}: each line needs an uploaded design or custom text.
 * </p>
 *
 * @since 1.0
 */
public class CartItem {

    /** The primary key, 0 before insertion. */
    private long cartItemId;
    /** The owner. */
    private long userId;
    /** The configured product id. */
    private long productId;
    /** The saved design, or null when only text is used. */
    private Long designId;
    /** The quantity. */
    private int quantity;
    /** The custom text, may be null. */
    private String customText;
    /** The print instructions, may be null. */
    private String instructions;
    /** The creation time. */
    private LocalDateTime createdAt;
    /** The chosen options. */
    private List<ProductOption> options = new ArrayList<>();
    /** The configured product (joined, read-only). */
    private Product product;
    /** The attached design (joined, read-only). */
    private Design design;

    /** Creates an empty cart item; prefer {@link Builder}. */
    public CartItem() {
    }

    /**
     * Checks BR-9: the line has a design with a file, or non-blank custom text.
     *
     * @return true when artwork is present
     */
    public boolean hasArtwork() {
        boolean hasText = customText != null && !customText.trim().isEmpty();
        return designId != null || hasText;
    }

    /** @return the primary key */
    public long getCartItemId() {
        return cartItemId;
    }

    /**
     * Sets the primary key.
     *
     * @param cartItemId the new value
     */
    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
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

    /** @return the product id */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the product id.
     *
     * @param productId the new value
     */
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /** @return the design id, may be null */
    public Long getDesignId() {
        return designId;
    }

    /**
     * Sets the design id.
     *
     * @param designId the new value, may be null
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

    /** @return the custom text, may be null */
    public String getCustomText() {
        return customText;
    }

    /**
     * Sets the custom text.
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
     * Sets the print instructions.
     *
     * @param instructions the new value
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    /** @return the chosen options (never null) */
    public List<ProductOption> getOptions() {
        return options;
    }

    /**
     * Sets the chosen options.
     *
     * @param options the new value; null becomes an empty list
     */
    public void setOptions(List<ProductOption> options) {
        this.options = options == null ? new ArrayList<>() : new ArrayList<>(options);
    }

    /** @return the joined product, may be null */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the joined product.
     *
     * @param product the new value
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /** @return the joined design, may be null */
    public Design getDesign() {
        return design;
    }

    /**
     * Sets the joined design.
     *
     * @param design the new value
     */
    public void setDesign(Design design) {
        this.design = design;
    }

    /**
     * Step-by-step builder for {@link CartItem}.
     */
    public static final class Builder {

        /** The item under construction. */
        private final CartItem item = new CartItem();

        /**
         * Starts a builder for a user and product.
         *
         * @param userId    the owner
         * @param productId the product
         */
        public Builder(long userId, long productId) {
            item.userId = userId;
            item.productId = productId;
        }

        /**
         * Sets the existing id when editing a cart line.
         *
         * @param cartItemId the id
         * @return this builder
         */
        public Builder id(long cartItemId) {
            item.cartItemId = cartItemId;
            return this;
        }

        /**
         * Sets the quantity.
         *
         * @param quantity the quantity
         * @return this builder
         */
        public Builder quantity(int quantity) {
            item.quantity = quantity;
            return this;
        }

        /**
         * Attaches a saved design.
         *
         * @param designId the design id, may be null
         * @return this builder
         */
        public Builder design(Long designId) {
            item.designId = designId;
            return this;
        }

        /**
         * Sets the custom text; blank text is stored as null.
         *
         * @param text the text
         * @return this builder
         */
        public Builder customText(String text) {
            item.customText = blankToNull(text);
            return this;
        }

        /**
         * Sets the print instructions; blank text is stored as null.
         *
         * @param text the instructions
         * @return this builder
         */
        public Builder instructions(String text) {
            item.instructions = blankToNull(text);
            return this;
        }

        /**
         * Sets the chosen options.
         *
         * @param options the options
         * @return this builder
         */
        public Builder options(List<ProductOption> options) {
            item.setOptions(options == null ? Collections.emptyList() : options);
            return this;
        }

        /**
         * Finishes the item.
         *
         * @return the cart item
         */
        public CartItem build() {
            return item;
        }

        /**
         * Converts blank text to null.
         *
         * @param text the text
         * @return the trimmed text or null
         */
        private static String blankToNull(String text) {
            return text == null || text.trim().isEmpty() ? null : text.trim();
        }
    }
}
