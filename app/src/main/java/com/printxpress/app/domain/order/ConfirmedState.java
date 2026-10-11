package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * CONFIRMED: accepted by the shop. Next: PROCESSING.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class ConfirmedState extends EditableOrderState {

    /** Creates the state. */
    public ConfirmedState() {
        super(OrderStatus.CONFIRMED);
    }

    /**
     * Moves to PROCESSING.
     *
     * @param fulfilment ignored
     * @return the processing state
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return OrderStateFactory.from(OrderStatus.PROCESSING);
    }
}
