package com.github.bwindsor.pairlearnapp;

import android.content.Context;
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestHomeUI {
    @Rule
    public IntentsTestRule<HomeActivity> mActivityRule = new IntentsTestRule<>(
            HomeActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.bwindsor.pairlearnapp", appContext.getPackageName());
    }

    @Test
    public void testTestMeButton() throws Exception {
        // This clicks the 'test me' button
        onView(withId(R.id.home_button_test))
            .perform(click());

        // This checks that intent to switch activity is correct
        intended(allOf(
                hasComponent(hasShortClassName(".SetupTestActivity")),
                toPackage("com.github.bwindsor.pairlearnapp")));

        // Checks that the next activity loaded
        onView(withId(R.id.setup_test_button_go))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEditActivityButton() throws Exception {
        // This clicks the 'edit activity' button
        onView(withId(R.id.home_button_edit_vocab))
                .perform(click());

        // This checks that intent to switch activity is correct
        intended(allOf(
                hasComponent(hasShortClassName(".CategoryOpenActivity")),
                toPackage("com.github.bwindsor.pairlearnapp")));

        // Checks that the next activity loaded
        onView(withId(R.id.cat_open_list))
                .check(matches(isDisplayed()));
    }
}
