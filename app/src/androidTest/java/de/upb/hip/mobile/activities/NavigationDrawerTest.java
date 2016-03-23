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

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Test for the Navigation Drawer
 */
public class NavigationDrawerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity activity;

    public NavigationDrawerTest() {
        super(MainActivity.class);
    }

    /**
     * Setup test
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    /**
     * Test: Open and close of the navigation drawer
     */
    public void testOpenAndClose() {
        onView(withId(R.id.mainActivityDrawerLayout)).check(matches(isClosed()));

        openDrawer(R.id.mainActivityDrawerLayout);
        onView(withId(R.id.mainActivityDrawerLayout)).check(matches(isOpen()));

        closeDrawer(R.id.mainActivityDrawerLayout);
    }

    /**
     * Test: Click on items in navigation drawer and test if switches are successful
     */
    public void testSwitchToOtherView() {
        Resources res = getInstrumentation().getTargetContext().getResources();
        String[] navDrawerDescriptions = res.getStringArray(R.array.nav_drawer_entries);

        assertNotNull(navDrawerDescriptions);

        for (int i = 0; i < navDrawerDescriptions.length; i++) {
            onView(withId(R.id.mainActivityDrawerLayout)).check(matches(isClosed()));

            openDrawer(R.id.mainActivityDrawerLayout);
            onView(withId(R.id.mainActivityDrawerLayout)).check(matches(isOpen()));

            onView(allOf(
                    withId(R.id.navigationDrawerRowItemText),
                    hasSibling(withText(navDrawerDescriptions[i])))).perform(click());
        }
    }
}
