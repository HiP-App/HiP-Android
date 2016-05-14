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

package de.upb.hip.mobile.helpers.db;

import com.couchbase.lite.Document;
import com.google.gson.Gson;

import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Route;

/**
 * A helper class for deserializing all objects related to routes
 * This class is specific to the CouchBase Database!
 */
public class RouteDeserializer {

    public static Route deserializeRoute(Document document) {
        return deserializeRoute(document.getProperties());
    }

    public static Route deserializeRoute(Map<String, Object> properties) {
        Gson gson = new Gson();
        return gson.fromJson((String) properties.get(DBAdapter.KEY_DATA), Route.class);
    }
}
