package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * OUT_FOR_DELIVERY: with the courier. Next: COMPLETED.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class OutForDeliveryState extends AbstractOrderState {

    /** Creates the state. */
    public OutForDeliveryState() {
        super(OrderStatus.OUT_FOR_DELIVERY);
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
