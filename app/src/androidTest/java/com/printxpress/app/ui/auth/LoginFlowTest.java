package com.printxpress.app.ui.auth;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.printxpress.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso checks of the auth screens (T-092): sign-in required-field errors, the generic
 * invalid-credentials message, and registration field validation.
 *
 * @since 1.0
 */
@RunWith(AndroidJUnit4.class)
public class LoginFlowTest {

    /** Submitting an empty form shows both required-field errors. */
    @Test
    public void emptyForm_showsRequiredErrors() {
        try (ActivityScenario<AuthActivity> ignored = ActivityScenario.launch(AuthActivity.class)) {
            onView(withId(R.id.btnLogin)).perform(click());
            onView(withText(R.string.err_identifier_required)).check(matches(isDisplayed()));
            onView(withText(R.string.err_password_required)).check(matches(isDisplayed()));
        }
    }

    /** Registration rejects a malformed email and a landline number (TC-03, TC-04). */
    @Test
    public void register_invalidEmailAndPhone() {
        try (ActivityScenario<AuthActivity> ignored = ActivityScenario.launch(AuthActivity.class)) {
            onView(withId(R.id.btnRegister)).perform(scrollTo(), click());
            onView(allOf(withId(R.id.pxEdit), isDescendantOfA(withId(R.id.inputEmail))))
                    .perform(scrollTo(), typeText("nimal@"));
            onView(allOf(withId(R.id.pxEdit), isDescendantOfA(withId(R.id.inputPhone))))
                    .perform(scrollTo(), typeText("0112345678"));
            closeSoftKeyboard();
            onView(withId(R.id.btnRegister)).perform(scrollTo(), click());
            onView(withText(R.string.err_email_invalid)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withText(R.string.err_phone_invalid)).perform(scrollTo()).check(matches(isDisplayed()));
        }
    }

    /** Unknown credentials show the generic message. */
    @Test
    public void unknownUser_showsGenericError() {
        try (ActivityScenario<AuthActivity> ignored = ActivityScenario.launch(AuthActivity.class)) {
            onView(allOf(withId(R.id.pxEdit), isDescendantOfA(withId(R.id.inputIdentifier))))
                    .perform(typeText("nobody@example.lk"));
            onView(allOf(withId(R.id.pxEdit), isDescendantOfA(withId(R.id.inputPassword))))
                    .perform(typeText("Wrong@2026"));
            closeSoftKeyboard();
            onView(withId(R.id.btnLogin)).perform(click());
            onView(withText(R.string.err_invalid_credentials)).check(matches(isDisplayed()));
        }
    }
}
