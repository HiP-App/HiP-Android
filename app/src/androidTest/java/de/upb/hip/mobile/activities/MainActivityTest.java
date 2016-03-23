/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.upb.hip.mobile.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.core.Is.is;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String BUSDORFKIRCHE = "Busdorfkirche";
    private static final String UNI_PADERBORN = "Universität Paderborn";
    private MainActivity mActivity;

    public MainActivityTest(){
        super(MainActivity.class);
    }

    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    public void testActivityExists() {
        assertNotNull("activity is null", mActivity);
    }

    public void testSwitchToDetailsActivityAndBack(){
        onView(withId(R.id.mainRecyclerView)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUSDORFKIRCHE)), click()));
        matchToolbarTitle(BUSDORFKIRCHE);
        onView(withId(R.id.mainRowItemName)).check(matches(withText(BUSDORFKIRCHE)));

        onView(withContentDescription("Navigate up")).perform(click());

        onView(withId(R.id.mainRecyclerView)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(UNI_PADERBORN)), click()));
        matchToolbarTitle(UNI_PADERBORN);
        onView(withId(R.id.mainRowItemName)).check(matches(withText(UNI_PADERBORN)));
    }

    public void testMapAvailable() {
        onView(withId(R.id.mainMap)).perform(click());
    }

    public void testActionBarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.app_name);
        matchToolbarTitle(title);
    }
}