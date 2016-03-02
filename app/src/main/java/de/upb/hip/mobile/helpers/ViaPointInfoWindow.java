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

import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.activities.DetailsActivity;
import de.upb.hip.mobile.activities.R;

/**
 * A customized InfoWindow handling "itinerary" points (start, destination and via-points).
 * We inherit from MarkerInfoWindow as it already provides most of what we want.
 */
public class ViaPointInfoWindow extends MarkerInfoWindow {

    private Map<String, Integer> mViaPointData = new HashMap<>();
    private String title;

    public ViaPointInfoWindow(int layoutResId, MapView mapView, final Context context) {
        super(layoutResId, mapView);
        Button btnInfo = (Button) (mView.findViewById(R.id.bubble_info));
        btnInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mViaPointData.containsKey(title) && mViaPointData.get(title) != -1) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("exhibit-id", mViaPointData.get(title));
                    context.startActivity(intent);
                }
                close();
            }
        });
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        title = marker.getTitle();
        try {
            mViaPointData = (Map<String, Integer>) marker.getRelatedObject();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        super.onOpen(item);
    }

}