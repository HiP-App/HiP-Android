package com.example.timo.hip;

import android.database.Cursor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExhibitSet {

    private List<Exhibit> exhibits = new ArrayList<Exhibit>();

    public ExhibitSet (Cursor cursor){
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(DBAdapter.COL_ROWID);
                String name = cursor.getString(DBAdapter.COL_NAME);
                String description = cursor.getString(DBAdapter.COL_DESCRIPTION);
                double lat = cursor.getDouble(DBAdapter.COL_LAT);
                double lng = cursor.getDouble(DBAdapter.COL_LNG);
                String categories = cursor.getString(DBAdapter.COL_CATEGORIES);
                String tags = cursor.getString(DBAdapter.COL_TAGS);

                this.exhibits.add(new Exhibit(id, name, description, lat, lng, categories, tags));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void addMarker(GoogleMap mMap) {
        mMap.clear();

        Iterator<Exhibit> iterator = exhibits.iterator();

        while(iterator.hasNext()) {
            Exhibit exhibit = iterator.next();
            mMap.addMarker(new MarkerOptions().position(new LatLng(exhibit.lat, exhibit.lng)).title(exhibit.name));
        }
    }

    public Exhibit getExhibit(int position) {
        return exhibits.get(position);
    }

    public int getSize() {
        return exhibits.size();
    }
}
