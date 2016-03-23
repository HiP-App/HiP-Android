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

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import de.upb.hip.mobile.adapters.DBAdapter;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Exhibit {

    private int mId;
    private String mName;
    private String mDescription;
    private LatLng mLatlng;
    private String[] mCategories;
    private String[] mTags;
    private double mDistance;
    private int mSliderId;
    private HashMap<String, String> mPictureDescriptions;

    public Exhibit (Document document) {

        Map<String, Object> properties = document.getProperties();
        int id = Integer.valueOf(document.getId());
        String name = (String)properties.get(DBAdapter.KEY_EXHIBIT_NAME);
        String description = (String)properties.get(DBAdapter.KEY_EXHIBIT_DESCRIPTION);
        double lat = (double)properties.get(DBAdapter.KEY_EXHIBIT_LAT);
        double lng = (double)properties.get(DBAdapter.KEY_EXHIBIT_LNG);
        String categories = (String)properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES);
        String tags = (String)properties.get(DBAdapter.KEY_EXHIBIT_TAGS);
        int sliderId = (int)properties.get(DBAdapter.KEY_EXHIBIT_SLIDER_ID);

        mPictureDescriptions = (LinkedHashMap<String, String>)document.getProperty
                (DBAdapter.KEY_EXHIBIT_PICTURE_DESCRIPTIONS);

        mId = id;
        mName = name;
        mDescription = description;
        mLatlng = new LatLng(lat, lng);
        mCategories = categories.split(",");
        mTags = tags.split(",");
        mSliderId = sliderId;
    }

    public Exhibit (int id, String name, String description, double lat,
                    double lng, String categories, String tags) {
        mId = id;
        mName = name;
        mDescription = description;
        mLatlng = new LatLng(lat, lng);
        mCategories = categories.split(",");
        mTags = tags.split(",");
    }

    public void setDistance (LatLng position) {
        mDistance = SphericalUtil.computeDistanceBetween(position, mLatlng);
    }

    public int getId() {
        return mId;
    }

    public String getName(){
        return mName;
    }

    public String getDescription(){
        return mDescription;
    }

    public LatLng getLatlng() {
        return mLatlng;
    }

    public String[] getCategories() {
        return mCategories.clone();
    }

    public String[] getTags(){
        return mTags.clone();
    }

    public int getSliderId() {
        return mSliderId;
    }

    public HashMap<String, String> getPictureDescriptions(){
        return new HashMap<String, String>(mPictureDescriptions);   //return a new object
                // so as to leave this one intact, when the return object is changed
    }

    public double getDistance(){
        return mDistance;
    }
}
