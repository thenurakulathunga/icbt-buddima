package com.printxpress.app.domain.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the State pattern that owns BR-7 (cancel/reschedule only before PRINTING) and
 * the pickup/delivery paths (TC-46, TC-49, TC-50).
 *
 * @since 1.0
 */
public class OrderStateTest {

    /** Pending, Confirmed and Processing are editable; later states are not. */
    @Test
    public void editableOnlyBeforePrinting() {
        for (OrderStatus s : Arrays.asList(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PROCESSING)) {
            OrderState state = OrderStateFactory.from(s);
            assertTrue(s.name(), state.canCancel());
            assertTrue(s.name(), state.canReschedule());
        }
        for (OrderStatus s : Arrays.asList(OrderStatus.PRINTING, OrderStatus.READY_FOR_PICKUP,
                OrderStatus.OUT_FOR_DELIVERY, OrderStatus.COMPLETED, OrderStatus.CANCELLED)) {
            OrderState state = OrderStateFactory.from(s);
            assertFalse(s.name(), state.canCancel());
            assertFalse(s.name(), state.canReschedule());
        }
    }

    /** A pickup order goes Printing → Ready for pickup → Completed (TC-50). */
    @Test
    public void pickupPath() {
        List<OrderStatus> expected = Arrays.asList(OrderStatus.PENDING, OrderStatus.CONFIRMED,
                OrderStatus.PROCESSING, OrderStatus.PRINTING, OrderStatus.READY_FOR_PICKUP, OrderStatus.COMPLETED);
        assertEquals(expected, walk(FulfilmentType.PICKUP));
        assertEquals(expected, OrderStateFactory.pathFor(FulfilmentType.PICKUP));
    }

    /** A delivery order goes Printing → Out for delivery → Completed. */
    @Test
    public void deliveryPath() {
        List<OrderStatus> path = walk(FulfilmentType.DELIVERY);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, path.get(4));
        assertEquals(OrderStatus.COMPLETED, path.get(5));
        assertEquals(path, OrderStateFactory.pathFor(FulfilmentType.DELIVERY));
    }

    /** Cancelling an editable order ends in CANCELLED, which is terminal. */
    @Test
    public void cancelFromConfirmed() {
        OrderState cancelled = OrderStateFactory.from(OrderStatus.CONFIRMED).cancel();
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertTrue(cancelled.isTerminal());
    }

    /** Cancelling a printing order is refused (TC-46 business rule). */
    @Test(expected = IllegalStateException.class)
    public void cancelWhilePrintingIsRefused() {
        OrderStateFactory.from(OrderStatus.PRINTING).cancel();
    }

    /** Completed and cancelled are terminal: advancing leaves them unchanged (TC-49). */
    @Test
    public void terminalStatesDoNotAdvance() {
        for (OrderStatus s : Arrays.asList(OrderStatus.COMPLETED, OrderStatus.CANCELLED)) {
            OrderState state = OrderStateFactory.from(s);
            assertTrue(state.isTerminal());
            assertEquals(s, state.next(FulfilmentType.PICKUP).getStatus());
        }
    }

    /**
     * Advances from PENDING until a terminal state.
     *
     * @param fulfilment the fulfilment type
     * @return the visited statuses
     */
    private static List<OrderStatus> walk(FulfilmentType fulfilment) {
        List<OrderStatus> visited = new ArrayList<>();
        OrderState state = OrderStateFactory.from(OrderStatus.PENDING);
        visited.add(state.getStatus());
        while (!state.isTerminal()) {
            state = state.next(fulfilment);
            visited.add(state.getStatus());
        }
        return visited;
    }
}
