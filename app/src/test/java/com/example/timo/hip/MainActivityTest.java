package com.example.timo.hip;

import android.app.ActionBar;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest extends TestCase {

    private MainActivity mActivity;

    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(MainActivity.class).create().get();

    }

    @Test
    public void testTitleActivityBar() throws Exception {
        final ActionBar actionBar = mActivity.getActionBar();
        assertThat("ToolBar is not null", actionBar, is(notNullValue()));
        assertEquals("Name of app", mActivity.getResources().getString(R.string.app_name), actionBar.getTitle().toString());
    }
}