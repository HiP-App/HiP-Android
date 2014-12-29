package com.example.timo.hip;

import com.google.android.gms.maps.model.LatLng;

public class Exhibit {

    public int id;
    public String name;
    public String description;
    public LatLng latlng;
    public String[] categories;
    public String[] tags;

    public Exhibit (int id, String name, String description, double lat, double lng, String categories, String tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latlng = new LatLng(lat, lng);
        this.categories = categories.split(",");
        this.tags = tags.split(",");
    }
}
