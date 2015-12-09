package de.upb.hip.mobile.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RouteSet implements Serializable {

    public List<Route> routes = new ArrayList<>();

    public RouteSet() {

    }

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
            for (Map<String, String> tagMap : tagList) {
                tags.add(new RouteTag(tagMap.get("tag"), tagMap.get("name"), tagMap.get("imageFilename")));
            }

            String imageName = (String) properties.get("imageName");
            Route route = new Route(id, title, description, waypoints, duration, tags, imageName);

            routes.add(route);
        }
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }


    public Route getRoute(int position) {
        return routes.get(position);
    }

    public int getSize() {
        return routes.size();
    }

}