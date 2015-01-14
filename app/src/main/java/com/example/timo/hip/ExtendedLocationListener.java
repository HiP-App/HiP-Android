package com.example.timo.hip;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class ExtendedLocationListener implements com.google.android.gms.location.LocationListener {
    MainActivity mActivity;

    public ExtendedLocationListener (MainActivity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    public void onLocationChanged(Location location) {
        Log.i("Location", location.toString());
        Log.i("Location", location.getLatitude() + "LAT");
        Log.i("Location", location.getLongitude() + "LNG");
        this.mActivity.updatePosition(location);
    }

    public void onProviderDisabled(String provider) {
        // required for interface, not used
    }

    public void onProviderEnabled(String provider) {
        // required for interface, not used
    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // required for interface, not used
    }
}
