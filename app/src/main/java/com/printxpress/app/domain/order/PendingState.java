package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * PENDING: placed and waiting for confirmation. Next: CONFIRMED.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class PendingState extends EditableOrderState {

    /** Creates the state. */
    public PendingState() {
        super(OrderStatus.PENDING);
    }

    /**
     * Moves to CONFIRMED.
     *
     * @param fulfilment ignored
     * @return the confirmed state
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return OrderStateFactory.from(OrderStatus.CONFIRMED);
    }
}
