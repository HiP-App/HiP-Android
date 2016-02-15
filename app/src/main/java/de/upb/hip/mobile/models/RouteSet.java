package de.upb.hip.mobile.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.upb.hip.mobile.adapters.DBAdapter;

public class RouteSet implements Serializable {

    public List<Route> routes = new ArrayList<>();

    public RouteSet() {

    }

    public RouteSet(List<Map<String, Object>> list) {

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            int id = Integer.valueOf((String) properties.get(DBAdapter.KEY_ID));
            String title = (String) properties.get(DBAdapter.KEY_ROUTE_TITLE);
            String description = (String) properties.get(DBAdapter.KEY_ROUTE_DESCRIPTION);
            ArrayList<Map<String, Object>> waypoints = (ArrayList<Map<String, Object>>) properties.get(DBAdapter.KEY_ROUTE_WAYPOINTS);
           // Map waypoints = (Map) properties.get(DBAdapter.KEY_ROUTE_WAYPOINTS);
            int duration = (Integer) properties.get(DBAdapter.KEY_ROUTE_DURATION);

            //Need to deserialize tags manually since CouchDB doesn't seem to do it automatically
            List<Map> tagList = (List<Map>) properties.get(DBAdapter.KEY_ROUTE_TAGS);
            List<RouteTag> tags = new LinkedList<>();
            for (Map<String, String> tagMap : tagList) {
                tags.add(new RouteTag(tagMap.get("tag"), tagMap.get("name"), tagMap.get("imageFilename")));
            }

            ArrayList<Waypoint> waypointsDeserialized = new ArrayList<>();
            for(Map<String, Object> map: waypoints){
                for(String key: map.keySet()){
                    Log.i("routes", key + ": " + map.get(key).toString());
                }
                //Make sure the DB actually contains these keys
                if(!map.containsKey("latitude") || !map.containsKey("longitude") || !map.containsKey("exhibit_id")){
                    continue;
                }
                double latitude = (Double) map.get("latitude");
                double longitude = (Double) map.get("longitude");
                int exhibit_id = (Integer) map.get("exhibit_id");
                waypointsDeserialized.add(new Waypoint(latitude, longitude, exhibit_id));
            }

            String imageName = (String) properties.get(DBAdapter.KEY_ROUTE_IMAGE_NAME);
            Route route = new Route(id, title, description, waypointsDeserialized, duration, tags, imageName);

            routes.add(route);
        }
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }


    public Route getRouteByPosition(int position) {
        return routes.get(position);
    }

    public Route getRouteById(int id) {
        for (Route route : routes) {
            if (route.id == id) {
                return route;
            }
        }
        return null;
    }

    public int getSize() {
        return routes.size();
    }

    public void addRoute(Route r)
    {
        this.routes.add(r);
    }
}