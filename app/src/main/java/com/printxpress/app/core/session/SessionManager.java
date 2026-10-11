package com.printxpress.app.core.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores the signed-in user and small app preferences in private SharedPreferences.
 * <p>
 * Pattern role: Singleton. Only the user id is kept here, never a password or personal data.
 * A "remember me" flag decides whether the session survives an app restart (FR-1.3).
 * </p>
 *
 * @since 1.0
 */
public final class SessionManager {

    /** Sentinel for "no user signed in". */
    public static final long NO_USER = -1L;

    /** Preference file name. */
    private static final String PREFS = "printxpress_session";
    /** Key: signed-in user id. */
    private static final String KEY_USER_ID = "user_id";
    /** Key: whether the session persists across restarts. */
    private static final String KEY_REMEMBER = "remember_me";
    /** Key: whether onboarding has been completed. */
    private static final String KEY_ONBOARDED = "onboarding_done";
    /** Key: night mode preference. */
    private static final String KEY_THEME = "theme_mode";
    /** Key: demo speed for the order simulation. */
    private static final String KEY_DEMO_SPEED = "demo_speed";
    /** Key: whether the notification rationale was shown. */
    private static final String KEY_NOTIF_ASKED = "notification_asked";
    /** Key: promotions already announced by notification. */
    private static final String KEY_ANNOUNCED_PROMOS = "announced_promos";

    /** The lazily created shared instance. */
    private static volatile SessionManager instance;

    /** Backing preferences. */
    private final SharedPreferences prefs;
    /** Observable signed-in user id. */
    private final MutableLiveData<Long> userIdLive = new MutableLiveData<>();
    /** User id kept only for this process when "remember me" is off. */
    private long volatileUserId = NO_USER;

    /**
     * Creates the manager.
     *
     * @param context any context; the application context is retained
     */
    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_REMEMBER, false)) {
            prefs.edit().remove(KEY_USER_ID).apply();
        }
        userIdLive.setValue(getUserId());
    }

    /**
     * Returns the shared instance.
     *
     * @param context any context
     * @return the singleton
     */
    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Starts a session.
     *
     * @param userId   the signed-in user
     * @param remember true to persist the session across restarts
     */
    public void login(long userId, boolean remember) {
        volatileUserId = userId;
        SharedPreferences.Editor editor = prefs.edit().putBoolean(KEY_REMEMBER, remember);
        if (remember) {
            editor.putLong(KEY_USER_ID, userId);
        } else {
            editor.remove(KEY_USER_ID);
        }
        editor.apply();
        userIdLive.postValue(userId);
    }

    /** Ends the session and clears the stored user id. */
    public void logout() {
        volatileUserId = NO_USER;
        prefs.edit().remove(KEY_USER_ID).putBoolean(KEY_REMEMBER, false).apply();
        userIdLive.postValue(NO_USER);
    }

    /** @return the signed-in user id or {@link #NO_USER} */
    public long getUserId() {
        if (volatileUserId != NO_USER) {
            return volatileUserId;
        }
        return prefs.getLong(KEY_USER_ID, NO_USER);
    }

    /** @return true when a user is signed in */
    public boolean isLoggedIn() {
        return getUserId() != NO_USER;
    }

    /** @return observable signed-in user id */
    public LiveData<Long> observeUserId() {
        return userIdLive;
    }

    /** @return true once onboarding has been completed */
    public boolean isOnboardingDone() {
        return prefs.getBoolean(KEY_ONBOARDED, false);
    }

    /** Marks onboarding as completed. */
    public void setOnboardingDone() {
        prefs.edit().putBoolean(KEY_ONBOARDED, true).apply();
    }

    /** @return the AppCompatDelegate night mode */
    public int getThemeMode() {
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /**
     * Saves and applies the night mode.
     *
     * @param mode an AppCompatDelegate night mode constant
     */
    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME, mode).apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /** @return true when the order simulation runs at demo speed */
    public boolean isDemoSpeed() {
        return prefs.getBoolean(KEY_DEMO_SPEED, true);
    }

    /**
     * Sets the order simulation speed.
     *
     * @param demo true for fast demo steps
     */
    public void setDemoSpeed(boolean demo) {
        prefs.edit().putBoolean(KEY_DEMO_SPEED, demo).apply();
    }

    /** @return true once the notification rationale has been shown */
    public boolean wasNotificationRationaleShown() {
        return prefs.getBoolean(KEY_NOTIF_ASKED, false);
    }

    /** Records that the notification rationale has been shown. */
    public void setNotificationRationaleShown() {
        prefs.edit().putBoolean(KEY_NOTIF_ASKED, true).apply();
    }

    /**
     * Atomically claims the announcement of a promotion to a user: returns true only for the
     * first caller, so overlapping promotion jobs cannot announce the same offer twice.
     *
     * @param userId  the user
     * @param promoId the promotion
     * @return true when this caller should announce it
     */
    public synchronized boolean claimPromoAnnouncement(long userId, long promoId) {
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY_ANNOUNCED_PROMOS, new HashSet<>()));
        if (!ids.add(userId + ":" + promoId)) {
            return false;
        }
        prefs.edit().putStringSet(KEY_ANNOUNCED_PROMOS, ids).commit();
        return true;
    }

    /** Clears everything except onboarding and theme, used after account deletion. */
    public void clearUserData() {
        volatileUserId = NO_USER;
        prefs.edit().remove(KEY_USER_ID).remove(KEY_REMEMBER).remove(KEY_ANNOUNCED_PROMOS).apply();
        userIdLive.postValue(NO_USER);
    }
}
