package com.example.timo.hip;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.LinkedList;

public class Route {

    public int id;
    public String title;
    public String description;
    public ArrayList<Waypoint> waypoints;
    public int duration; //in seconds

    public Route (Document document) {

    // STUB

    }

    public Route (int id, String title, String description, ArrayList<Waypoint> waypoints, int duration)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.waypoints = waypoints;
        this.duration = duration;
    }
}
