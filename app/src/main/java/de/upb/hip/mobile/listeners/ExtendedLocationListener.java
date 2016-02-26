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

import android.location.Location;
import android.os.Bundle;

import de.upb.hip.mobile.activities.MainActivity;

/**
 * Listener Class for updating the location.
 */
public class ExtendedLocationListener implements com.google.android.gms.location.LocationListener {
    private MainActivity mActivity;

    /**
     * Constructor of ExtendedLocationListener
     *
     * @param mActivity MainActivity
     */
    public ExtendedLocationListener(MainActivity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    /**
     * Calls {@link de.upb.hip.mobile.activities.MainActivity[#updatePosition(Location)}]}
     *
     * @param location Android Location
     */
    public void onLocationChanged(Location location) {
        this.mActivity.updatePosition(location);
    }
}
