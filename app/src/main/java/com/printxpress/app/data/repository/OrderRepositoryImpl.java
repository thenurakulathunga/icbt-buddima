package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.dao.AddressDao;
import com.printxpress.app.data.dao.BranchDao;
import com.printxpress.app.data.dao.CartDao;
import com.printxpress.app.data.dao.OrderDao;
import com.printxpress.app.data.dao.ProductOptionDao;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.CheckoutRequest;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.OrderItem;
import com.printxpress.app.data.model.OrderItemOption;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.domain.fulfilment.FulfilmentStrategy;
import com.printxpress.app.domain.fulfilment.HomeDeliveryStrategy;
import com.printxpress.app.domain.fulfilment.PickupStrategy;
import com.printxpress.app.domain.order.OrderState;
import com.printxpress.app.domain.order.OrderStateFactory;
import com.printxpress.app.domain.pricing.LineQuote;
import com.printxpress.app.domain.pricing.PriceBreakdown;
import com.printxpress.app.domain.pricing.PriceCalculator;
import com.printxpress.app.domain.schedule.ScheduleCalculator;
import com.printxpress.app.domain.schedule.TimeSlot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SQLite-backed {@link OrderRepository}.
 * <p>
 * The checkout is fully re-validated here: cart not empty, artwork on every line (BR-9), branch or
 * address present (BR-8), date and slot allowed (BR-6) and promo still valid (BR-5). Prices are
 * recalculated by {@link PriceCalculator}. Only then is {@link OrderDao#insertOrderWithItems}
 * called, which commits everything in one transaction.
 * </p>
 *
 * @since 1.0
 */
public class OrderRepositoryImpl extends BaseRepository implements OrderRepository {

    /** Order access. */
    private final OrderDao orderDao;
    /** Cart access. */
    private final CartDao cartDao;
    /** Address access. */
    private final AddressDao addressDao;
    /** Branch access. */
    private final BranchDao branchDao;
    /** Option access, for reorder. */
    private final ProductOptionDao optionDao;
    /** Promo validation. */
    private final PromotionRepositoryImpl promotions;
    /** Pricing. */
    private final PriceCalculator calculator;
    /** Date rules. */
    private final ScheduleCalculator schedule;
    /** Registered observers. */
    private final List<OrderEventListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Creates the repository.
     *
     * @param executors  background executors
     * @param orderDao   order DAO
     * @param cartDao    cart DAO
     * @param addressDao address DAO
     * @param branchDao  branch DAO
     * @param optionDao  option DAO
     * @param promotions promotion repository used for code checks
     * @param calculator price calculator
     * @param schedule   date rules
     */
    public OrderRepositoryImpl(AppExecutors executors, OrderDao orderDao, CartDao cartDao, AddressDao addressDao,
                               BranchDao branchDao, ProductOptionDao optionDao, PromotionRepositoryImpl promotions, PriceCalculator calculator,
                               ScheduleCalculator schedule) {
        super(executors);
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.addressDao = addressDao;
        this.branchDao = branchDao;
        this.optionDao = optionDao;
        this.promotions = promotions;
        this.calculator = calculator;
        this.schedule = schedule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(OrderEventListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Order>> placeOrder(CheckoutRequest request) {
        return execute(() -> {
            List<CartItem> cart = cartDao.findByUser(request.getUserId());
            require(!cart.isEmpty(), R.string.err_cart_empty);
            for (CartItem item : cart) {
                require(item.getProduct() != null && item.getProduct().isActive(), R.string.err_product_unavailable);
                require(item.hasArtwork(), R.string.err_design_required);
            }
            FulfilmentStrategy fulfilment = resolveFulfilment(request);
            requireSchedule(fulfilment, maxTurnaround(cart), request.getDate(), request.getSlot());
            Promotion promotion = ValidationRule.isBlank(request.getPromoCode())
                    ? null : promotions.resolve(request.getUserId(), request.getPromoCode(), cart);
            PriceBreakdown price = calculator.calculate(cart, promotion, fulfilment);
            Order.Builder builder = new Order.Builder(request.getUserId())
                    .schedule(request.getDate(), request.getSlot())
                    .promotion(promotion == null ? null : promotion.getPromoId())
                    .items(toOrderItems(cart))
                    .notes(ValidationRule.isBlank(request.getNotes()) ? null : request.getNotes().trim());
            if (fulfilment.requiresAddress()) {
                builder.deliverTo(request.getAddressId(), fulfilment.deliveryFee());
            } else {
                builder.pickupAt(request.getBranchId());
            }
            Order order = builder.amounts(price.getSubtotal(), price.getPromoDiscount(), price.getDeliveryFee(),
                    price.getTotal()).build();
            long orderId = orderDao.insertOrderWithItems(order, true);
            Order placed = orderDao.findDetail(orderId);
            for (OrderEventListener listener : listeners) {
                listener.onOrderPlaced(placed);
            }
            return placed;
        });
    }

    /**
     * Builds the fulfilment strategy and checks BR-8.
     *
     * @param request the checkout choices
     * @return the strategy
     */
    private FulfilmentStrategy resolveFulfilment(CheckoutRequest request) {
        if (request.getFulfilmentType() == FulfilmentType.DELIVERY) {
            require(request.getAddressId() != null, R.string.err_address_required);
            Address address = addressDao.findById(request.getAddressId());
            require(address != null && address.getUserId() == request.getUserId(), R.string.err_address_required);
            return new HomeDeliveryStrategy(schedule, address.getDeliveryFee());
        }
        require(request.getBranchId() != null && branchDao.findById(request.getBranchId()) != null,
                R.string.err_branch_required);
        return new PickupStrategy(schedule);
    }

    /**
     * Checks BR-6 and the slot.
     *
     * @param fulfilment the fulfilment strategy
     * @param turnaround the longest turnaround in the cart
     * @param date       the chosen date
     * @param slot       the chosen slot label
     */
    private void requireSchedule(FulfilmentStrategy fulfilment, int turnaround, LocalDate date, String slot) {
        require(schedule.isAvailable(date, turnaround + fulfilment.extraLeadDays()), R.string.err_date_invalid);
        require(TimeSlot.fromLabel(slot) != null, R.string.err_slot_required);
    }

    /**
     * Returns the longest turnaround among cart lines (BR-6 uses the maximum).
     *
     * @param items the cart lines
     * @return the turnaround in days
     */
    static int maxTurnaround(List<CartItem> items) {
        int max = 0;
        for (CartItem item : items) {
            if (item.getProduct() != null) {
                max = Math.max(max, item.getProduct().getTurnaroundDays());
            }
        }
        return max;
    }

    /**
     * Converts cart lines into priced order lines with option snapshots.
     *
     * @param cart the cart lines
     * @return the order lines
     */
    private List<OrderItem> toOrderItems(List<CartItem> cart) {
        List<OrderItem> items = new ArrayList<>();
        for (CartItem c : cart) {
            LineQuote quote = calculator.quoteLine(c.getProduct(), c.getOptions(), c.getQuantity());
            OrderItem item = new OrderItem();
            item.setProductId(c.getProductId());
            item.setDesignId(c.getDesignId());
            item.setQuantity(c.getQuantity());
            item.setUnitPrice(quote.getUnitPrice());
            item.setLineTotal(quote.getLineTotal());
            item.setCustomText(c.getCustomText());
            item.setInstructions(c.getInstructions());
            List<OrderItemOption> options = new ArrayList<>();
            for (ProductOption option : c.getOptions()) {
                OrderItemOption snapshot = new OrderItemOption();
                snapshot.setOptionId(option.getOptionId());
                snapshot.setPriceModifierSnapshot(option.getPriceModifier());
                options.add(snapshot);
            }
            item.setOptions(options);
            items.add(item);
        }
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Order>>> getActiveOrders(long userId) {
        return execute(() -> orderDao.findActive(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Order>>> getPastOrders(long userId) {
        return execute(() -> orderDao.findPast(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Order>> getOrder(long orderId) {
        return execute(() -> existing(orderId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Order>> cancel(long orderId, String reason) {
        return execute(() -> {
            Order order = existing(orderId);
            OrderState state = OrderStateFactory.from(order.getStatus());
            require(state.canCancel(), R.string.err_order_locked);
            require(!ValidationRule.isBlank(reason), R.string.err_reason_required);
            OrderStatus old = order.getStatus();
            orderDao.cancel(orderId, reason.trim());
            Order updated = existing(orderId);
            for (OrderEventListener listener : listeners) {
                listener.onStatusChanged(updated, old);
            }
            return updated;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Order>> reschedule(long orderId, LocalDate date, String slot) {
        return execute(() -> {
            Order order = existing(orderId);
            require(OrderStateFactory.from(order.getStatus()).canReschedule(), R.string.err_order_locked);
            int turnaround = 0;
            for (OrderItem item : order.getItems()) {
                turnaround = Math.max(turnaround, item.getTurnaroundDays());
            }
            int extra = order.getFulfilmentType() == FulfilmentType.DELIVERY
                    ? new HomeDeliveryStrategy(schedule, null).extraLeadDays() : 0;
            require(schedule.isAvailable(date, turnaround + extra), R.string.err_date_invalid);
            require(TimeSlot.fromLabel(slot) != null, R.string.err_slot_required);
            orderDao.reschedule(orderId, date, slot, DateTimeUtils.toDbDate(date) + " " + slot);
            Order updated = existing(orderId);
            for (OrderEventListener listener : listeners) {
                listener.onRescheduled(updated);
            }
            return updated;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Integer>> reorder(long userId, long orderId) {
        return execute(() -> {
            Order order = existing(orderId);
            require(order.getUserId() == userId, R.string.err_order_not_found);
            int added = 0;
            for (OrderItem line : order.getItems()) {
                List<Long> optionIds = new ArrayList<>();
                for (OrderItemOption option : line.getOptions()) {
                    optionIds.add(option.getOptionId());
                }
                List<ProductOption> options = optionIds.isEmpty() ? new ArrayList<>() : optionDao.findByIds(optionIds);
                CartItem item = new CartItem.Builder(userId, line.getProductId()).quantity(line.getQuantity())
                        .design(line.getDesignId()).customText(line.getCustomText())
                        .instructions(line.getInstructions()).options(options).build();
                if (item.hasArtwork()) {
                    cartDao.save(item);
                    added++;
                }
            }
            return added;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order advanceBlocking(long orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return null;
        }
        OrderState state = OrderStateFactory.from(order.getStatus());
        if (state.isTerminal()) {
            return null;
        }
        OrderStatus old = order.getStatus();
        OrderStatus next = state.next(order.getFulfilmentType()).getStatus();
        orderDao.updateStatus(orderId, next, null);
        Order updated = orderDao.findDetail(orderId);
        for (OrderEventListener listener : listeners) {
            listener.onStatusChanged(updated, old);
        }
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Order> findUnfinishedBlocking() {
        return orderDao.findAllUnfinished();
    }

    /**
     * Loads a full order or fails with "not found".
     *
     * @param orderId the order
     * @return the order
     */
    private Order existing(long orderId) {
        Order order = orderDao.findDetail(orderId);
        require(order != null, R.string.err_order_not_found);
        return order;
    }
}
