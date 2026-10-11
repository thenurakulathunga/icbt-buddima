package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * COMPLETED: collected or delivered. Terminal.
 * <p>Pattern role: Concrete State.</p>
 *
 * @since 1.0
 */
public final class CompletedState extends AbstractOrderState {

    /** Creates the state. */
    public CompletedState() {
        super(OrderStatus.COMPLETED);
    }

    /** @return true: no further transitions */
    @Override
    public boolean isTerminal() {
        return true;
    }
}
