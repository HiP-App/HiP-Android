package de.upb.hip.mobile.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.uiautomator.*;

import android.support.test.espresso.contrib.*;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;

    private static final String BUSDORFKIRCHE = "Busdorfkirche";
    private static final String UNIPADERNORN = "Universität Paderborn";

    public MainActivityTest(){
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void testActivityExists() {
        assertNotNull("activity is null", activity);
    }

    public void testSwitchToDetailsActivityAndBack(){
        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(BUSDORFKIRCHE)), click()));
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(BUSDORFKIRCHE))));
        onView(withId(R.id.txtName)).check(matches(withText(BUSDORFKIRCHE)));

        onView(withContentDescription("Navigate up")).perform(click());

        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(UNIPADERNORN)), click()));
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(UNIPADERNORN))));
        onView(withId(R.id.txtName)).check(matches(withText(UNIPADERNORN)));
    }

    public void testGoogleMapsMarker() throws UiObjectNotFoundException, InterruptedException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker1 = device.findObject(new UiSelector().descriptionContains(BUSDORFKIRCHE));
//        marker1.click();
        UiObject marker2 = device.findObject(new UiSelector().descriptionContains(UNIPADERNORN));
        marker2.click();
    }

    public void testActionBarTitle(){
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.app_name))));
    }

//    public void testThatFails(){
//        assertEquals(3, 1 + 1);
//    }
}