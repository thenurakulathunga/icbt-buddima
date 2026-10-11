package com.printxpress.app.domain.order;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Creates {@link OrderState} objects from stored statuses and describes the workflow path.
 * <p>
 * Pattern role: Factory. States hold no per-order data, so one shared instance per status is
 * reused.
 * </p>
 *
 * @since 1.0
 */
public final class OrderStateFactory {

    /** One shared state per status. */
    private static final Map<OrderStatus, OrderState> STATES = new EnumMap<>(OrderStatus.class);

    static {
        STATES.put(OrderStatus.PENDING, new PendingState());
        STATES.put(OrderStatus.CONFIRMED, new ConfirmedState());
        STATES.put(OrderStatus.PROCESSING, new ProcessingState());
        STATES.put(OrderStatus.PRINTING, new PrintingState());
        STATES.put(OrderStatus.READY_FOR_PICKUP, new ReadyForPickupState());
        STATES.put(OrderStatus.OUT_FOR_DELIVERY, new OutForDeliveryState());
        STATES.put(OrderStatus.COMPLETED, new CompletedState());
        STATES.put(OrderStatus.CANCELLED, new CancelledState());
    }

    /** Hidden constructor: static factory. */
    private OrderStateFactory() {
    }

    /**
     * Returns the state for a status.
     *
     * @param status the stored status; null is treated as PENDING
     * @return the state
     */
    public static OrderState from(OrderStatus status) {
        return STATES.get(status == null ? OrderStatus.PENDING : status);
    }

    /**
     * Lists the happy-path steps for a fulfilment type, used by the order timeline (FR-3.2).
     *
     * @param fulfilment pickup or delivery
     * @return the statuses from PENDING to COMPLETED
     */
    public static List<OrderStatus> pathFor(FulfilmentType fulfilment) {
        OrderStatus handover = fulfilment == FulfilmentType.DELIVERY
                ? OrderStatus.OUT_FOR_DELIVERY : OrderStatus.READY_FOR_PICKUP;
        return Collections.unmodifiableList(Arrays.asList(OrderStatus.PENDING, OrderStatus.CONFIRMED,
                OrderStatus.PROCESSING, OrderStatus.PRINTING, handover, OrderStatus.COMPLETED));
    }
}
