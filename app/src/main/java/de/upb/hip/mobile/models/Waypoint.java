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

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Model Class the way points.
 */
public class Waypoint implements Serializable {

    private double mLatitude;
    private double mLongitude;
    private int mExhibitId;

    /**
     * Constructor for Waypoint.
     *
     * @param latitude   double
     * @param longitude  double
     * @param exhibit_id int
     */
    public Waypoint(double latitude, double longitude, int exhibit_id) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mExhibitId = exhibit_id;
    }

    /**
     * Getter for the exhibit the way point belongs to.
     *
     * @param dbAdapter DBAdapter
     * @return Exhibit
     */
    public Exhibit getExhibit(DBAdapter dbAdapter) {
        return new Exhibit(dbAdapter.getDocument(mExhibitId));
    }

    /**
     * Getter for the latitude of the way point.
     *
     * @return latitude
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Getter for the longitude of the way point.
     *
     * @return longitude
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Getter for the exhibits id the way point belong to.
     *
     * @return exhibit id
     */
    public int getExhibitId() {
        return mExhibitId;
    }
}
