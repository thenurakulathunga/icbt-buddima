package com.printxpress.app.ui.settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.UserRepository;
import com.printxpress.app.worker.OrderProgressScheduler;

/**
 * Settings state (S34, FR-4.6, FR-1.10): notification preferences, theme, demo speed and account
 * deletion.
 *
 * @since 1.0
 */
public class SettingsViewModel extends BaseViewModel {

    /** Theme index: follow the system. */
    public static final int THEME_SYSTEM = 0;
    /** Theme index: light. */
    public static final int THEME_LIGHT = 1;
    /** Theme index: dark. */
    public static final int THEME_DARK = 2;

    /** Session. */
    private final AuthRepository auth;
    /** Profile and preferences. */
    private final UserRepository users;
    /** Local settings. */
    private final SessionManager session;
    /** Simulation jobs, cancelled when the account is deleted. */
    private final OrderProgressScheduler scheduler;
    /** The signed-in user. */
    private final MutableLiveData<Resource<User>> user = new MutableLiveData<>();
    /** One-shot account deletion. */
    private final MutableLiveData<Event<Boolean>> deleted = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth      auth repository
     * @param users     user repository
     * @param session   session storage
     * @param scheduler order progress scheduler
     */
    public SettingsViewModel(AuthRepository auth, UserRepository users, SessionManager session,
                             OrderProgressScheduler scheduler) {
        this.auth = auth;
        this.users = users;
        this.session = session;
        this.scheduler = scheduler;
    }

    /** @return true when signed in */
    public boolean isLoggedIn() {
        return auth.isLoggedIn();
    }

    /** Loads the user's preferences. */
    public void refresh() {
        if (auth.isLoggedIn()) {
            load(users.getUser(auth.currentUserId()), user);
        }
    }

    /**
     * Saves both notification preferences (TC-54).
     *
     * @param orders order updates on
     * @param promos promotions on
     */
    public void savePreferences(boolean orders, boolean promos) {
        subscribe(users.updatePreferences(auth.currentUserId(), orders, promos), r -> {
            if (r.isSuccess()) {
                postMessage(R.string.prefs_saved);
            } else if (r.isError()) {
                postError(r.getMessageRes());
                refresh();
            }
        });
    }

    /** @return the selected theme index */
    public int getThemeIndex() {
        int mode = session.getThemeMode();
        if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
            return THEME_LIGHT;
        }
        return mode == AppCompatDelegate.MODE_NIGHT_YES ? THEME_DARK : THEME_SYSTEM;
    }

    /**
     * Applies and stores a theme.
     *
     * @param index one of the THEME_ constants
     */
    public void setThemeIndex(int index) {
        int mode = index == THEME_LIGHT ? AppCompatDelegate.MODE_NIGHT_NO
                : index == THEME_DARK ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        if (mode != session.getThemeMode()) {
            session.setThemeMode(mode);
        }
    }

    /** @return true when fast order simulation is on */
    public boolean isDemoSpeed() {
        return session.isDemoSpeed();
    }

    /**
     * Switches the simulation speed; applies from the next step of each order.
     *
     * @param demo true for 20-second steps
     */
    public void setDemoSpeed(boolean demo) {
        session.setDemoSpeed(demo);
    }

    /** Deletes the account, its data and its pending simulation jobs (FR-1.10). */
    public void deleteAccount() {
        subscribe(auth.deleteAccount(), r -> {
            if (r.isSuccess()) {
                scheduler.cancelAll();
                deleted.setValue(new Event<>(true));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /** @return the user with preferences */
    public LiveData<Resource<User>> getUser() {
        return user;
    }

    /** @return fires once after deletion */
    public LiveData<Event<Boolean>> getDeleted() {
        return deleted;
    }
}
