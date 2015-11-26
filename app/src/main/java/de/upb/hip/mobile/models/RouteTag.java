package de.upb.hip.mobile.models;

import android.graphics.drawable.Drawable;

/**
 * Represents a tag for a route
 */
public class RouteTag {

    private String tag;
    private String name;
    private Drawable image;

    public RouteTag(String tag, String name, Drawable image) {
        this.tag = tag;
        this.name = name;
        this.image = image;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public Drawable getImage() {
        return image;
    }

}
