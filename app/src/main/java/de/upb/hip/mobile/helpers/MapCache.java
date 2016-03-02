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
import android.widget.Toast;

import org.osmdroid.bonuspack.cachemanager.CacheManager;
import org.osmdroid.views.MapView;

/**
 * explicit download and clear view area of the map
 * check the cache usage
 */
public class MapCache {

    private MapView mMap;
    private Context mContext;

    public MapCache(Context context, MapView map) {
        this.mContext = context;
        this.mMap = map;
    }

    public void downloadViewArea() {
        CacheManager cacheManager = new CacheManager(mMap);
        int zoomMin = mMap.getZoomLevel();
        int zoomMax = mMap.getZoomLevel() + 4;
        cacheManager.downloadAreaAsync(mContext, mMap.getBoundingBox(), zoomMin, zoomMax);
    }

    public void clearViewArea() {
        CacheManager cacheManager = new CacheManager(mMap);
        int zoomMin = mMap.getZoomLevel();
        int zoomMax = mMap.getZoomLevel() + 7;
        cacheManager.cleanAreaAsync(mContext, mMap.getBoundingBox(), zoomMin, zoomMax);
    }

    public void cacheUsage() {
        CacheManager cacheManager = new CacheManager(mMap);
        long cacheUsage = cacheManager.currentCacheUsage() / (1024 * 1024);
        long cacheCapacity = cacheManager.cacheCapacity() / (1024 * 1024);
        float percent = 100.0f * cacheUsage / cacheCapacity;
        String message = "Cache usage:\n" + cacheUsage + " Mo / " + cacheCapacity + " Mo = " + (int) percent + "%";
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
