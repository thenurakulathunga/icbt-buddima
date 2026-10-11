package com.printxpress.app.ui.checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.repository.OrderRepository;

/**
 * Order success state (S16, FR-2.13): the placed order with its number.
 *
 * @since 1.0
 */
public class OrderSuccessViewModel extends BaseViewModel {

    /** Orders. */
    private final OrderRepository orders;
    /** The order. */
    private final MutableLiveData<Resource<Order>> order = new MutableLiveData<>();
    /** Loaded order id. */
    private long orderId = -1;

    /**
     * Creates the ViewModel.
     *
     * @param orders order repository
     */
    public OrderSuccessViewModel(OrderRepository orders) {
        this.orders = orders;
    }

    /**
     * Loads the order once.
     *
     * @param id the order id
     */
    public void start(long id) {
        if (orderId != id) {
            orderId = id;
            load(orders.getOrder(id), order);
        }
    }

    /** @return the order */
    public LiveData<Resource<Order>> getOrder() {
        return order;
    }
}
