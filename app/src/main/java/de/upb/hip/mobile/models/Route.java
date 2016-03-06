package de.upb.hip.mobile.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model Class the route.
 */
public class Route implements Serializable {

    public int id;
    public String title;
    public String description;
    public ArrayList<Waypoint> waypoints;
    public int duration; //in seconds
    public double distance; //in km
    public List<RouteTag> tags;
    public String imageName;

    /**
     * Constructor for route.
     *
     * @param id          The id of the route.
     * @param title       The title of the route.
     * @param description The description of the route.
     * @param wayPoints   The way points of the route.
     * @param duration    The duration of the route in seconds.
     * @param distance    The distance of the route in kilometer.
     * @param tags        The tags of the route.
     * @param imageName   The name of the image, that belongs to the route.
     */
    public Route(int id, String title, String description, ArrayList<Waypoint> wayPoints,
                 int duration, double distance, List<RouteTag> tags, String imageName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.waypoints = wayPoints;
        this.duration = duration;
        this.distance = distance;
        this.tags = tags;
        this.imageName = imageName;
    }
}
