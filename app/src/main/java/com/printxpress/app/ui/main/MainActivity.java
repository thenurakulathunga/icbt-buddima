package com.printxpress.app.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseActivity;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.databinding.ActivityMainBinding;
import com.printxpress.app.ui.components.AppDialog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Main shell (docs/02 §6): hosts {@code nav_main} with the bottom navigation, hides the bar on
 * inner screens, and keeps the cart and bell badges fresh.
 *
 * @since 1.0
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    /** Destinations that show the bottom navigation. */
    private static final Set<Integer> TOP_LEVEL = new HashSet<>(Arrays.asList(R.id.homeFragment,
            R.id.catalogFragment, R.id.ordersFragment, R.id.supportFragment, R.id.profileFragment));

    /** Shell state shared with the tab fragments. */
    private MainViewModel viewModel;
    /** The navigation controller. */
    private NavController navController;
    /** Notification permission request; the answer needs no handling beyond the system state. */
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    /** Creates the activity; the system instantiates it from the manifest. */
    public MainActivity() {
        super();
    }

    /**
     * Inflates the layout.
     *
     * @param inflater layout inflater
     * @return the binding
     */
    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    /**
     * Connects the bottom navigation to the graph.
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
        viewModel = viewModel(MainViewModel.class);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHost);
        navController = host.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int visibility = TOP_LEVEL.contains(destination.getId()) ? View.VISIBLE : View.GONE;
            binding.bottomNav.setVisibility(visibility);
            binding.navDivider.setVisibility(visibility);
        });
    }

    /** Refreshes the badges whenever the user signs in or out. */
    @Override
    protected void initObservers() {
        viewModel.getUserId().observe(this, id -> viewModel.refreshBadges());
    }

    /** Refreshes the badges when returning to the app. */
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshBadges();
    }

    /** @return the navigation controller, for deep links */
    public NavController getNavController() {
        return navController;
    }
    /**
     * Follows a notification deep link once the graph is ready, then asks for notification
     * permission if needed.
     *
     * @param savedInstanceState saved state; deep links are handled only on a fresh start
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleDeepLink(getIntent());
        }
        maybeAskNotificationPermission();
    }

    /**
     * Handles a notification tap while the shell is already open ({@code singleTop}).
     *
     * @param intent the new intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    /**
     * Opens the order or offer named by notification extras (FR-4.3, TC-52).
     *
     * @param intent the launching intent
     */
    private void handleDeepLink(Intent intent) {
        if (intent == null || navController == null) {
            return;
        }
        long orderId = intent.getLongExtra(Constants.ARG_ORDER_ID, 0);
        long promoId = intent.getLongExtra(Constants.ARG_PROMO_ID, 0);
        intent.removeExtra(Constants.ARG_ORDER_ID);
        intent.removeExtra(Constants.ARG_PROMO_ID);
        if (orderId > 0 && container().getAuthRepository().isLoggedIn()) {
            ShellRoutes.orderDetail(navController, orderId);
        } else if (promoId > 0) {
            ShellRoutes.offerDetail(navController, promoId);
        }
    }

    /**
     * On Android 13+, explains and then requests POST_NOTIFICATIONS once for signed-in users; a
     * refusal is respected and the in-app centre keeps working (FR-4.1).
     */
    private void maybeAskNotificationPermission() {
        SessionManager session = container().getSession();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || !session.isLoggedIn()
                || session.wasNotificationRationaleShown()
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        session.setNotificationRationaleShown();
        new AppDialog.Builder(this).icon(R.drawable.ic_notifications).title(R.string.permission_title)
                .message(R.string.permission_message)
                .positive(R.string.action_allow, () -> permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS))
                .negative(R.string.action_not_now).show();
    }
}
