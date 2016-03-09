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
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String BUSDORFKIRCHE = "Busdorfkirche";
    private static final String UNIPADERNORN = "Universität Paderborn";
    private MainActivity activity;

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
        activity = getActivity();
    }

    public void testActivityExists() {
        assertNotNull("activity is null", activity);
    }

    public void testSwitchToDetailsActivityAndBack(){
        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(BUSDORFKIRCHE)), click()));
        matchToolbarTitle(BUSDORFKIRCHE);
        onView(withId(R.id.detailsName)).check(matches(withText(BUSDORFKIRCHE)));

        onView(withContentDescription("Navigate up")).perform(click());

        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(UNIPADERNORN)), click()));
        matchToolbarTitle(UNIPADERNORN);
        onView(withId(R.id.detailsName)).check(matches(withText(UNIPADERNORN)));
    }

    public void testMapAvailable() {
        onView(withId(R.id.map_main)).perform(click());
    }

    public void testActionBarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        matchToolbarTitle(title);
    }
}