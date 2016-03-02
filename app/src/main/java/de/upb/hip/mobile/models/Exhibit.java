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

import de.upb.hip.mobile.adapters.DBAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.couchbase.lite.Document;

public class Exhibit {

    public int id;
    public String name;
    public String description;
    public LatLng latlng;
    public String[] categories;
    public String[] tags;
    public double distance;
    public int sliderID;
    public HashMap<String, String> pictureDescriptions;

    public Exhibit (Document document) {

        Map<String, Object> properties = document.getProperties();
        int id = Integer.valueOf(document.getId());
        String name = (String)properties.get(DBAdapter.KEY_EXHIBIT_NAME);
        String description = (String)properties.get(DBAdapter.KEY_EXHIBIT_DESCRIPTION);
        double lat = (double)properties.get(DBAdapter.KEY_EXHIBIT_LAT);
        double lng = (double)properties.get(DBAdapter.KEY_EXHIBIT_LNG);
        String categories = (String)properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES);
        String tags = (String)properties.get(DBAdapter.KEY_EXHIBIT_TAGS);
        int sliderID = (int)properties.get(DBAdapter.KEY_EXHIBIT_SLIDER_ID);

        this.pictureDescriptions = (LinkedHashMap<String, String>)document.getProperty(DBAdapter.KEY_EXHIBIT_PICTURE_DESCRIPTIONS);

        this.id = id;
        this.name = name;
        this.description = description;
        this.latlng = new LatLng(lat, lng);
        this.categories = categories.split(",");
        this.tags = tags.split(",");
        this.sliderID = sliderID;
    }

    public Exhibit (int id, String name, String description, double lat, double lng, String categories, String tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latlng = new LatLng(lat, lng);
        this.categories = categories.split(",");
        this.tags = tags.split(",");
    }

    public void setDistance (LatLng position) {
        this.distance = SphericalUtil.computeDistanceBetween(position, this.latlng);
    }
}
