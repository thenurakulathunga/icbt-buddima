package com.printxpress.app.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.OrderRepository;
import com.printxpress.app.data.repository.SupportRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contact support form (S28, FR-5.3, TC-59/TC-60): optional related order, subject 5–80 and
 * message 10–1000 characters. The repository re-checks the same limits.
 *
 * @since 1.0
 */
public class ContactViewModel extends BaseFormViewModel {

    /** Field key: subject. */
    public static final String FIELD_SUBJECT = "subject";
    /** Field key: message. */
    public static final String FIELD_MESSAGE = "message";
    /** Subject length limits. */
    public static final int SUBJECT_MIN = 5;
    /** Subject maximum length. */
    public static final int SUBJECT_MAX = 80;
    /** Message minimum length. */
    public static final int MESSAGE_MIN = 10;
    /** Message maximum length. */
    public static final int MESSAGE_MAX = 1000;

    /** Session. */
    private final AuthRepository auth;
    /** Orders, for the related-order picker. */
    private final OrderRepository orders;
    /** Tickets. */
    private final SupportRepository support;
    /** Active orders. */
    private List<Order> active = Collections.emptyList();
    /** Past orders. */
    private List<Order> past = Collections.emptyList();
    /** The user's orders, newest first within each group. */
    private final MutableLiveData<List<Order>> orderChoices = new MutableLiveData<>();
    /** One-shot success. */
    private final MutableLiveData<Event<Long>> sent = new MutableLiveData<>();
    /** Selected related order id, or 0. */
    private long relatedOrderId;

    /**
     * Creates the ViewModel.
     *
     * @param auth    auth repository
     * @param orders  order repository
     * @param support support repository
     */
    public ContactViewModel(AuthRepository auth, OrderRepository orders, SupportRepository support) {
        this.auth = auth;
        this.orders = orders;
        this.support = support;
        validator.field(FIELD_SUBJECT, new RequiredRule(R.string.err_ticket_subject),
                new LengthRule(SUBJECT_MIN, SUBJECT_MAX, R.string.err_ticket_subject));
        validator.field(FIELD_MESSAGE, new RequiredRule(R.string.err_ticket_invalid),
                new LengthRule(MESSAGE_MIN, MESSAGE_MAX, R.string.err_ticket_invalid));
    }

    /** @return true when a ticket can be raised */
    public boolean isLoggedIn() {
        return auth.isLoggedIn();
    }

    /** Loads the user's orders for the picker. */
    public void loadOrders() {
        if (!auth.isLoggedIn()) {
            return;
        }
        long userId = auth.currentUserId();
        subscribe(orders.getActiveOrders(userId), r -> {
            if (r.isSuccess()) {
                active = r.getData();
                publishOrders();
            }
        });
        subscribe(orders.getPastOrders(userId), r -> {
            if (r.isSuccess()) {
                past = r.getData();
                publishOrders();
            }
        });
    }

    /** Merges active and past orders into one list. */
    private void publishOrders() {
        List<Order> all = new ArrayList<>(active);
        all.addAll(past);
        orderChoices.setValue(all);
    }

    /**
     * Records the related order.
     *
     * @param orderId the order, or 0 for none
     */
    public void setRelatedOrder(long orderId) {
        relatedOrderId = orderId;
    }

    /** @return the related order id, or 0 */
    public long getRelatedOrderId() {
        return relatedOrderId;
    }

    /** Validates and sends the ticket. */
    public void submit() {
        if (!validateAll()) {
            return;
        }
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(auth.currentUserId());
        ticket.setOrderId(relatedOrderId > 0 ? relatedOrderId : null);
        ticket.setSubject(value(FIELD_SUBJECT));
        ticket.setMessage(value(FIELD_MESSAGE));
        subscribe(support.createTicket(ticket), r -> {
            if (r.isSuccess()) {
                sent.setValue(new Event<>(r.getData()));
            } else if (r.isError()) {
                int error = r.getMessageRes();
                if (error == R.string.err_ticket_subject) {
                    showFieldError(FIELD_SUBJECT, error);
                } else if (error == R.string.err_ticket_invalid) {
                    showFieldError(FIELD_MESSAGE, error);
                } else {
                    postError(error);
                }
            }
        }, true);
    }

    /** @return the orders available for the picker */
    public LiveData<List<Order>> getOrderChoices() {
        return orderChoices;
    }

    /** @return fires once with the new ticket id */
    public LiveData<Event<Long>> getSent() {
        return sent;
    }
}
