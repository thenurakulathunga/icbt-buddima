package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.CartItem;

import java.util.List;

/**
 * The signed-in user's cart (FR-2.8).
 * <p>Pattern role: Repository; also the Subject for the cart badge count (Observer).</p>
 *
 * @since 1.0
 */
public interface CartRepository {

    /**
     * Lists the cart with products, options and designs.
     *
     * @param userId the owner
     * @return the lines
     */
    LiveData<Resource<List<CartItem>>> getCart(long userId);

    /**
     * Loads one line for editing.
     *
     * @param cartItemId the line
     * @return the line
     */
    LiveData<Resource<CartItem>> getItem(long cartItemId);

    /**
     * Adds or updates a line after re-checking BR-3 and BR-9.
     *
     * @param item the line (id 0 for a new one)
     * @return the line id
     */
    LiveData<Resource<Long>> save(CartItem item);

    /**
     * Changes a line's quantity after re-checking BR-3.
     *
     * @param userId     the owner
     * @param cartItemId the line
     * @param quantity   the new quantity
     * @return true on success
     */
    LiveData<Resource<Boolean>> updateQuantity(long userId, long cartItemId, int quantity);

    /**
     * Removes a line.
     *
     * @param userId     the owner
     * @param cartItemId the line
     * @return true on success
     */
    LiveData<Resource<Boolean>> remove(long userId, long cartItemId);

    /** @return the observable number of lines for the badge */
    LiveData<Integer> getCartCount();

    /**
     * Recounts the lines in the background and updates {@link #getCartCount()}.
     *
     * @param userId the owner, or -1 for a guest (count 0)
     */
    void refreshCount(long userId);
}
