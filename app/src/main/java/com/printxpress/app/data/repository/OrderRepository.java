package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.CheckoutRequest;
import com.printxpress.app.data.model.Order;

import java.time.LocalDate;
import java.util.List;

/**
 * Placing, tracking, cancelling, rescheduling and reordering orders (FR-2.12, 2.13, FR-3.x).
 * <p>Pattern role: Repository and Subject for {@link OrderEventListener} observers.</p>
 *
 * @since 1.0
 */
public interface OrderRepository {

    /**
     * Re-validates the checkout, recalculates prices and places the order in one transaction.
     *
     * @param request the checkout choices
     * @return the placed order with its number
     */
    LiveData<Resource<Order>> placeOrder(CheckoutRequest request);

    /**
     * Lists active orders.
     *
     * @param userId the owner
     * @return the orders
     */
    LiveData<Resource<List<Order>>> getActiveOrders(long userId);

    /**
     * Lists completed and cancelled orders.
     *
     * @param userId the owner
     * @return the orders
     */
    LiveData<Resource<List<Order>>> getPastOrders(long userId);

    /**
     * Loads an order with lines and history.
     *
     * @param orderId the order
     * @return the order
     */
    LiveData<Resource<Order>> getOrder(long orderId);

    /**
     * Cancels an order when its state allows (BR-7).
     *
     * @param orderId the order
     * @param reason  the reason
     * @return the updated order
     */
    LiveData<Resource<Order>> cancel(long orderId, String reason);

    /**
     * Reschedules an order when its state allows and the date is valid (BR-6, BR-7).
     *
     * @param orderId the order
     * @param date    the new date
     * @param slot    the new slot label
     * @return the updated order
     */
    LiveData<Resource<Order>> reschedule(long orderId, LocalDate date, String slot);

    /**
     * Copies a past order's lines back into the cart (FR-3.5).
     *
     * @param userId  the owner
     * @param orderId the order
     * @return the number of lines added
     */
    LiveData<Resource<Integer>> reorder(long userId, long orderId);

    /**
     * Advances an order one step on the calling thread, used by the progress worker.
     *
     * @param orderId the order
     * @return the updated order, or null when finished or missing
     */
    Order advanceBlocking(long orderId);

    /** @return unfinished orders across users, on the calling thread */
    List<Order> findUnfinishedBlocking();

    /**
     * Registers an observer of order events.
     *
     * @param listener the observer
     */
    void addListener(OrderEventListener listener);
}
