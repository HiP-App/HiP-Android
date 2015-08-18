package com.example.timo.hip;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Map;

import com.couchbase.lite.Document;

public class Exhibit {

    public int id;
    public String name;
    public String description;
    public LatLng latlng;
    public String[] categories;
    public String[] tags;
    public double distance;

    public Exhibit (Document document) {

        Map<String, Object> properties = document.getProperties();
        int id = Integer.valueOf(document.getId());
        String name = (String)properties.get(DBAdapter.KEY_NAME);
        String description = (String)properties.get(DBAdapter.KEY_DESCRIPTION);
        double lat = (double)properties.get(DBAdapter.KEY_LAT);
        double lng = (double)properties.get(DBAdapter.KEY_LNG);
        String categories = (String)properties.get(DBAdapter.KEY_CATEGORIES);
        String tags = (String)properties.get(DBAdapter.KEY_TAGS);

        this.id = id;
        this.name = name;
        this.description = description;
        this.latlng = new LatLng(lat, lng);
        this.categories = categories.split(",");
        this.tags = tags.split(",");
    }

    public Exhibit (int id, String name, String description, double lat, double lng, String categories, String tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latlng = new LatLng(lat, lng);
        this.categories = categories.split(",");
        this.tags = tags.split(",");
    }

    public void setDistance (LatLng position) {
        this.distance = SphericalUtil.computeDistanceBetween(position, this.latlng);
    }
}
