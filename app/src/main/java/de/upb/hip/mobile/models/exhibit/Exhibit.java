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

package de.upb.hip.mobile.models.exhibit;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.models.Image;

/**
 * Exhibit objects store general information to exhibit points
 * they have an id which should be unique, although in theory the same id can be assigned to
 * more than one exhibit
 */
public class Exhibit {

    private int mId;
    private String mName;
    private String mDescription;
    private LatLng mLatlng;
    private String[] mCategories;
    private String[] mTags;
    private Image mImage;
    private List<Page> mPages = new LinkedList<>();

    public Exhibit(int id, String name, String description, double lat,
                   double lng, String[] categories, String[] tags, Image image, List<Page> pages) {
        mId = id;
        mName = name;
        mDescription = description;
        mLatlng = new LatLng(lat, lng);
        mCategories = categories;
        mTags = tags;
        mImage = image;
        this.mPages = pages;
    }

    /**
     * calculates the distance from position to the exhibit location.
     *
     * @param position user location
     * @return
     */
    public double getDistance(LatLng position) {
        return SphericalUtil.computeDistanceBetween(position, mLatlng);
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public LatLng getLatlng() {
        return mLatlng;
    }

    public String[] getCategories() {
        return mCategories.clone();
    }

    public Image getImage() {
        return mImage;
    }

    public String[] getTags() {
        return mTags.clone();
    }

    public List<Page> getPages() {
        return mPages;
    }

}
