package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * READY_FOR_PICKUP: waiting at the branch. Next: COMPLETED.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class ReadyForPickupState extends AbstractOrderState {

    /** Creates the state. */
    public ReadyForPickupState() {
        super(OrderStatus.READY_FOR_PICKUP);
    }

    /**
     * Moves to COMPLETED.
     *
     * @param fulfilment ignored
     * @return the completed state
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return OrderStateFactory.from(OrderStatus.COMPLETED);
    }
}
