package de.upb.hip.mobile.models;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

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
