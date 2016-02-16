package de.upb.hip.mobile.activities;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

import de.upb.hip.mobile.activities.BaseActivity;
import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Lobner on 20.01.2016.
 */
public class NavigationDrawerTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    MainActivity activity;

    public NavigationDrawerTest()
    {
        super(MainActivity.class);
    }


    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void testOpenAndClose()    {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        closeDrawer(R.id.drawer_layout);
    }

    public void testSwitchToOtherView() {
        Resources res = getInstrumentation().getTargetContext().getResources();
        String[] navDrawerDescriptions = res.getStringArray(R.array.nav_drawer_entries);

        assertNotNull(navDrawerDescriptions);

        for(int i = 0; i < navDrawerDescriptions.length; i++) {
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

            openDrawer(R.id.drawer_layout);
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

            onView(allOf(withId(R.id.navigation_drawer_item_text), hasSibling(withText(navDrawerDescriptions[i])))).perform(click());
        }
    }
}
