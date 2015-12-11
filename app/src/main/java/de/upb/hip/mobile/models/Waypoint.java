package de.upb.hip.mobile.models;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


public class Waypoint implements Serializable{

    public double latitude;
    public double longitude;
    public int exhibit_id;


    public Waypoint(double latitude, double longitude, int exhibit_id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.exhibit_id = exhibit_id;
    }

    public Exhibit getExhibit(DBAdapter db){
        return new Exhibit(db.getDocument(exhibit_id));
    }
}
