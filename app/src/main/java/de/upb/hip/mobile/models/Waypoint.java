package de.upb.hip.mobile.models;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import com.google.android.gms.maps.model.LatLng;


public class Waypoint {

    public LatLng latlng;
    public int exhibit_id;


    public Waypoint(LatLng latlng, int exhibit_id) {
        this.latlng = latlng;
        this.exhibit_id = exhibit_id;
    }
}
