package com.printxpress.app.worker;

import android.content.Context;

import com.printxpress.app.R;
import com.printxpress.app.core.notification.NotificationHelper;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.model.enums.NotificationType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.data.repository.NotificationRepository;
import com.printxpress.app.data.repository.OrderEventListener;
import com.printxpress.app.data.repository.UserRepository;
import com.printxpress.app.domain.order.OrderStateFactory;
import com.printxpress.app.ui.components.StatusStyle;

/**
 * Reacts to order events: records an in-app notification, posts a system notification when the
 * customer allows it (FR-4.1, FR-4.2, FR-4.6), and keeps the progress simulation going.
 * <p>Pattern role: concrete Observer of {@code OrderRepository}.</p>
 *
 * @since 1.0
 */
public class OrderEventsHandler implements OrderEventListener {

    /** Application context for strings. */
    private final Context context;
    /** Notification centre. */
    private final NotificationRepository notifications;
    /** Profiles, for preferences. */
    private final UserRepository users;
    /** System notifications. */
    private final NotificationHelper helper;
    /** Progress simulation. */
    private final OrderProgressScheduler scheduler;
    /** Demo speed preference. */
    private final SessionManager session;

    /**
     * Creates the handler.
     *
     * @param context       any context
     * @param notifications notification repository
     * @param users         user repository
     * @param helper        notification helper
     * @param scheduler     progress scheduler
     * @param session       session manager
     */
    public OrderEventsHandler(Context context, NotificationRepository notifications, UserRepository users,
                              NotificationHelper helper, OrderProgressScheduler scheduler, SessionManager session) {
        this.context = context.getApplicationContext();
        this.notifications = notifications;
        this.users = users;
        this.helper = helper;
        this.scheduler = scheduler;
        this.session = session;
    }

    /**
     * Confirms the new order and starts the simulation.
     *
     * @param order the placed order
     */
    @Override
    public void onOrderPlaced(Order order) {
        notify(order, context.getString(R.string.notif_order_placed_title, order.getOrderNumber()),
                context.getString(R.string.notif_order_placed_msg));
        scheduler.scheduleNext(order.getOrderId(), session.isDemoSpeed());
    }

    /**
     * Announces the new status and schedules the next step unless finished.
     *
     * @param order     the order
     * @param oldStatus the previous status
     */
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus) {
        String status = context.getString(StatusStyle.of(order.getStatus()).getLabel());
        notify(order, context.getString(R.string.notif_status_title, order.getOrderNumber(), status), messageFor(order));
        if (OrderStateFactory.from(order.getStatus()).isTerminal()) {
            scheduler.cancel(order.getOrderId());
        } else {
            scheduler.scheduleNext(order.getOrderId(), session.isDemoSpeed());
        }
    }

    /**
     * Confirms the new schedule.
     *
     * @param order the order
     */
    @Override
    public void onRescheduled(Order order) {
        notify(order, context.getString(R.string.notif_rescheduled_title, order.getOrderNumber()),
                context.getString(R.string.notif_rescheduled_msg, DateTimeUtils.formatDisplay(order.getScheduledDate()),
                        order.getScheduledSlot()));
    }

    /**
     * Stores the in-app entry and posts the system notification when allowed.
     *
     * @param order   the order
     * @param title   the title
     * @param message the message
     */
    private void notify(Order order, String title, String message) {
        AppNotification n = new AppNotification();
        n.setUserId(order.getUserId());
        n.setOrderId(order.getOrderId());
        n.setType(NotificationType.ORDER);
        n.setTitle(title);
        n.setMessage(message);
        notifications.addBlocking(n);
        User user = users.findUserBlocking(order.getUserId());
        if (user != null && user.isNotifyOrders()) {
            helper.showOrder(order.getOrderId(), title, message);
        }
    }

    /**
     * Picks the message for a status.
     *
     * @param order the order
     * @return the message
     */
    private String messageFor(Order order) {
        switch (order.getStatus()) {
            case CONFIRMED:
                return context.getString(R.string.notif_msg_confirmed);
            case PROCESSING:
                return context.getString(R.string.notif_msg_processing);
            case PRINTING:
                return context.getString(R.string.notif_msg_printing);
            case READY_FOR_PICKUP:
                return context.getString(R.string.notif_msg_ready, order.getBranchName() == null
                        ? context.getString(R.string.branch_generic) : order.getBranchName());
            case OUT_FOR_DELIVERY:
                return context.getString(R.string.notif_msg_delivery);
            case COMPLETED:
                return context.getString(R.string.notif_msg_completed);
            case CANCELLED:
            default:
                return context.getString(R.string.notif_msg_cancelled);
        }
    }
}
