package de.upb.hip.mobile.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import de.upb.hip.mobile.adapters.DBAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;


public class DetailsActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainactivity;

    private static final String UNIPADERBORN = "Universität Paderborn";

    public DetailsActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainactivity = getActivity();
        onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(UNIPADERBORN)), click()));
    }

    /* test title name */
    public void testActionBarTitle() {
        matchToolbarTitle(UNIPADERBORN);
    }

    /* help function for toolbar name matching */
    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    /* help function for toolbar name matching */
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

    /* test name of the image */
    public void testImageNameText() {
        onView(withId(R.id.txtName)).check(matches(withText(UNIPADERBORN)));
    }

    /* test the description */
    public void testDescriptionText() {
        onView(withId(R.id.txtDescription)).check(matches(withText("Die Universität Paderborn in Paderborn, Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.")));
    }

    /* test if the image is disployed */
    public void testImage() {
        onView(withId(R.id.imageViewDetail)).check(matches(isDisplayed()));
    }

    /* test if the ImageViewDetail can be opened by clicking on the image */
    public void testOpenImageView() {
        onView(withId(R.id.imageViewDetail)).perform(click());
        matchToolbarTitle(UNIPADERBORN);
    }
}