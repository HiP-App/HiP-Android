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

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * A customized InfoWindow handling "itinerary" points (start, destination and via-points).
 * We inherit from MarkerInfoWindow as it already provides most of what we want.
 */
public class ViaPointInfoWindow extends MarkerInfoWindow {

    int mSelectedPoint;

    public ViaPointInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    @Override
    public void onOpen(Object item) {
        Marker eItem = (Marker) item;
        mSelectedPoint = (Integer) eItem.getRelatedObject();
        super.onOpen(item);
    }
}