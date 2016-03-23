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

        for (Route route : routeSet.getRoutes()) {
            if (route.getTitle() == "Stadtroute") {
                /* found Stadtroute, check attributes */
                assertEquals(route.getDescription(), "Dies ist eine kurze Route in der Stadt.");
                assertEquals(route.getDuration(), 7200);
                assertEquals(route.getImageName(), "route_stadt.jpg");

                /* check tags */
                assertEquals(route.getTags().size(), 1);
                for (RouteTag tag : route.getTags()) {
                    assertEquals(tag.getImage(), null);
                    assertEquals(tag.getImageFilename(), "route_tag_restaurant");
                    assertEquals(tag.getName(), "Restaurant");
                    assertEquals(tag.getTag(), "restaurant");
                }
                /* check waypoints */
                LinkedList<Waypoint> waypoint_list = new LinkedList<>();
                for (Waypoint waypoint : route.getWayPoints()) {
                    waypoint_list.add(waypoint);
                }
                assertEquals(waypoint_list.size(), 6);

                assertEquals(waypoint_list.get(0).getExhibitId(), 5);
                assertEquals(waypoint_list.get(0).getLatitude(), 51.71859);
                assertEquals(waypoint_list.get(0).getLongitude(), 8.752206);

                assertEquals(waypoint_list.get(1).getExhibitId(), 1);
                assertEquals(waypoint_list.get(1).getLatitude(), 51.719128);
                assertEquals(waypoint_list.get(1).getLongitude(), 8.755457);

                assertEquals(waypoint_list.get(2).getExhibitId(), 4);
                assertEquals(waypoint_list.get(2).getLatitude(), 51.719527);
                assertEquals(waypoint_list.get(2).getLongitude(), 8.755736);

                assertEquals(waypoint_list.get(3).getExhibitId(), 6);
                assertEquals(waypoint_list.get(3).getLatitude(), 51.718969);
                assertEquals(waypoint_list.get(3).getLongitude(), 8.758472);

                assertEquals(waypoint_list.get(4).getExhibitId(), -1);
                assertEquals(waypoint_list.get(4).getLatitude(), 51.720371);
                assertEquals(waypoint_list.get(4).getLongitude(), 8.761723);

                assertEquals(waypoint_list.get(5).getExhibitId(), -1);
                assertEquals(waypoint_list.get(5).getLatitude(), 51.719454);
                assertEquals(waypoint_list.get(5).getLongitude(), 8.767484);

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
