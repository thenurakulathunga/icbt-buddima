package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * PROCESSING: artwork is being checked. Last state that allows cancel/reschedule. Next: PRINTING.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class ProcessingState extends EditableOrderState {

    /** Creates the state. */
    public ProcessingState() {
        super(OrderStatus.PROCESSING);
    }

    /**
     * Moves to PRINTING.
     *
     * @param fulfilment ignored
     * @return the printing state
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return OrderStateFactory.from(OrderStatus.PRINTING);
    }
}
