package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.OrderStatus;

/**
 * Base for the states before printing starts, where cancel and reschedule are allowed (BR-7).
 * <p>Pattern role: abstract State.</p>
 *
 * @since 1.0
 */
public abstract class EditableOrderState extends AbstractOrderState {

    /**
     * Creates a state.
     *
     * @param status the represented status
     */
    protected EditableOrderState(OrderStatus status) {
        super(status);
    }

    /** @return true: printing has not started */
    @Override
    public boolean canCancel() {
        return true;
    }

    /** @return true: printing has not started */
    @Override
    public boolean canReschedule() {
        return true;
    }
}
