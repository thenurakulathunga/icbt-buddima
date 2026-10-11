package com.printxpress.app.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.OrderRepository;

import java.util.Collections;
import java.util.List;

/**
 * My orders state (S17, FR-3.1, FR-1.8): Active and Past tabs.
 *
 * @since 1.0
 */
public class OrdersViewModel extends BaseViewModel {

    /** Tab index: active. */
    public static final int TAB_ACTIVE = 0;
    /** Tab index: past. */
    public static final int TAB_PAST = 1;

    /** Session. */
    private final AuthRepository auth;
    /** Orders. */
    private final OrderRepository orders;
    /** Selected tab. */
    private final MutableLiveData<Integer> tab = new MutableLiveData<>(TAB_ACTIVE);
    /** The list of the selected tab. */
    private final MutableLiveData<Resource<List<Order>>> list = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth   auth repository
     * @param orders order repository
     */
    public OrdersViewModel(AuthRepository auth, OrderRepository orders) {
        this.auth = auth;
        this.orders = orders;
    }

    /** @return true when a customer is signed in */
    public boolean isLoggedIn() {
        return auth.isLoggedIn();
    }

    /**
     * Selects a tab and loads it.
     *
     * @param index {@link #TAB_ACTIVE} or {@link #TAB_PAST}
     */
    public void selectTab(int index) {
        tab.setValue(index);
        refresh();
    }

    /** Reloads the selected tab. */
    public void refresh() {
        if (!auth.isLoggedIn()) {
            list.setValue(Resource.success(Collections.emptyList()));
            return;
        }
        long userId = auth.currentUserId();
        Integer t = tab.getValue();
        load(t != null && t == TAB_PAST ? orders.getPastOrders(userId) : orders.getActiveOrders(userId), list);
    }

    /** @return the selected tab */
    public LiveData<Integer> getTab() {
        return tab;
    }

    /** @return the orders of the selected tab */
    public LiveData<Resource<List<Order>>> getOrders() {
        return list;
    }
}
