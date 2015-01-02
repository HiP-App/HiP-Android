package com.example.timo.hip;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Exhibit {

    public int id;
    public String name;
    public String description;
    public LatLng latlng;
    public String[] categories;
    public String[] tags;
    public double distance;

    public Exhibit (Cursor cursor) {
        if(cursor.moveToFirst()) {
            int id = cursor.getInt(DBAdapter.COL_ROWID);
            String name = cursor.getString(DBAdapter.COL_NAME);
            String description = cursor.getString(DBAdapter.COL_DESCRIPTION);
            double lat = cursor.getDouble(DBAdapter.COL_LAT);
            double lng = cursor.getDouble(DBAdapter.COL_LNG);
            String categories = cursor.getString(DBAdapter.COL_CATEGORIES);
            String tags = cursor.getString(DBAdapter.COL_TAGS);

            this.id = id;
            this.name = name;
            this.description = description;
            this.latlng = new LatLng(lat, lng);
            this.categories = categories.split(",");
            this.tags = tags.split(",");
        }
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
