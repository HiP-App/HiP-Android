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

package de.upb.hip.mobile.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.activities.ExhibitDetailsActivity;
import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A customized InfoWindow handling "itinerary" points (start, destination and via-points).
 * We inherit from MarkerInfoWindow as it already provides most of what we want.
 */
public class ViaPointInfoWindow extends MarkerInfoWindow {

    public static final String KEY_MARKER_EXHIBIT_NAME = "exhibitname";
    public static final String KEY_MARKER_EXHIBIT_PAGES = "exhibitpages";

    private Map<String, Object> mViaPointData = new HashMap<>();
    private String mTitle;

    /**
     * Constructor
     *
     * @param layoutResId integer for Resource ID from the layout
     * @param mapView     MapView
     * @param context     Context in which the Activity of the ViaPoint is started
     */
    public ViaPointInfoWindow(int layoutResId, MapView mapView, final Context context) {
        super(layoutResId, mapView);

        Button btnInfo = (Button) (mView.findViewById(R.id.bubble_info));
        btnInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mViaPointData.containsKey(KEY_MARKER_EXHIBIT_NAME)) {
                    Intent intent = new Intent(context, ExhibitDetailsActivity.class);
                    intent.putExtra(ExhibitDetailsActivity.INTENT_EXTRA_EXHIBIT_NAME,
                            (String) mViaPointData.get(KEY_MARKER_EXHIBIT_NAME));

                    intent.putExtra(ExhibitDetailsActivity.INTENT_EXTRA_EXHIBIT_PAGES,
                            (Serializable) (List<Page>) mViaPointData.get(KEY_MARKER_EXHIBIT_PAGES));
                    context.startActivity(intent);
                }
                close();
            }
        });
    }

    /**
     * Set variables on marker open
     *
     * @param item Marker as Object
     */
    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        mTitle = marker.getTitle();
        try {
            mViaPointData = (Map<String, Object>) marker.getRelatedObject();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        super.onOpen(item);
    }

}