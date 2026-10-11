package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * Default behaviour for order states: nothing is allowed and the state does not move.
 * Concrete states override only what they permit.
 * <p>Pattern role: abstract State.</p>
 *
 * @since 1.0
 */
public abstract class AbstractOrderState implements OrderState {

    /** The status this state represents. */
    private final OrderStatus status;

    /**
     * Creates a state.
     *
     * @param status the represented status
     */
    protected AbstractOrderState(OrderStatus status) {
        this.status = status;
    }

    /** @return the represented status */
    @Override
    public final OrderStatus getStatus() {
        return status;
    }

    /** @return false by default */
    @Override
    public boolean canCancel() {
        return false;
    }

    /** @return false by default */
    @Override
    public boolean canReschedule() {
        return false;
    }

    /** @return false by default */
    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * Stays in this state by default.
     *
     * @param fulfilment ignored
     * @return this state
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return this;
    }

    /**
     * Cancels when allowed.
     *
     * @return the cancelled state
     * @throws IllegalStateException when {@link #canCancel()} is false
     */
    @Override
    public OrderState cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("Cannot cancel an order in " + status);
        }
        return OrderStateFactory.from(OrderStatus.CANCELLED);
    }

    /** @return the status name */
    @Override
    public String toString() {
        return status.name();
    }
}
