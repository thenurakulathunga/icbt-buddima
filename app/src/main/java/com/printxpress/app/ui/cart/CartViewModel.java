package com.printxpress.app.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.validation.rules.RangeRule;
import com.printxpress.app.core.validation.rules.StepRule;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.domain.pricing.PriceBreakdown;
import com.printxpress.app.domain.pricing.PriceCalculator;

import java.util.List;

/**
 * Cart state (S13, FR-2.8): lines, subtotal with bulk discounts, quantity changes (BR-3) and
 * removal.
 *
 * @since 1.0
 */
public class CartViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Cart. */
    private final CartRepository cart;
    /** Pricing. */
    private final PriceCalculator calculator;
    /** Lines. */
    private final MutableLiveData<Resource<List<CartItem>>> items = new MutableLiveData<>();
    /** Subtotal breakdown. */
    private final MutableLiveData<PriceBreakdown> price = new MutableLiveData<>(PriceBreakdown.EMPTY);
    /** One-shot quantity error: cart line id, or a negative id to clear. */
    private final MutableLiveData<Event<long[]>> quantityError = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth       auth repository
     * @param cart       cart repository
     * @param calculator price calculator
     */
    public CartViewModel(AuthRepository auth, CartRepository cart, PriceCalculator calculator) {
        this.auth = auth;
        this.cart = cart;
        this.calculator = calculator;
    }

    /** @return true when a customer is signed in */
    public boolean isLoggedIn() {
        return auth.isLoggedIn();
    }

    /** Reloads the cart. */
    public void refresh() {
        if (!auth.isLoggedIn()) {
            items.setValue(Resource.success(java.util.Collections.emptyList()));
            return;
        }
        subscribe(cart.getCart(auth.currentUserId()), r -> {
            items.setValue(r);
            if (r.isSuccess()) {
                price.setValue(calculator.calculate(r.getData(), null, null));
            }
        });
    }

    /**
     * Validates and stores a new quantity (BR-3).
     *
     * @param item     the line
     * @param quantity the new quantity, or -1 when not a number
     */
    public void updateQuantity(CartItem item, int quantity) {
        Product p = item.getProduct();
        String value = quantity < 0 ? "" : String.valueOf(quantity);
        boolean valid = new RangeRule(p.getMinQty(), p.getMaxQty(), 0).validate(value).isValid()
                && new StepRule(p::getMinQty, p::getQtyStep, 0).validate(value).isValid() && quantity > 0;
        quantityError.setValue(new Event<>(new long[]{item.getCartItemId(), valid ? 0 : 1}));
        if (!valid) {
            return;
        }
        subscribe(cart.updateQuantity(auth.currentUserId(), item.getCartItemId(), quantity), r -> {
            if (r.isSuccess()) {
                refresh();
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        });
    }

    /**
     * Removes a line.
     *
     * @param item the line
     */
    public void remove(CartItem item) {
        subscribe(cart.remove(auth.currentUserId(), item.getCartItemId()), r -> {
            if (r.isSuccess()) {
                postMessage(R.string.item_removed);
                refresh();
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        });
    }

    /** @return the lines */
    public LiveData<Resource<List<CartItem>>> getItems() {
        return items;
    }

    /** @return the subtotal breakdown */
    public LiveData<PriceBreakdown> getPrice() {
        return price;
    }

    /** @return quantity error events: {cartItemId, 1 = error / 0 = ok} */
    public LiveData<Event<long[]>> getQuantityError() {
        return quantityError;
    }

    /** @return the price calculator, for the adapter */
    public PriceCalculator getCalculator() {
        return calculator;
    }
}
