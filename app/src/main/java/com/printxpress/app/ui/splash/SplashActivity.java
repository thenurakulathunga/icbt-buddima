package com.printxpress.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.main.MainActivity;
import com.printxpress.app.ui.onboarding.OnboardingActivity;

/**
 * Launcher (S01): shows the SplashScreen API brand screen, then routes: first run to onboarding,
 * a remembered session straight to Home (TC-12), otherwise to sign-in (which offers guest browsing).
 *
 * @since 1.0
 */
public class SplashActivity extends AppCompatActivity {

    /** Creates the activity; the system instantiates it from the manifest. */
    public SplashActivity() {
        super();
    }

    /**
     * Installs the splash screen and routes.
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        SessionManager session = ((PrintXpressApp) getApplication()).getContainer().getSession();
        Class<?> next;
        if (!session.isOnboardingDone()) {
            next = OnboardingActivity.class;
        } else if (session.isLoggedIn()) {
            next = MainActivity.class;
        } else {
            next = AuthActivity.class;
        }
        Intent intent = new Intent(this, next);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }
}
