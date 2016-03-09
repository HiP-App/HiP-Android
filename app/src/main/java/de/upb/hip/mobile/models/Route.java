/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.upb.hip.mobile.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model Class the route.
 */
public class Route implements Serializable {

    private int mId;
    private String mTitle;
    private String mDescription;
    private ArrayList<Waypoint> mWayPoints;
    private int mDuration; //in seconds
    private double mDistance; //in km
    private List<RouteTag> mTags;
    private String mImageName;

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
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mWayPoints = wayPoints;
        this.mDuration = duration;
        this.mDistance = distance;
        this.mTags = tags;
        this.mImageName = imageName;
    }

    /**
     * Getter for the route id.
     *
     * @return id
     */
    public int getId() {
        return this.mId;
    }

    /**
     * Getter for the route title.
     *
     * @return title
     */
    public String getTitle() {
        return this.mTitle;
    }

    /**
     * Getter for the route description.
     *
     * @return description
     */
    public String getDescription() {
        return this.mDescription;
    }

    /**
     * Getter for the route way points.
     *
     * @return way points
     */
    public ArrayList<Waypoint> getWayPoints() {
        return this.mWayPoints;
    }

    /**
     * Getter for the route duration.
     *
     * @return duration
     */
    public int getDuration() {
        return this.mDuration;
    }

    /**
     * Getter for the route distance.
     *
     * @return distance
     */
    public double getDistance() {
        return this.mDistance;
    }

    /**
     * Getter for the route tags.
     *
     * @return tags
     */
    public List<RouteTag> getTags() {
        return this.mTags;
    }

    /**
     * Getter for the routes image name.
     *
     * @return image name
     */
    public String getImageName() {
        return this.mImageName;
    }
}
