package de.upb.hip.mobile.helpers;

import android.util.Log;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class OpenRouteServiceRoadManager extends RoadManager {

    private String baseURL = "http://openls.geog.uni-heidelberg.de/route?";

    private static String fomatWaypointForURL(final GeoPoint waypoint) {
        return waypoint.getLongitude() + "," + waypoint.getLatitude();
    }

    @Override
    public Road getRoad(ArrayList<GeoPoint> waypoints) {
        String url = buildRequestURL(waypoints);
        Log.i("routes", url);
        return null;
    }

    @Override
    public Road[] getRoads(ArrayList<GeoPoint> waypoints) {
        return new Road[]{getRoad(waypoints)};
    }

    private String buildRequestURL(ArrayList<GeoPoint> waypoints) {
        StringBuilder url = new StringBuilder();
        url.append(baseURL);
        if (waypoints.size() < 2) {
            return null;
        }
        url.append("&start=" + fomatWaypointForURL(waypoints.get(0)));
        url.append("&end=" + fomatWaypointForURL(waypoints.get(waypoints.size() - 1)));
        if (waypoints.size() > 2) {
            url.append("&via=");
            for (int i = 1; i < waypoints.size() - 1; i++) {
                url.append(fomatWaypointForURL(waypoints.get(i)) + " ");
            }
            url.deleteCharAt(url.length() - 1); //Remove last empty space
        }

        url.append("routepref=Pedestrian");


        return url.toString();
    }
}
