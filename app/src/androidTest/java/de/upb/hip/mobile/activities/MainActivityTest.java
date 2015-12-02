package de.upb.hip.mobile.activities;

import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;

    public MainActivityTest(){
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        activity = getActivity();
    }

    public void testActivityExists() {
        assertNotNull("activity is null", activity);

    }

    public void testThatSucceeds(){
        assert(true);
    }

//    public void testThatFails(){
//        assertEquals(3, 1 + 1);
//    }
}