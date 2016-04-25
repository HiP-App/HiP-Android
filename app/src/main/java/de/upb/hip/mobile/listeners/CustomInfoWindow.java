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

package de.upb.hip.mobile.listeners;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * This class customizes the info window that is opened when clicking a POI marker on the map
 */
public class CustomInfoWindow extends MarkerInfoWindow {
    public CustomInfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
        Button btn = (Button)(mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));

    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        POI poi = (POI) ((Marker) item).getRelatedObject();
        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
        String title = "";
        //The title is before the first "," in the  string
        String[] split = poi.mDescription.split(",");
        if (split.length > 0) {
            title = split[0];
        } else {
            title = poi.mDescription;
        }
        ((TextView) mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_title)).setText(title);
        ((TextView) mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_description)).setText("");
    }
}
