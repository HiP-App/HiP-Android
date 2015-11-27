package de.upb.hip.mobile.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RouteSet {

    public List<Route> routes = new ArrayList<>();

    public RouteSet(List<Map<String, Object>> list) {

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            int id = Integer.valueOf((String) properties.get("_id"));
            String title = (String) properties.get("title");
            String description = (String) properties.get("description");
            ArrayList<Waypoint> waypoints = (ArrayList<Waypoint>) properties.get("waypoints");
            int duration = (Integer) properties.get("duration");

            //Need to deserialize tags manually since CouchDB doesn't seem to do it automatically
            List<Map> tagList = (List<Map>) properties.get("tags");
            List<RouteTag> tags = new LinkedList<>();
            for(Map<String, String> tagMap: tagList){
                tags.add(new RouteTag(tagMap.get("tag"), tagMap.get("name"), tagMap.get("imageFilename")));
            }
            Route route = new Route(id, title, description, waypoints, duration, tags);

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
