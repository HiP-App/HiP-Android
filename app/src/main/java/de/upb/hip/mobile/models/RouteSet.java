package de.upb.hip.mobile.models;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RouteSet {

    public List<Route> routes = new ArrayList<>();

    public RouteSet (List<Map<String, Object>> list){

        for (int i=0; i<list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            int id = Integer.valueOf((String) properties.get("_id"));
            String title = (String)properties.get("title");
            String description = (String)properties.get("description");
            ArrayList<Waypoint> waypoints = (ArrayList<Waypoint>)properties.get("waypoints");

            Route route = new Route(id, title, description, waypoints);

            routes.add(route);
        }
    }

    public Route getRoute(int position) {
        return routes.get(position);
    }

    public int getSize() {
        return routes.size();
    }

}
