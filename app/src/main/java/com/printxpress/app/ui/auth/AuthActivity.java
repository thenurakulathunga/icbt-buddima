package com.printxpress.app.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.printxpress.app.core.base.BaseActivity;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.databinding.ActivityAuthBinding;
import com.printxpress.app.ui.main.MainActivity;

/**
 * Hosts the sign-in flow (nav_auth). When opened from inside the app (e.g. adding to the cart as a
 * guest) it simply closes after signing in so the user returns to where they were.
 *
 * @since 1.0
 */
public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    /** Creates the activity; the system instantiates it from the manifest. */
    public AuthActivity() {
        super();
    }

    /**
     * Builds an intent that returns to the caller after sign-in.
     *
     * @param context the caller
     * @return the intent
     */
    public static Intent returning(Context context) {
        return new Intent(context, AuthActivity.class).putExtra(Constants.EXTRA_RETURN_AFTER_LOGIN, true);
    }

    /**
     * Inflates the layout.
     *
     * @param inflater layout inflater
     * @return the binding
     */
    @Override
    protected ActivityAuthBinding inflateBinding(LayoutInflater inflater) {
        return ActivityAuthBinding.inflate(inflater);
    }

    /**
     * Nothing to configure: the navigation host is declared in XML.
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
    }

    /** Leaves the auth flow after a successful sign-in or registration. */
    public void onAuthenticated() {
        if (!getIntent().getBooleanExtra(Constants.EXTRA_RETURN_AFTER_LOGIN, false)) {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            setResult(RESULT_OK);
        }
        finish();
    }

    /** Continues into the app without an account. */
    public void continueAsGuest() {
        if (!getIntent().getBooleanExtra(Constants.EXTRA_RETURN_AFTER_LOGIN, false)) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
