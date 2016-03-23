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

import android.graphics.drawable.Drawable;

import de.upb.hip.mobile.helpers.ViaPointInfoWindow;

import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;



/**
 * Create marker on the overlay and add it to the map
 */
public class SetMarker {
    public FolderOverlay mFolderOverlay;
    private MapView mMap;
    private ViaPointInfoWindow mViaPointInfoWindow;

    public SetMarker
            (MapView map, FolderOverlay folderOverlay, ViaPointInfoWindow viaPointInfoWindow) {
        this.mMap = map;
        this.mFolderOverlay = folderOverlay;
        this.mViaPointInfoWindow = viaPointInfoWindow;

/*        Overlay overlay = mMap.getOverlayManager().get(i);

        if ( overlay.getClass().equals(FolderOverlay.class) ) {
            ((FolderOverlay)mFolderOverlay).enableFollowLocation();
        }*/
    }

    public Marker addMarker(Marker marker, String title, String description, GeoPoint geoLocation,
                            Drawable image, Drawable icon, Object relatedObject) {

        if (marker == null) {
            marker = new Marker(mMap);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(mViaPointInfoWindow);
            marker.setDraggable(true);
            mFolderOverlay.add(marker);
        }

        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setPosition(geoLocation);
        marker.setIcon(icon);

        if (image != null) {
            marker.setImage(image);
        }

        if (relatedObject != null) {
            marker.setRelatedObject(relatedObject);
        }

        mMap.invalidate();

        return marker;
    }
}
