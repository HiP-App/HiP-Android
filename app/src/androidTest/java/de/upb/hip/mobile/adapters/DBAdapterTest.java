package de.upb.hip.mobile.adapters;


import android.test.AndroidTestCase;

import com.couchbase.lite.Document;

import java.util.LinkedList;

import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;


public class DBAdapterTest extends AndroidTestCase {


    private DBAdapter testobject;


    /* Constructor */
    public DBAdapterTest() {
        super();
    }


    /* Setup test environment */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
        testobject = new DBAdapter(getContext());
    }


    /* test if adapter is set up correctly */
    public void testAdapterExists() {
        assertNotNull("adapter is null", testobject);
    }


    /* test if database is empty and therefor not correctly synchronized */
    // ToDo: Comment in after iss-hipm-123 is merged into development
    // public void testDatabaseNotEmpty() {
    //    assertTrue("database is empty!", testobject.getDocumentCount() > 0);
    //}


    /* test if the Stadtroute is retrieved correctly */
    public void testGetStadtroute() {
        RouteSet routeSet = new RouteSet(testobject.getView("routes"));
        assertNotNull(routeSet);

        for (Route route : routeSet.routes) {
            if (route.title == "Stadtroute") {
                /* found Stadtroute, check attributes */
                assertEquals(route.description, "Dies ist eine kurze Route in der Stadt.");
                assertEquals(route.duration, 7200);
                assertEquals(route.imageName, "route_stadt.jpg");

                /* check tags */
                assertEquals(route.tags.size(), 1);
                for (RouteTag tag : route.tags) {
                    assertEquals(tag.getImage(), null);
                    assertEquals(tag.getImageFilename(), "route_tag_restaurant");
                    assertEquals(tag.getName(), "Restaurant");
                    assertEquals(tag.getTag(), "restaurant");
                }
                /* check waypoints */
                LinkedList<Waypoint> waypoint_list = new LinkedList<>();
                for (Waypoint waypoint : route.waypoints) {
                    waypoint_list.add(waypoint);
                }
                assertEquals(waypoint_list.size(), 6);

                assertEquals(waypoint_list.get(0).exhibit_id, 5);
                assertEquals(waypoint_list.get(0).latitude, 51.71859);
                assertEquals(waypoint_list.get(0).longitude, 8.752206);

                assertEquals(waypoint_list.get(1).exhibit_id, 1);
                assertEquals(waypoint_list.get(1).latitude, 51.719128);
                assertEquals(waypoint_list.get(1).longitude, 8.755457);

                assertEquals(waypoint_list.get(2).exhibit_id, 4);
                assertEquals(waypoint_list.get(2).latitude, 51.719527);
                assertEquals(waypoint_list.get(2).longitude, 8.755736);

                assertEquals(waypoint_list.get(3).exhibit_id, 6);
                assertEquals(waypoint_list.get(3).latitude, 51.718969);
                assertEquals(waypoint_list.get(3).longitude, 8.758472);

                assertEquals(waypoint_list.get(4).exhibit_id, -1);
                assertEquals(waypoint_list.get(4).latitude, 51.720371);
                assertEquals(waypoint_list.get(4).longitude, 8.761723);

                assertEquals(waypoint_list.get(5).exhibit_id, -1);
                assertEquals(waypoint_list.get(5).latitude, 51.719454);
                assertEquals(waypoint_list.get(5).longitude, 8.767484);

                break; // the other routes are unimportant, skip
            }
        }
    }


    /* test the exhibits view */
    public void testExhibitsView() {
        assertNotNull(testobject.getView("exhibits"));
        assertTrue(testobject.getView("exhibits").size() > 0);
    }


    /* test the getDocument() function */
    public void testGetDocument() {
        Document exhibit = testobject.getDocument(1);
        assertNotNull(exhibit);
        assertEquals(exhibit.getProperty("name"), "Paderborner Dom");
    }


    /* negative test if wrong document ids lead to an empty document */
    public void testNegativeGetDocument() {
        Document exhibit = testobject.getDocument(-1);
        assertNull(exhibit.getProperties());
    }
}