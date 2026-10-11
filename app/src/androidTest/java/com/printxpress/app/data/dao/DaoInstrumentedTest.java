package com.printxpress.app.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.util.OrderNumberGenerator;
import com.printxpress.app.data.db.DatabaseHelper;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.OrderItem;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.data.model.enums.PaymentMethod;
import com.printxpress.app.data.model.enums.TicketStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Instrumented DAO tests against a fresh, seeded in-memory SQLite database (T-091): constraints,
 * parameterised look-ups, the single-transaction order insert, status history and cart clearing.
 *
 * @since 1.0
 */
@RunWith(AndroidJUnit4.class)
public class DaoInstrumentedTest {

    /** Seeded product id (Standard Business Card). */
    private static final long PRODUCT_ID = 1L;
    /** Seeded branch id. */
    private static final long BRANCH_ID = 1L;

    /** In-memory database. */
    private DatabaseHelper db;
    /** Users. */
    private UserDao users;
    /** Orders. */
    private OrderDao orders;
    /** Cart. */
    private CartDao cart;
    /** Tickets. */
    private SupportTicketDao tickets;
    /** The test customer. */
    private long userId;

    /** Creates the database and one customer. */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = DatabaseHelper.createInMemory(context);
        users = new UserDao(db);
        ProductDao products = new ProductDao(db);
        orders = new OrderDao(db);
        cart = new CartDao(db, products, new DesignDao(db));
        tickets = new SupportTicketDao(db);
        userId = users.insert(user("nimal@example.lk", "0771234567"));
    }

    /** Closes the database. */
    @After
    public void tearDown() {
        db.close();
    }

    /**
     * Builds a user.
     *
     * @param email the email
     * @param phone the phone
     * @return the user
     */
    private static User user(String email, String phone) {
        User u = new User();
        u.setFullName("Nimal Perera");
        u.setEmail(email);
        u.setPhone(phone);
        u.setPasswordHash("00");
        u.setPasswordSalt("00");
        u.setNotifyOrders(true);
        u.setNotifyPromos(true);
        return u;
    }

    /** Users are found by email or phone; duplicates are detected and rejected by UNIQUE. */
    @Test
    public void userDao_lookupAndUniqueness() {
        assertTrue(userId > 0);
        assertEquals(userId, users.findByIdentifier("nimal@example.lk").getUserId());
        assertEquals(userId, users.findByIdentifier("0771234567").getUserId());
        assertNull(users.findByIdentifier("x' OR '1'='1"));
        assertTrue(users.isEmailTaken("nimal@example.lk", 0));
        assertFalse(users.isEmailTaken("nimal@example.lk", userId));
        assertTrue(users.isPhoneTaken("0771234567", 0));
        try {
            users.insert(user("nimal@example.lk", "0779999999"));
            throw new AssertionError("duplicate email accepted");
        } catch (SQLiteConstraintException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    /** Preferences are persisted. */
    @Test
    public void userDao_updatePreferences() {
        assertTrue(users.updatePreferences(userId, true, false));
        User u = users.findById(userId);
        assertTrue(u.isNotifyOrders());
        assertFalse(u.isNotifyPromos());
    }

    /** Cart lines are saved, counted and cleared per user. */
    @Test
    public void cartDao_saveCountClear() {
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(PRODUCT_ID);
        item.setQuantity(100);
        item.setCustomText("Nimal Perera");
        assertTrue(cart.save(item) > 0);
        assertEquals(1, cart.count(userId));
        assertTrue(cart.updateQuantity(userId, item.getCartItemId(), 500));
        assertEquals(500, cart.findById(item.getCartItemId()).getQuantity());
        cart.clear(userId);
        assertEquals(0, cart.count(userId));
    }

    /** Placing an order numbers it, records PENDING history and clears the cart in one transaction. */
    @Test
    public void orderDao_insertWithItems() {
        cartDaoAddLine();
        long orderId = orders.insertOrderWithItems(pickupOrder(), true);
        Order saved = orders.findDetail(orderId);
        assertTrue(OrderNumberGenerator.isValid(saved.getOrderNumber()));
        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(1, saved.getItems().size());
        assertEquals(1, saved.getHistory().size());
        assertEquals(0, cart.count(userId));
        assertEquals(1, orders.findActive(userId).size());
        assertEquals(1, orders.countOrders(userId));
    }

    /** Status changes append history; cancelling moves the order to Past. */
    @Test
    public void orderDao_statusAndCancel() {
        long orderId = orders.insertOrderWithItems(pickupOrder(), false);
        assertTrue(orders.updateStatus(orderId, OrderStatus.CONFIRMED, null));
        assertTrue(orders.cancel(orderId, "Ordered by mistake"));
        Order saved = orders.findDetail(orderId);
        assertEquals(OrderStatus.CANCELLED, saved.getStatus());
        assertEquals("Ordered by mistake", saved.getCancelReason());
        assertEquals(3, saved.getHistory().size());
        assertTrue(orders.findActive(userId).isEmpty());
        assertEquals(1, orders.findPast(userId).size());
    }

    /** Tickets are stored and listed per user. */
    @Test
    public void ticketDao_insertAndList() {
        SupportTicket t = new SupportTicket();
        t.setUserId(userId);
        t.setSubject("Flyer colour question");
        t.setMessage("Will the blue print darker than on screen?");
        t.setStatus(TicketStatus.OPEN);
        assertTrue(tickets.insert(t) > 0);
        List<SupportTicket> list = tickets.findByUser(userId);
        assertEquals(1, list.size());
        assertEquals(TicketStatus.OPEN, list.get(0).getStatus());
    }

    /** Adds one cart line for the test user. */
    private void cartDaoAddLine() {
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(PRODUCT_ID);
        item.setQuantity(100);
        item.setCustomText("Nimal Perera");
        cart.save(item);
    }

    /**
     * Builds a pickup order with one line.
     *
     * @return the order
     */
    private Order pickupOrder() {
        OrderItem item = new OrderItem();
        item.setProductId(PRODUCT_ID);
        item.setQuantity(100);
        item.setUnitPrice(Money.of("15.00"));
        item.setLineTotal(Money.of("1500.00"));
        item.setCustomText("Nimal Perera");
        Order order = new Order();
        order.setUserId(userId);
        order.setBranchId(BRANCH_ID);
        order.setFulfilmentType(FulfilmentType.PICKUP);
        order.setPaymentMethod(PaymentMethod.PAY_AT_PICKUP);
        order.setScheduledDate(LocalDate.now().plusDays(3));
        order.setScheduledSlot("09:00 - 12:00");
        order.setSubtotal(Money.of("1500.00"));
        order.setDiscount(Money.ZERO);
        order.setDeliveryFee(Money.ZERO);
        order.setTotal(Money.of("1500.00"));
        order.setItems(Collections.singletonList(item));
        return order;
    }
}
