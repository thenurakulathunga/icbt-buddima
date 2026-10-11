package com.printxpress.app.ui.main;

import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;

import com.printxpress.app.ui.components.AppTopBar;

/**
 * Wires any screen's {@link AppTopBar} to the shell: live cart and bell badges from
 * {@link MainViewModel}, and the cart and notifications shortcuts. Keeping this in one place means
 * every tab behaves the same.
 *
 * @since 1.0
 */
public final class TopBarBinder {

    /** Hidden constructor: static helper. */
    private TopBarBinder() {
    }

    /**
     * Connects a top bar.
     *
     * @param bar   the top bar
     * @param shell the activity-scoped shell ViewModel
     * @param owner the view lifecycle owner
     * @param nav   the navigation controller
     */
    public static void bind(AppTopBar bar, MainViewModel shell, LifecycleOwner owner, NavController nav) {
        shell.getCartCount().observe(owner, count -> bar.setCartBadge(count == null ? 0 : count));
        shell.getUnreadCount().observe(owner, count -> bar.setNotificationBadge(count == null ? 0 : count));
        ShellRoutes.bindTopBarActions(bar, nav);
    }
}
