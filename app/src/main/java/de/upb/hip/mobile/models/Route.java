package de.upb.hip.mobile.models;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.List;

public class Route {

    public int id;
    public String title;
    public String description;
    public ArrayList<Waypoint> waypoints;
    public int duration; //in seconds
    public List<String> tags;

    public Route (Document document) {

    // STUB

    }

    public Route (int id, String title, String description, ArrayList<Waypoint> waypoints, int duration, List<String> tags)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.waypoints = waypoints;
        this.duration = duration;
        this.tags = tags;
    }
}
