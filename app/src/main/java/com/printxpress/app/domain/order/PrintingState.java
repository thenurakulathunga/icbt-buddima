package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * PRINTING: on the press; the order is locked. Next depends on fulfilment:
 * READY_FOR_PICKUP for pickup, OUT_FOR_DELIVERY for delivery (TC-50).
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class PrintingState extends AbstractOrderState {

    /** Creates the state. */
    public PrintingState() {
        super(OrderStatus.PRINTING);
    }

    /**
     * Branches on fulfilment.
     *
     * @param fulfilment pickup or delivery
     * @return ready-for-pickup or out-for-delivery
     */
    @Override
    public OrderState next(FulfilmentType fulfilment) {
        return OrderStateFactory.from(fulfilment == FulfilmentType.DELIVERY
                ? OrderStatus.OUT_FOR_DELIVERY : OrderStatus.READY_FOR_PICKUP);
    }
}
