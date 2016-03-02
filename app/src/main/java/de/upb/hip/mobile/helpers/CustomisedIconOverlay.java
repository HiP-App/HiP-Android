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
 * Used to customise location marker
 */
public class CustomisedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

    private List<OverlayItem> mOverlayItemArray;
    private ResourceProxy pResourceProxy;
    private Bitmap mLocationMarker;
    private Context mContext;

    public CustomisedIconOverlay(Context contex, Bitmap locationMarker,
                                 List<OverlayItem> pList,
                                 org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                 ResourceProxy pResourceProxy) {
        super(pList, pOnItemGestureListener, pResourceProxy);

        this.mContext = contex;
        this.mLocationMarker = locationMarker;
        this.mOverlayItemArray = pList;
        this.pResourceProxy = pResourceProxy;
    }

    @Override
    public void draw(Canvas canvas, MapView mapview, boolean arg2) {
        super.draw(canvas, mapview, arg2);

        if (!mOverlayItemArray.isEmpty()) {

            //overlayItemArray have only ONE element only, so I hard code to get(0)
            GeoPoint in = (GeoPoint) mOverlayItemArray.get(0).getPoint();

            Point out = new Point();
            mapview.getProjection().toPixels(in, out);

            if (mLocationMarker == null) {
                mLocationMarker = pResourceProxy.getBitmap(ResourceProxy.bitmap.person);
            }

            canvas.drawBitmap(mLocationMarker,
                    out.x - mLocationMarker.getWidth() / 2,  //shift the bitmap center
                    out.y - mLocationMarker.getHeight() / 2,  //shift the bitmap center
                    null);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
        //return super.onSingleTapUp(event, mapView);
        return true;
    }
}