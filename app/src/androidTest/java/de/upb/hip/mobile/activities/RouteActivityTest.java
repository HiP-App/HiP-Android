
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


import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import de.upb.hip.mobile.models.RouteSet;

/**
 * Test for the RouteActivity
 */
public class RouteActivityTest extends ActivityInstrumentationTestCase2<RouteActivity> {
    public static final int TEST_ID = 127;
    private RouteActivity activity;

    public RouteActivityTest() {
        super(RouteActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void test() throws Exception {
        //TODO: implement tests:
        //TODO: 1. get route, 2. compare to expected values
        assertNotNull("RouteActivity is null", activity);
        RouteSet routeSet = activity.getRouteSet();
        assertNotNull("RouteSet is null", routeSet);

        Log.i("RouteActivityTest", "Test");
        activity.runOnUiThread(new Runnable() {
            public void run() {
//                final int expected = 1;
//                final int reality = 5;
//                assertEquals(expected, reality);
            }
        });
    }

    //Disable tests as they are not compatible with the new DB format and would have to be rewritten
    //anyway for new data
/**
    public void testMissingDescription() {

        RouteSet routeSet = activity.getRouteSet();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        List<RouteTag> routeTags = new LinkedList<>();
        routeTags.add(new RouteTag("testtag", "testname", "route_stadt"));
        waypoints.add(new Waypoint(51.718953, 8.75583, 127));
        Route route1 = new Route(TEST_ID, "new route without description",
                "", waypoints, 1, 1, routeTags, "route_stadt");
        Route route2 = new Route(TEST_ID, "new route with null description",
                null, waypoints, 1, 1, routeTags, "route_stadt");
        routeSet.addRoute(route1);
        routeSet.addRoute(route2);
        assertEquals(routeSet.getRouteByPosition(routeSet.getSize() - 1).getId(), TEST_ID);
        assertEquals(routeSet.getRouteByPosition(routeSet.getSize() - 2).getId(), TEST_ID);
    }

    public void testMissingTitle() {
        RouteSet routeSet = activity.getRouteSet();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        List<RouteTag> routeTags = new LinkedList<>();
        routeTags.add(new RouteTag("testtag", "testname", "route_stadt"));
        waypoints.add(new Waypoint(51.718953, 8.75583, 127));
        Route route1 = new Route(TEST_ID, "", "this is a route without title", waypoints, 1, 1, routeTags, "route_stadt");
        Route route2 = new Route(TEST_ID, null, "this is a route without title", waypoints, 1, 1, routeTags, "route_stadt");

        routeSet.addRoute(route1);
        routeSet.addRoute(route2);
        assertEquals(routeSet.getRouteByPosition(routeSet.getSize() - 1).getId(), TEST_ID);
        assertEquals(routeSet.getRouteByPosition(routeSet.getSize() - 2).getId(), TEST_ID);
    }

    public void testMissingTag() {
        RouteSet routeSet = activity.getRouteSet();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        List<RouteTag> routeTags = new LinkedList<>();
        routeTags.add(new RouteTag("", "testname", "route_stadt"));
        routeTags.add(new RouteTag(null, "testname2", "route_stadt"));
        waypoints.add(new Waypoint(51.718953, 8.75583, 127));
        Route route = new Route(TEST_ID, "tagless route", "this is a route without tag", waypoints, 1, 1, routeTags, "route_stadt");

        routeSet.addRoute(route);
        assertEquals(routeSet.getRouteByPosition(routeSet.getSize() - 1).getId(), TEST_ID);
    }
 **/
}
