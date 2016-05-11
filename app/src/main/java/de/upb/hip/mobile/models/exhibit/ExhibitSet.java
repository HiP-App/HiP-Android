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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.SetMarker;


/**
 * Model for an Set of Exhibits
 */
public class ExhibitSet {

    private List<Exhibit> mInitSet = new ArrayList<>();
    private List<Exhibit> mActiveSet = new ArrayList<>();
    private List<String> mCategories = new ArrayList<>();
    private LatLng mPosition;

    /**
     * Constructor for an set of exhibits
     *
     * @param list     list of exhibits as Map<String, Object>
     * @param position a LatLng Object with current position
     */
    public ExhibitSet(List<Map<String, Object>> list, LatLng position) {
        this.mPosition = position;

        // add all exhibits
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            Exhibit exhibit = ExhibitDeserializer.deserializeExhibit(properties);

            for (String categorie : exhibit.getCategories()) {
                if (!this.mCategories.contains(categorie)) this.mCategories.add(categorie);
            }

            this.mInitSet.add(exhibit);
        }

        for (Exhibit item : mInitSet) mActiveSet.add(item);

        this.orderByDistance();
    }


    /**
     * update the categories
     *
     * @param strArray array with categories as string
     */
    public void updateCategories(List<String> strArray) {

        this.mActiveSet = new ArrayList<>();

        for (int i = 0; i < strArray.size(); i++) {

            for (Exhibit exhibit : mInitSet) {
                if (Arrays.asList(exhibit.getCategories()).contains(strArray.get(i))) {
                    this.mActiveSet.add(exhibit);
                }
            }
        }

        this.orderByDistance();
    }

    /**
     * update the current position
     *
     * @param position current position as LatLng
     */
    public void updatePosition(LatLng position) {
        this.mPosition = position;

        this.orderByDistance();
    }


    /**
     * orders the exhibits by distance to the current position
     */
    private void orderByDistance() {
        List<Exhibit> tmpList = new ArrayList<>();

        double minDistance = 0;
        int minPosition = 0;
        double currentDistance;
        int i = 0;

        while (this.mActiveSet.size() > 0) {
            currentDistance = this.mActiveSet.get(i).getDistance(this.mPosition);
            if (minDistance == 0) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if (i == this.mActiveSet.size() - 1) {
                tmpList.add(this.mActiveSet.remove(minPosition));
                minDistance = 0;
                i = 0;
            } else i++;
        }

        this.mActiveSet = tmpList;
    }


    /**
     * adds a marker to the map
     *
     * @param mMarker the marker
     * @param ctx     actual context
     */
    public void addMarker(SetMarker mMarker, Context ctx) {
        mMarker.mFolderOverlay.closeAllInfoWindows();
        mMarker.mFolderOverlay.getItems().clear();

        for (Exhibit exhibit : mActiveSet) {
            Drawable d = DBAdapter.getImage(exhibit.getId(), "image.jpg", 32);

            Map<String, Integer> data = new HashMap<>();
            data.put(exhibit.getName(), exhibit.getId());

            Drawable icon = ContextCompat.getDrawable(ctx, R.drawable.marker_via);

            mMarker.addMarker(null, exhibit.getName(), exhibit.getDescription(),
                    new GeoPoint(exhibit.getLatlng().latitude, exhibit.getLatlng().longitude), d, icon, data);
        }
    }

    /**
     * getter for an exhibit
     *
     * @param position position of the exhibit in the ExhibitSet
     * @return Exhibit
     */
    public Exhibit getExhibit(int position) {
        return mActiveSet.get(position);
    }

    /**
     * gets the size of the exhibitSet
     *
     * @return size
     */
    public int getSize() {
        return mActiveSet.size();
    }
}
