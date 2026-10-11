package com.printxpress.app.ui.checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.Branch;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.CheckoutRequest;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.repository.AddressRepository;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.data.repository.OrderRepository;
import com.printxpress.app.data.repository.PromotionRepository;
import com.printxpress.app.domain.fulfilment.FulfilmentStrategy;
import com.printxpress.app.domain.fulfilment.HomeDeliveryStrategy;
import com.printxpress.app.domain.fulfilment.PickupStrategy;
import com.printxpress.app.domain.pricing.PriceBreakdown;
import com.printxpress.app.domain.pricing.PriceCalculator;
import com.printxpress.app.domain.schedule.ScheduleCalculator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Shared state of checkout (S14) and review (S15): fulfilment choice with branch or address
 * (FR-2.9, BR-8), date and slot (FR-2.10, BR-6), promo code (FR-2.11, BR-5), payment (FR-2.12) and
 * placing the order (FR-2.13). Scoped to the {@code nav_checkout} graph.
 *
 * @since 1.0
 */
public class CheckoutViewModel extends BaseViewModel {

    /** Days shown in the date strip. */
    private static final int WINDOW = 14;

    /** Session. */
    private final AuthRepository auth;
    /** Cart. */
    private final CartRepository cart;
    /** Addresses and branches. */
    private final AddressRepository addresses;
    /** Promotions. */
    private final PromotionRepository promotions;
    /** Orders. */
    private final OrderRepository orders;
    /** Pricing. */
    private final PriceCalculator calculator;
    /** Date rules. */
    private final ScheduleCalculator schedule;

    /** Cart lines. */
    private final MutableLiveData<Resource<List<CartItem>>> items = new MutableLiveData<>();
    /** Branches. */
    private final MutableLiveData<Resource<List<Branch>>> branches = new MutableLiveData<>();
    /** Addresses. */
    private final MutableLiveData<Resource<List<Address>>> addressList = new MutableLiveData<>();
    /** Pickup or delivery. */
    private final MutableLiveData<FulfilmentType> fulfilment = new MutableLiveData<>(FulfilmentType.PICKUP);
    /** Chosen branch. */
    private final MutableLiveData<Branch> branch = new MutableLiveData<>();
    /** Chosen address. */
    private final MutableLiveData<Address> address = new MutableLiveData<>();
    /** Days for the date strip. */
    private final MutableLiveData<List<LocalDate>> days = new MutableLiveData<>(Collections.emptyList());
    /** Applied promotion. */
    private final MutableLiveData<Promotion> promotion = new MutableLiveData<>();
    /** Promo error, 0 when none. */
    private final MutableLiveData<Integer> promoError = new MutableLiveData<>(0);
    /** Price breakdown. */
    private final MutableLiveData<PriceBreakdown> price = new MutableLiveData<>(PriceBreakdown.EMPTY);
    /** Step errors: branch/address, date, slot (0 when none). */
    private final MutableLiveData<int[]> stepErrors = new MutableLiveData<>(new int[]{0, 0, 0});
    /** One-shot: move to review. */
    private final MutableLiveData<Event<Boolean>> toReview = new MutableLiveData<>();
    /** One-shot: order placed. */
    private final MutableLiveData<Event<Order>> placed = new MutableLiveData<>();

    /** Chosen date. */
    private LocalDate date;
    /** Chosen slot. */
    private String slot;
    /** Longest turnaround in the cart. */
    private int turnaround;
    /** Optional notes. */
    private String notes = "";
    /** Whether data was loaded. */
    private boolean started;

    /**
     * Creates the ViewModel.
     *
     * @param auth       auth repository
     * @param cart       cart repository
     * @param addresses  address repository
     * @param promotions promotion repository
     * @param orders     order repository
     * @param calculator price calculator
     * @param schedule   date rules
     */
    public CheckoutViewModel(AuthRepository auth, CartRepository cart, AddressRepository addresses,
                             PromotionRepository promotions, OrderRepository orders, PriceCalculator calculator,
                             ScheduleCalculator schedule) {
        this.auth = auth;
        this.cart = cart;
        this.addresses = addresses;
        this.promotions = promotions;
        this.orders = orders;
        this.calculator = calculator;
        this.schedule = schedule;
    }

    /** Loads cart, branches and addresses once. */
    public void start() {
        if (started) {
            return;
        }
        started = true;
        subscribe(cart.getCart(auth.currentUserId()), r -> {
            items.setValue(r);
            if (r.isSuccess()) {
                turnaround = 0;
                for (CartItem item : r.getData()) {
                    turnaround = Math.max(turnaround, item.getProduct().getTurnaroundDays());
                }
                refreshSchedule();
            }
        });
        load(addresses.getBranches(), branches);
        reloadAddresses();
    }

    /** Reloads addresses, e.g. after adding one; keeps or picks the default. */
    public void reloadAddresses() {
        subscribe(addresses.getAddresses(auth.currentUserId()), r -> {
            addressList.setValue(r);
            if (r.isSuccess() && !r.getData().isEmpty()) {
                Address current = address.getValue();
                Address pick = r.getData().get(0);
                for (Address a : r.getData()) {
                    if (current != null && a.getAddressId() == current.getAddressId()) {
                        pick = a;
                    }
                }
                address.setValue(pick);
                reprice();
            }
        });
    }

    /**
     * Chooses pickup or delivery.
     *
     * @param type the fulfilment type
     */
    public void setFulfilment(FulfilmentType type) {
        fulfilment.setValue(type);
        refreshSchedule();
        clearStepErrors();
    }

    /**
     * Chooses a branch.
     *
     * @param b the branch
     */
    public void selectBranch(Branch b) {
        branch.setValue(b);
        clearStepErrors();
    }

    /**
     * Chooses an address.
     *
     * @param a the address
     */
    public void selectAddress(Address a) {
        address.setValue(a);
        reprice();
        clearStepErrors();
    }

    /**
     * Records the date and slot.
     *
     * @param d the date, may be null
     * @param s the slot label, may be null
     */
    public void setSchedule(LocalDate d, String s) {
        date = d;
        slot = s;
        clearStepErrors();
    }

    /** Rebuilds the date strip for the current lead time and keeps a still-valid date. */
    private void refreshSchedule() {
        int lead = lead();
        if (date == null || !schedule.isAvailable(date, lead)) {
            date = schedule.earliestDate(lead);
        }
        days.setValue(schedule.window(lead, WINDOW));
        reprice();
    }

    /** @return turnaround plus the fulfilment's extra days */
    private int lead() {
        return turnaround + strategy().extraLeadDays();
    }

    /** @return the current fulfilment strategy */
    private FulfilmentStrategy strategy() {
        if (fulfilment.getValue() == FulfilmentType.DELIVERY) {
            Address a = address.getValue();
            return new HomeDeliveryStrategy(schedule, a == null ? null : a.getDeliveryFee());
        }
        return new PickupStrategy(schedule);
    }

    /** @return the date availability rule for the strip */
    public Predicate<LocalDate> availability() {
        int lead = lead();
        return d -> schedule.isAvailable(d, lead);
    }

    /** Validates the fulfilment step and moves to review. */
    public void continueToReview() {
        int where = 0;
        if (fulfilment.getValue() == FulfilmentType.DELIVERY && address.getValue() == null) {
            where = R.string.err_address_required;
        } else if (fulfilment.getValue() == FulfilmentType.PICKUP && branch.getValue() == null) {
            where = R.string.err_branch_required;
        }
        int dateError = date == null || !schedule.isAvailable(date, lead()) ? R.string.err_date_invalid : 0;
        int slotError = slot == null ? R.string.err_slot_required : 0;
        stepErrors.setValue(new int[]{where, dateError, slotError});
        if (where == 0 && dateError == 0 && slotError == 0) {
            toReview.setValue(new Event<>(true));
        }
    }

    /** Clears the fulfilment step errors. */
    private void clearStepErrors() {
        stepErrors.setValue(new int[]{0, 0, 0});
    }

    /**
     * Checks and applies a promo code (FR-2.11).
     *
     * @param code the typed code
     */
    public void applyPromo(String code) {
        if (ValidationRule.isBlank(code)) {
            promoError.setValue(R.string.err_promo_invalid);
            return;
        }
        List<CartItem> lines = currentItems();
        subscribe(promotions.applyCode(auth.currentUserId(), code, lines), r -> {
            if (r.isSuccess()) {
                promotion.setValue(r.getData());
                promoError.setValue(0);
                reprice();
                postMessage(R.string.promo_applied_generic);
            } else if (r.isError()) {
                promotion.setValue(null);
                promoError.setValue(r.getMessageRes());
                reprice();
            }
        }, true);
    }

    /** Removes the applied promo. */
    public void removePromo() {
        promotion.setValue(null);
        promoError.setValue(0);
        reprice();
    }

    /**
     * Stores optional notes.
     *
     * @param text the notes
     */
    public void setNotes(String text) {
        notes = text == null ? "" : text;
    }

    /** Recomputes the price breakdown. */
    private void reprice() {
        price.setValue(calculator.calculate(currentItems(), promotion.getValue(), strategy()));
    }

    /** Places the order; the repository re-validates and recalculates everything. */
    public void placeOrder() {
        FulfilmentType type = fulfilment.getValue();
        Promotion promo = promotion.getValue();
        CheckoutRequest request = new CheckoutRequest(auth.currentUserId(), type,
                branch.getValue() == null ? null : branch.getValue().getBranchId(),
                address.getValue() == null ? null : address.getValue().getAddressId(),
                date, slot, promo == null ? null : promo.getPromoCode(), notes);
        subscribe(orders.placeOrder(request), r -> {
            if (r.isSuccess()) {
                cart.refreshCount(auth.currentUserId());
                placed.setValue(new Event<>(r.getData()));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /** @return the current cart lines, never null */
    private List<CartItem> currentItems() {
        Resource<List<CartItem>> r = items.getValue();
        return r != null && r.isSuccess() ? r.getData() : Collections.emptyList();
    }

    /** @return the chosen date */
    public LocalDate getDate() {
        return date;
    }

    /** @return the chosen slot */
    public String getSlot() {
        return slot;
    }

    /** @return the longest turnaround in days */
    public int getTurnaround() {
        return turnaround;
    }

    /** @return the notes */
    public String getNotes() {
        return notes;
    }

    /** @return cart lines */
    public LiveData<Resource<List<CartItem>>> getItems() {
        return items;
    }

    /** @return branches */
    public LiveData<Resource<List<Branch>>> getBranches() {
        return branches;
    }

    /** @return addresses */
    public LiveData<Resource<List<Address>>> getAddresses() {
        return addressList;
    }

    /** @return the fulfilment type */
    public LiveData<FulfilmentType> getFulfilment() {
        return fulfilment;
    }

    /** @return the chosen branch */
    public LiveData<Branch> getBranch() {
        return branch;
    }

    /** @return the chosen address */
    public LiveData<Address> getAddress() {
        return address;
    }

    /** @return the date strip days */
    public LiveData<List<LocalDate>> getDays() {
        return days;
    }

    /** @return the applied promotion */
    public LiveData<Promotion> getPromotion() {
        return promotion;
    }

    /** @return the promo error, 0 when none */
    public LiveData<Integer> getPromoError() {
        return promoError;
    }

    /** @return the price breakdown */
    public LiveData<PriceBreakdown> getPrice() {
        return price;
    }

    /** @return step errors {where, date, slot} */
    public LiveData<int[]> getStepErrors() {
        return stepErrors;
    }

    /** @return fires when the review step should open */
    public LiveData<Event<Boolean>> getToReview() {
        return toReview;
    }

    /** @return fires with the placed order */
    public LiveData<Event<Order>> getPlaced() {
        return placed;
    }

    /** @return the price calculator, for the review list */
    public PriceCalculator getCalculator() {
        return calculator;
    }
}
