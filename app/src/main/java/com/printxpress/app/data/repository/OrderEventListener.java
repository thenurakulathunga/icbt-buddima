package com.printxpress.app.data.repository;

import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * Receives order lifecycle events published by {@link OrderRepository}.
 * <p>
 * Pattern role: Observer. The repository does not know who listens: the in-app notification
 * centre, system notifications and the progress simulation all subscribe through this interface.
 * Callbacks run on a background thread.
 * </p>
 *
 * @since 1.0
 */
public interface OrderEventListener {

    /**
     * Called after an order has been committed.
     *
     * @param order the placed order
     */
    void onOrderPlaced(Order order);

    /**
     * Called after the status changed (including cancellation).
     *
     * @param order     the order with its new status
     * @param oldStatus the previous status
     */
    void onStatusChanged(Order order, OrderStatus oldStatus);

    /**
     * Called after the schedule changed.
     *
     * @param order the rescheduled order
     */
    void onRescheduled(Order order);
}
