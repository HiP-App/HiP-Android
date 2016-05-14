package de.upb.hip.mobile.helpers.db;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;

/**
 * A helper class for serializing all objects related to a route
 * This class is specific to the CouchBase Database!
 */
public class RouteSerializer {

    public static final String TAG = "route-serializer";

    public static void serializeRoute(Document document, Route route, Context mContext, DBDummyDataFiller filler) {
        Map<String, Object> properties = new HashMap<>();

        properties.put(DBAdapter.KEY_TYPE, "route");
        properties.put(DBAdapter.KEY_ROUTE_TITLE, route.getTitle());
        properties.put(DBAdapter.KEY_ROUTE_DESCRIPTION, route.getDescription());
        properties.put(DBAdapter.KEY_ROUTE_WAYPOINTS, route.getWayPoints());
        properties.put(DBAdapter.KEY_ROUTE_DURATION, route.getDuration());
        properties.put(DBAdapter.KEY_ROUTE_DISTANCE, route.getDistance());
        properties.put(DBAdapter.KEY_ROUTE_TAGS, route.getTags());
        properties.put(DBAdapter.KEY_ROUTE_IMAGE_NAME, route.getImageName());
        properties.put(DBAdapter.KEY_CHANNELS, "*");
        //KEY_CHANNELS "*" ensures the access for all users in the Couchbase database

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

        //Add images for route tags as attachment
        for (RouteTag tag : route.getTags()) {
            final int resId = mContext.getResources().getIdentifier(tag.getImageFilename(),
                    "drawable", mContext.getPackageName());
            if (resId != 0) {
                InputStream ress = mContext.getResources().openRawResource(+resId);
                filler.addAttachment(route.getId(), tag.getImageFilename(), "image/jpeg", ress);
            } else {
                Log.e("routes", "Could not load image tag resource for route " + route.getId());
            }
        }

        //Add route image as attachment
        //Use only the part before "." for the filename when accessing the Android resource
        final int resId = mContext.getResources().getIdentifier(route.getImageName().split("\\.")[0],
                "drawable", mContext.getPackageName());
        if (resId != 0) {
            InputStream ress = mContext.getResources().openRawResource(+resId);
            filler.addAttachment(route.getId(), route.getImageName(), "image/png", ress);
        } else {
            Log.e("routes", "Could not load image resource for route " + route.getId());
        }
    }
}
