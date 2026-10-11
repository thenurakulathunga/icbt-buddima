package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * CANCELLED: cancelled by the customer. Terminal.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class CancelledState extends AbstractOrderState {

    /** Creates the state. */
    public CancelledState() {
        super(OrderStatus.CANCELLED);
    }

    /** @return true: no further transitions */
    @Override
    public boolean isTerminal() {
        return true;
    }
}
