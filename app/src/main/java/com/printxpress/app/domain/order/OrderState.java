package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * State pattern contract; each order status decides which transitions and actions are legal.
 * <p>
 * BR-7 lives here: only PENDING, CONFIRMED and PROCESSING allow cancel and reschedule. The UI,
 * repository and worker all ask the state instead of comparing statuses themselves.
 * </p>
 *
 * @since 1.0
 */
public interface OrderState {

    /** @return the status enum this state represents */
    OrderStatus getStatus();

    /** @return true when the customer may cancel in this state */
    boolean canCancel();

    /** @return true when the customer may reschedule in this state */
    boolean canReschedule();

    /** @return true when no further transition is possible */
    boolean isTerminal();

    /**
     * Resolves the next state in the workflow.
     *
     * @param fulfilment pickup or delivery, which decides the branch after PRINTING
     * @return the next state, or this state if terminal
     */
    OrderState next(FulfilmentType fulfilment);

    /**
     * Moves to CANCELLED.
     *
     * @return the cancelled state
     * @throws IllegalStateException when cancelling is not allowed in this state
     */
    OrderState cancel();
}
