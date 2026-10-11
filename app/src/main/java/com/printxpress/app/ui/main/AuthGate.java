package com.printxpress.app.ui.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.ui.auth.AuthActivity;

/**
 * Guards customer-only actions for guests (docs/01 §3): runs the action immediately when signed
 * in, otherwise opens sign-in and runs it after a successful login (TC-42).
 * Create one per fragment in {@code onCreate} or as a field, before the fragment is started.
 *
 * @since 1.0
 */
public final class AuthGate {

    /** The hosting fragment. */
    private final Fragment fragment;
    /** Launches the sign-in flow. */
    private final ActivityResultLauncher<android.content.Intent> launcher;
    /** The action waiting for sign-in. */
    private Runnable pending;

    /**
     * Registers the gate with a fragment.
     *
     * @param fragment the host fragment
     */
    public AuthGate(Fragment fragment) {
        this.fragment = fragment;
        this.launcher = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Runnable action = pending;
                    pending = null;
                    if (action != null && isLoggedIn()) {
                        action.run();
                    }
                });
    }

    /**
     * Runs an action for signed-in customers or asks guests to sign in first.
     *
     * @param action the customer-only action
     */
    public void run(Runnable action) {
        if (isLoggedIn()) {
            action.run();
        } else {
            pending = action;
            launcher.launch(AuthActivity.returning(fragment.requireContext()));
        }
    }

    /** @return true when a customer is signed in */
    public boolean isLoggedIn() {
        return ((PrintXpressApp) fragment.requireActivity().getApplication()).getContainer().getSession().isLoggedIn();
    }
}
