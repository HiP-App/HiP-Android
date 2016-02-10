package de.upb.hip.mobile.activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.core.Is.is;
import android.support.test.uiautomator.*;

import android.support.test.espresso.contrib.*;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

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
        matchToolbarTitle(BUSDORFKIRCHE);
        onView(withId(R.id.txtName)).check(matches(withText(BUSDORFKIRCHE)));

        onView(withContentDescription("Navigate up")).perform(click());

        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(UNIPADERNORN)), click()));
        matchToolbarTitle(UNIPADERNORN);
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
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        matchToolbarTitle(title);
    }

    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    public void testOpenAndCloseDrawer(){

        openDrawer(R.id.drawer_layout);

        onView(withId(R.id.navigation_drawer)).check(matches(hasDescendant(withText("Übersicht"))));

        closeDrawer(R.id.drawer_layout);
    }
}