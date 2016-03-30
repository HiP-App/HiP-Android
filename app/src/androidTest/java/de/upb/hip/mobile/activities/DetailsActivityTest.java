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
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;

/**
 * Test for DetailsActivity
 */
public class DetailsActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    // Title of the ActionBar
    private static final String UNIPADERBORN = "Universität Paderborn";
    private static final String UNI_DESCRIPTION = "Die Universität Paderborn in Paderborn, " +
            "Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.";

    public DetailsActivityTest() {
        super(MainActivity.class);
    }

    /**
     * Help function for toolbar name matching
     *
     * @param title the assertion to check.
     * @return interaction for further perform/verification calls.
     */
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

    /**
     * Sets up the fixture. This method is called before a test is executed.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
        onView(withId(R.id.mainRecyclerView)).perform(RecyclerViewActions.
                actionOnItem(hasDescendant(withText(UNIPADERBORN)), click()));
    }

    /**
     * Test title name in ActionBar
     */
    public void testActionBarTitle() {
        matchToolbarTitle(UNIPADERBORN);
    }

    /**
     * Test name of the image
     */
    public void testImageNameText() {
        onView(withId(R.id.detailsName)).check(matches(withText(UNIPADERBORN)));
    }

    /**
     * Test the description
     */
    public void testDescriptionText() {
        onView(withId(R.id.detailsDescription)).check(matches(withText(UNI_DESCRIPTION)));
    }

    /**
     * Test if the image is disployed
     */
    public void testImage() {
        onView(withId(R.id.detailsImageView)).check(matches(isDisplayed()));
    }

    /**
     * Test if the ImageViewDetail can be opened by clicking on the image
     */
    public void testOpenImageView() {
        onView(withId(R.id.detailsImageView)).perform(click());
        matchToolbarTitle(UNIPADERBORN);
    }
}
