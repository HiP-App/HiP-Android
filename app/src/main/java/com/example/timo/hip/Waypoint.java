package com.example.timo.hip;

import com.google.android.gms.maps.model.LatLng;


public class Waypoint {

    public LatLng latlng;
    public int exhibit_id;


    public Waypoint(LatLng latlng, int exhibit_id) {
        this.latlng = latlng;
        this.exhibit_id = exhibit_id;
    }
}
