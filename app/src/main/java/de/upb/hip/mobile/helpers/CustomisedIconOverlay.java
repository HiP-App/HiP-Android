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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Class to customise location marker on map
 */
public class CustomisedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

    private List<OverlayItem> mOverlayItemArray;
    private ResourceProxy mProxyResource;
    private Bitmap mLocationMarker;

    
    /**
     * Constructor, initializes local variables
     *
     * @param locationMarker         Bitmap of the marker
     * @param pList                  List of overlay items
     * @param pOnItemGestureListener gesture listener
     * @param mProxyResource         ResourceProxy
     */
    public CustomisedIconOverlay(Bitmap locationMarker,
                                 List<OverlayItem> pList,
                                 OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                 ResourceProxy mProxyResource) {
        super(pList, pOnItemGestureListener, mProxyResource);

        this.mLocationMarker = locationMarker;
        this.mOverlayItemArray = pList;
        this.mProxyResource = mProxyResource;
    }


    /**
     * draws the marker on the map
     *
     * @param canvas  canvas to draw to
     * @param mapview view of the map
     * @param arg2    Boolean needed for the parent class
     */
    @Override
    public void draw(Canvas canvas, MapView mapview, boolean arg2) {
        super.draw(canvas, mapview, arg2);

        if (!mOverlayItemArray.isEmpty()) {

            //overlayItemArray have only ONE element, so get(0) is possible
            GeoPoint in = (GeoPoint) mOverlayItemArray.get(0).getPoint();

            Point out = new Point();
            mapview.getProjection().toPixels(in, out);

            if (mLocationMarker == null) {
                mLocationMarker = mProxyResource.getBitmap(ResourceProxy.bitmap.person);
            }

            canvas.drawBitmap(mLocationMarker,
                    out.x - mLocationMarker.getWidth() / 2,  //shift the bitmap center
                    out.y - mLocationMarker.getHeight() / 2,  //shift the bitmap center
                    null);
        }
    }


    /**
     * Stub for tab up event on icon
     *
     * @param event   motion event on icon
     * @param mapView view of the map
     * @return always true
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
        return true;
    }
}