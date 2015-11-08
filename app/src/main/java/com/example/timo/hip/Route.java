package com.example.timo.hip;

import com.couchbase.lite.Document;

import java.util.LinkedList;

public class Route {

    public int id;
    public String title;
    public String description;
    public LinkedList<Waypoint> waypoints;

    public Route (Document document) {

    // STUB

    }

    public Route (int id, String title, String description, LinkedList<Waypoint> waypoints)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.waypoints = waypoints;
    }
}
