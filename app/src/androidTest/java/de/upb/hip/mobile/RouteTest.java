package de.upb.hip.mobile;

//import android.test.ActivityUnitTestCase;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import de.upb.hip.mobile.activities.RouteActivity;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;
import com.google.android.gms.maps.model.LatLng;
//import android.app.Activity;
/**
 * Created by Lobner on 13.12.2015.
 */
public class RouteTest extends ActivityInstrumentationTestCase2<RouteActivity>
{
    public static final int TEST_ID = 127;
    private RouteActivity activity;
//    private Activity activity;
    public RouteTest()
    {
        super(RouteActivity.class);

    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void test() throws Exception
    {
        //TODO: implement tests:
        //TODO: 1. get route, 2. compare to expected values
        assertNotNull("activity is null", activity);
        RouteSet rs = activity.getRouteSet();
        assertNotNull("routeset is null", rs);

        System.out.println("Test");
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
//                final int expected = 1;
//                final int reality = 5;
//                assertEquals(expected, reality);
            }
        });
    }

    public void testMissingDescription()
    {       //IDs for testing: 127

        RouteSet rs = activity.getRouteSet();
        ArrayList<Waypoint> wp = new ArrayList<Waypoint>();
        List<RouteTag> rt = new LinkedList<RouteTag>();
        rt.add(new RouteTag("testtag", "testname", "route_stadt"));
        wp.add(new Waypoint(new LatLng(51.718953, 8.75583), 127));
        Route r = new Route(/*rs.getSize()*/127, "new route without description",
                "", wp, 1, rt, "route_stadt");
        Route r2 = new Route(/*rs.getSize()*/127, "new route with null description",
                null, wp, 1, rt, "route_stadt");
        rs.addRoute(r);
        rs.addRoute(r2);
        assertEquals(rs.getRoute(rs.getSize() - 1).id, TEST_ID);
        assertEquals(rs.getRoute(rs.getSize() - 2).id, TEST_ID);
    }

    public void testMissingTitle()
    {
        RouteSet rs = activity.getRouteSet();
        ArrayList<Waypoint> wp = new ArrayList<Waypoint>();
        List<RouteTag> rt = new LinkedList<RouteTag>();
        rt.add(new RouteTag("testtag", "testname", "route_stadt"));
        wp.add(new Waypoint(new LatLng(51.718953, 8.75583), 127));
        Route r = new Route(127, "", "this is a route without title", wp, 1, rt, "route_stadt");
        Route r2 = new Route(127, null, "this is a route without title", wp, 1, rt, "route_stadt");

        rs.addRoute(r);
        rs.addRoute(r2);
        assertEquals(rs.getRoute(rs.getSize() - 1).id, TEST_ID);
        assertEquals(rs.getRoute(rs.getSize() - 2).id, TEST_ID);
    }

    public void testMissingTag()
    {
        RouteSet rs = activity.getRouteSet();
        ArrayList<Waypoint> wp = new ArrayList<Waypoint>();
        List<RouteTag> rt = new LinkedList<RouteTag>();
        rt.add(new RouteTag("", "testname", "route_stadt"));
        rt.add(new RouteTag(null, "testname2", "route_stadt"));
        wp.add(new Waypoint(new LatLng(51.718953, 8.75583), 127));
        Route r = new Route(127, "tagless route", "this is a route without tag", wp, 1, rt, "route_stadt");

        rs.addRoute(r);
        assertEquals(rs.getRoute(rs.getSize() - 1).id, TEST_ID);
    }
}
