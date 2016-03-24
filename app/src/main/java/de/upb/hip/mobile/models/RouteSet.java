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

package de.upb.hip.mobile.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Model for set of routes
 */
public class RouteSet implements Serializable {

    private List<Route> mRoutes = new ArrayList<>();

    public RouteSet() {

    }

    /**
     * Constructor for the RouteSet
     * Creates a list of route objects
     *
     * @param list which is returned from DBAdapter
     */
    public RouteSet(List<Map<String, Object>> list) {

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            int id = Integer.valueOf((String) properties.get(DBAdapter.KEY_ID));
            String title = (String) properties.get(DBAdapter.KEY_ROUTE_TITLE);
            String description = (String) properties.get(DBAdapter.KEY_ROUTE_DESCRIPTION);
            ArrayList<Map<String, Object>> waypoints =
                    (ArrayList<Map<String, Object>>) properties.get(DBAdapter.KEY_ROUTE_WAYPOINTS);
            // Map waypoints = (Map) properties.get(DBAdapter.KEY_ROUTE_WAYPOINTS);
            int duration = (Integer) properties.get(DBAdapter.KEY_ROUTE_DURATION);
            double distance = (Double) properties.get(DBAdapter.KEY_ROUTE_DISTANCE);

            // Need to deserialize tags manually since CouchDB doesn't seem to do it automatically
            List<Map> tagList = (List<Map>) properties.get(DBAdapter.KEY_ROUTE_TAGS);
            List<RouteTag> tags = new LinkedList<>();
            for (Map<String, String> tagMap : tagList) {
                tags.add(
                        new RouteTag(
                                tagMap.get("tag"),
                                tagMap.get("name"),
                                tagMap.get("imageFilename")
                        )
                );
            }

            ArrayList<Waypoint> waypointsDeserialized = new ArrayList<>();
            for (Map<String, Object> map : waypoints) {
                for (String key : map.keySet()) {
                    Log.i("routes", key + ": " + map.get(key).toString());
                }

                //Make sure the DB actually contains these keys
                if (!map.containsKey("latitude")
                        || !map.containsKey("longitude")
                        || !map.containsKey("exhibit_id")
                        ) {
                    continue;
                }

                double latitude = (Double) map.get("latitude");
                double longitude = (Double) map.get("longitude");
                int exhibit_id = (Integer) map.get("exhibit_id");

                waypointsDeserialized.add(new Waypoint(latitude, longitude, exhibit_id));
            }

            String imageName = (String) properties.get(DBAdapter.KEY_ROUTE_IMAGE_NAME);

            Route route = new Route(
                    id,
                    title,
                    description,
                    waypointsDeserialized,
                    duration,
                    distance,
                    tags,
                    imageName
            );

            mRoutes.add(route);
        }
    }

    
    public Route getRouteByPosition(int position) {
        return mRoutes.get(position);
    }

    public Route getRouteById(int id) {
        for (Route route : mRoutes) {
            if (route.getId() == id) {
                return route;
            }
        }
        return null;
    }

    public int getSize() {
        return mRoutes.size();
    }

    public void addRoute(Route r) {
        this.mRoutes.add(r);
    }

    public List<Route> getRoutes() {
        return this.mRoutes;
    }

    public void setRoutes(List<Route> routes) {
        this.mRoutes = routes;
    }
}