package de.upb.hip.mobile;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import de.upb.hip.mobile.activities.RouteActivity;

/**
 * Created by Lobner on 13.01.2016.
 */
public class RouteUserTest extends ActivityInstrumentationTestCase2<RouteActivity>
{
    private DisplaySingleImageActivity activity;
    //private Activity activity;
    public RouteUserTest()
    {
        super(RouteActivity.class);

    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }









}
