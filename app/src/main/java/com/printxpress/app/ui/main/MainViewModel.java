package com.printxpress.app.ui.main;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.data.repository.NotificationRepository;

/**
 * Activity-scoped state of the main shell: who is signed in and the cart and bell badge counts,
 * shared by every tab's top bar.
 *
 * @since 1.0
 */
public class MainViewModel extends BaseViewModel {

    /** Session access. */
    private final AuthRepository auth;
    /** Cart badge source. */
    private final CartRepository cart;
    /** Bell badge source. */
    private final NotificationRepository notifications;

    /**
     * Creates the ViewModel.
     *
     * @param auth          auth repository
     * @param cart          cart repository
     * @param notifications notification repository
     */
    public MainViewModel(AuthRepository auth, CartRepository cart, NotificationRepository notifications) {
        this.auth = auth;
        this.cart = cart;
        this.notifications = notifications;
    }

    /** @return the observable signed-in user id (-1 for guests) */
    public LiveData<Long> getUserId() {
        return auth.observeUserId();
    }

    /** @return the cart line count */
    public LiveData<Integer> getCartCount() {
        return cart.getCartCount();
    }

    /** @return the unread notification count */
    public LiveData<Integer> getUnreadCount() {
        return notifications.getUnreadCount();
    }

    /** @return true when a customer is signed in */
    public boolean isLoggedIn() {
        return auth.isLoggedIn();
    }

    /** @return the signed-in user id, or -1 */
    public long currentUserId() {
        return auth.currentUserId();
    }

    /** Recounts both badges, e.g. when the shell resumes. */
    public void refreshBadges() {
        long userId = auth.currentUserId();
        cart.refreshCount(userId);
        notifications.refreshUnread(userId);
    }
}
