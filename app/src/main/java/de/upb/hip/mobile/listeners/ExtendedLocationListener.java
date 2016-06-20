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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;

/**
 * Listener Class for updating the location.
 */
public class ExtendedLocationListener extends Service implements LocationListener {

    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 2 meters
    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 2000; // 2 sec
    public static final LatLng PADERBORN_HBF = new LatLng(51.7189826, 8.754652599999986);
    private final Context mContext;
    // Declaring a Location Manager
    protected LocationManager mLocationManager;
    // Flag for GPS status
    private boolean mCanGetLocation = false;
    private Location mLocation; // Location

    /**
     * Constructor of ExtendedLocationListener
     *
     * @param context Android Context
     */
    public ExtendedLocationListener(Context context) {
        this.mContext = context;
        getLocation();
    }

    /**
     * Returns the current location of the device, if GPS or internet connection is available,
     * else it returns the last known location or null.
     *
     * @return Location
     */
    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            boolean networkEnabled = mLocationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled) {
                // No network provider is enabled
                mCanGetLocation = false;
            } else {
                this.mCanGetLocation = true;
                if (networkEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER);
                    }
                }

                // If GPS enabled, get mLatitude/mLongitude using GPS Services
                if (gpsEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    public void stopUsingGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(ExtendedLocationListener.this);
        }
    }


    /**
     * Return Latitude of last known location.
     * Returns 0 if there is no last known location.
     *
     * @return Latitude
     */
    public double getLatitude() {
        if (mLocation != null) {
            return mLocation.getLatitude();
        }

        return 0;
    }


    /**
     * Return Longitude of last known location.
     * Returns 0 if there is no last known location.
     *
     * @return Longitude
     */
    public double getLongitude() {
        if (mLocation != null) {
            return mLocation.getLongitude();
        }

        return 0;
    }

    /**
     * Getter for the used LocationManager.
     *
     * @return LocationManager
     */
    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    /**
     * Returns true if location could be found via GPS or internet connection.
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.mCanGetLocation;
    }


    /**
     * Shows a dialog window, with a message that GPS is disabled and provides a button that will
     * starts the settings app for the GPS settings.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.gps_settings);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.gps_not_enabled_message);

        // On pressing the Settings button.
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Sets the current location.
     * Calls the MainActivity's updatePosition method.
     *
     * @param location Location
     */
    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;

        Activity activity = (Activity) this.mContext;
        if (activity.getClass().equals(MainActivity.class)) {
            MainActivity mainActivity = (MainActivity) activity;
            //Disabled for the wissenschaftstage
            //mainActivity.updatePosition(location);
        }
    }

    /**
     * Method that must be declared because of the LocationListener interface, but does nothing.
     *
     * @param provider String
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * Method that must be declared because of the LocationListener interface, but does nothing.
     *
     * @param provider String
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * Method that must be declared because of the LocationListener interface, but does nothing.
     *
     * @param provider String
     * @param status   int
     * @param extras   Bundle
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * Method that must be declared because of the inheritance of the Service class,
     * but always returns null.
     *
     * @param arg0 Intent
     * @return null
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}