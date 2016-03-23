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

import org.osmdroid.util.GeoPoint;

/**
 * Holds data for a specific point the route should pass through
 * Note that this may but does not have to be connected to an exhibit
 */
public class ViaPointData {
    private GeoPoint mGeo = new GeoPoint(51.7276064, 8.7684325); // Paderborn
    private String mTitle = "";
    private String mDescription = "";
    private int mExhibitId = -1;

    /**
     * Default constructor initializes class with dummy data
     */
    public ViaPointData() {

    }

    public ViaPointData(GeoPoint geo, String title, String description, int exhibitId) {
        setViaPointData(geo, title, description, exhibitId);
    }

    public void setViaPointData(GeoPoint geo, String title, String description, int exhibitId) {
        this.mGeo = geo;
        this.mTitle = title;
        this.mDescription = description;
        this.mExhibitId = exhibitId;
    }

    public GeoPoint getGeoPoint() {
        return mGeo;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public Integer getExhibitsId() {
        return mExhibitId;
    }
}