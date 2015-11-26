package de.upb.hip.mobile.models;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.activities.R;

/**
 * Represents the available tags for routes
 */
public class RouteTagSet {

    private Map<String, RouteTag> tagMap = new HashMap<>();

    public RouteTagSet(Context context) {
        RouteTag bar = new RouteTag("bar", "Bar", context.getResources().getDrawable(R.drawable.route_tag_bar));
        RouteTag restaurant = new RouteTag("restaurant", "Restaurant", context.getResources().getDrawable(R.drawable.route_tag_restaurant));
        tagMap.put(bar.getTag(), bar);
        tagMap.put(restaurant.getTag(), restaurant);
    }

    public RouteTag getTagById(String id) {
        return tagMap.get(id);
    }
}
