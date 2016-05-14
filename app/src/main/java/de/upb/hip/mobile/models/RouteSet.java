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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.helpers.db.RouteDeserializer;

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

        for (Map<String, Object> properties : list) {
            Route route = RouteDeserializer.deserializeRoute(properties);
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