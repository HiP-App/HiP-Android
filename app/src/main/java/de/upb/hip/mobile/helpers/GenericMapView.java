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
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

public class GenericMapView extends FrameLayout {

    protected MapView mMapView;

    public GenericMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTileProvider(MapTileProviderBase aTileProvider) {
        if (mMapView != null) {
            this.removeView(mMapView);
        }
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(this.getContext());
        MapView newMapView = new MapView(this.getContext(), resourceProxy, aTileProvider);

        if (mMapView != null) {
            //restore as much parameters as possible from previous mMap:
            IMapController mapController = newMapView.getController();
            mapController.setZoom(mMapView.getZoomLevel());
            mapController.setCenter(mMapView.getMapCenter());
            newMapView.setBuiltInZoomControls(true); //no way to get old setting
            newMapView.setMultiTouchControls(true); //no way to get old setting
            newMapView.setUseDataConnection(mMapView.useDataConnection());
            newMapView.setMapOrientation(mMapView.getMapOrientation());
            newMapView.setScrollableAreaLimit(mMapView.getScrollableAreaLimit());
            List<Overlay> overlays = mMapView.getOverlays();
            for (Overlay o : overlays)
                newMapView.getOverlays().add(o);
        }

        mMapView = newMapView;
        this.addView(mMapView);
    }

    public MapView getMapView() {
        return mMapView;
    }
}
