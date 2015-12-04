package de.upb.hip.mobile.models;

import com.couchbase.lite.Document;
import com.google.android.gms.internal.im;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Route implements Serializable{

    public int id;
    public String title;
    public String description;
    public ArrayList<Waypoint> waypoints;
    public int duration; //in seconds
    public List<RouteTag> tags;
    public String imageName;

    public Route (Document document) {

    // STUB

    }

    public Route (int id, String title, String description, ArrayList<Waypoint> waypoints, int duration, List<RouteTag> tags, String imageName)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.waypoints = waypoints;
        this.duration = duration;
        this.tags = tags;
        this.imageName = imageName;
    }
}
