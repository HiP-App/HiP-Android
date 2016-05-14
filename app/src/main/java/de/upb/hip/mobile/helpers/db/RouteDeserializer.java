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
