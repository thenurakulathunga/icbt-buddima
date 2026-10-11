package com.printxpress.app.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.OrderItem;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.data.repository.OrderRepository;
import com.printxpress.app.domain.order.OrderState;
import com.printxpress.app.domain.order.OrderStateFactory;
import com.printxpress.app.domain.schedule.ScheduleCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

/**
 * Order detail state (S18–S20): tracking, reschedule (FR-3.3), cancel (FR-3.4) and reorder
 * (FR-3.5). Which actions are allowed comes from {@link OrderState} (BR-7). Shared with the
 * reschedule and cancel sheets.
 *
 * @since 1.0
 */
public class OrderDetailViewModel extends BaseViewModel {

    /** Days offered in the reschedule sheet. */
    private static final int WINDOW = 14;

    /** Session. */
    private final AuthRepository auth;
    /** Orders. */
    private final OrderRepository orders;
    /** Cart, for the reorder badge. */
    private final CartRepository cart;
    /** Date rules. */
    private final ScheduleCalculator schedule;
    /** The order. */
    private final MutableLiveData<Resource<Order>> order = new MutableLiveData<>();
    /** One-shot: a sheet finished successfully. */
    private final MutableLiveData<Event<Integer>> done = new MutableLiveData<>();
    /** Order id shown. */
    private long orderId = -1;

    /**
     * Creates the ViewModel.
     *
     * @param auth     auth repository
     * @param orders   order repository
     * @param cart     cart repository
     * @param schedule date rules
     */
    public OrderDetailViewModel(AuthRepository auth, OrderRepository orders, CartRepository cart,
                                ScheduleCalculator schedule) {
        this.auth = auth;
        this.orders = orders;
        this.cart = cart;
        this.schedule = schedule;
    }

    /**
     * Shows an order.
     *
     * @param id the order id
     */
    public void start(long id) {
        orderId = id;
        refresh();
    }

    /** Reloads the order (it progresses in the background). */
    public void refresh() {
        if (orderId > 0) {
            subscribe(orders.getOrder(orderId), r -> {
                if (!r.isLoading() || order.getValue() == null) {
                    order.setValue(r);
                }
            });
        }
    }

    /** @return the current state machine node, or PENDING while loading */
    public OrderState state() {
        Order o = current();
        return OrderStateFactory.from(o == null ? null : o.getStatus());
    }

    /** @return the loaded order, or null */
    public Order current() {
        Resource<Order> r = order.getValue();
        return r != null && r.isSuccess() ? r.getData() : null;
    }

    /** @return the lead time for rescheduling: longest turnaround plus courier day */
    private int lead() {
        Order o = current();
        if (o == null) {
            return 0;
        }
        int max = 0;
        for (OrderItem item : o.getItems()) {
            max = Math.max(max, item.getTurnaroundDays());
        }
        return max + (o.getFulfilmentType() == FulfilmentType.DELIVERY ? 1 : 0);
    }

    /** @return days for the reschedule strip */
    public List<LocalDate> rescheduleDays() {
        return schedule.window(lead(), WINDOW);
    }

    /** @return the availability rule for the reschedule strip */
    public Predicate<LocalDate> availability() {
        int lead = lead();
        return d -> schedule.isAvailable(d, lead);
    }

    /**
     * Reschedules the order; the repository re-checks BR-6 and BR-7.
     *
     * @param date the new date
     * @param slot the new slot
     */
    public void reschedule(LocalDate date, String slot) {
        subscribe(orders.reschedule(orderId, date, slot), r -> {
            if (r.isSuccess()) {
                order.setValue(r);
                postMessage(R.string.rescheduled);
                done.setValue(new Event<>(R.string.rescheduled));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /**
     * Cancels the order with a reason; the repository re-checks BR-7.
     *
     * @param reason the reason text
     */
    public void cancel(String reason) {
        subscribe(orders.cancel(orderId, reason), r -> {
            if (r.isSuccess()) {
                order.setValue(r);
                postMessage(R.string.order_cancelled);
                done.setValue(new Event<>(R.string.order_cancelled));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /** Copies the order's lines into the cart (FR-3.5). */
    public void reorder() {
        subscribe(orders.reorder(auth.currentUserId(), orderId), r -> {
            if (r.isSuccess()) {
                cart.refreshCount(auth.currentUserId());
                done.setValue(new Event<>(R.string.action_reorder));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /** @return the order */
    public LiveData<Resource<Order>> getOrder() {
        return order;
    }

    /** @return one-shot completion events (message resource of the action) */
    public LiveData<Event<Integer>> getDone() {
        return done;
    }
}
