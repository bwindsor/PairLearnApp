package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.provider.UserDictionary;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.intent.Intents.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.core.AllOf.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestSetupTestUI {
    @Rule
    public IntentsTestRule<HomeActivity> mActivityRule = new IntentsTestRule<>(
            HomeActivity.class);
    @Before
    public void InitActivity() {
        onView(withId(R.id.home_button_test)).perform(click());
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.github.bwindsor.pairlearnapp", appContext.getPackageName());
    }

    @Test
    public void testValidTimeLimit() throws Exception {
        // onView(withId(R.id.home_button_test)).perform(click());

        // This sets the time limit to something valid
        onView(withId(R.id.setup_test_time_input))
                .perform(replaceText("2.3"));

        // This selects some words
        onView(withId(R.id.setup_test_select_cat_button))
                .perform(click());

        // This is a bit silly. It's no good having data source as a singleton class because it
        // makes tests hard to implement. So I should probably change that. Also too much logic in
        // the activities, should be in separate java classes
        onView(withId(R.id.cat_select_ok)).perform(click());

        // This clicks the 'go' button
        onView(withId(R.id.setup_test_button_go))
                .perform(click());

        // This checks that intent to switch activity is correct
        intended(allOf(
                hasComponent(hasShortClassName(".TestActivity")),
                toPackage("com.github.bwindsor.pairlearnapp"),
                hasExtra(TestActivity.EXTRA_QUESTION_TIMEOUT, 2.3f)));

        // Checks that the next activity loaded
        onView(withId(R.id.test_fragment_container))
                .check(matches(isDisplayed()));
    }
    public void testEmptyTimeLimit() throws Exception {
        // This sets the time limit to empty
        onView(withId(R.id.setup_test_time_input))
                .perform(replaceText(""));

        // This clicks the 'go' button
        onView(withId(R.id.setup_test_button_go))
                .perform(click());

        // This checks that the error dialog is displayed
        onView(withText(R.string.dialog_invalid_time_limit_message))
                .check(matches(isDisplayed()));

        // Click OK on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        // Checks that this activity is still the same
        onView(withId(R.id.setup_test_time_input))
                .check(matches(isDisplayed()));
    }
    public void testZeroTimeLimit() throws Exception {
        // This sets the time limit to empty
        onView(withId(R.id.setup_test_time_input))
                .perform(replaceText("0"));

        // This clicks the 'go' button
        onView(withId(R.id.setup_test_button_go))
                .perform(click());

        // This checks that the error dialog is displayed
        onView(withText(R.string.dialog_invalid_time_limit_message))
                .check(matches(isDisplayed()));

        // Click OK on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        // Checks that this activity is still the same
        onView(withId(R.id.setup_test_time_input))
                .check(matches(isDisplayed()));
    }
}
