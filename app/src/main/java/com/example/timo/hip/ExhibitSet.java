package com.example.timo.hip;

import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExhibitSet {

    private List<Exhibit> exhibits = new ArrayList<>();
    private LatLng position;

    public ExhibitSet (Cursor cursor, LatLng position){
        this.position = position;
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(DBAdapter.COL_ROWID);
                String name = cursor.getString(DBAdapter.COL_NAME);
                String description = cursor.getString(DBAdapter.COL_DESCRIPTION);
                double lat = cursor.getDouble(DBAdapter.COL_LAT);
                double lng = cursor.getDouble(DBAdapter.COL_LNG);
                String categories = cursor.getString(DBAdapter.COL_CATEGORIES);
                String tags = cursor.getString(DBAdapter.COL_TAGS);

                Exhibit exhibit = new Exhibit(id, name, description, lat, lng, categories, tags);
                exhibit.setDistance(this.position);

                this.exhibits.add(exhibit);
            } while (cursor.moveToNext());
            cursor.close();
        }

        this.orderByDistance();
    }

    public void updatePosition (LatLng position) {
        this.position = position;

        Iterator<Exhibit> iterator = exhibits.iterator();

        while(iterator.hasNext()) {
            Exhibit exhibit = iterator.next();
            exhibit.setDistance(this.position);
        }

        this.orderByDistance();
    }

//   TODO: Refactor ListSort! BAD Performance
    private void orderByDistance() {
        List<Exhibit> tmpList = new ArrayList<>();

        double minDistance = 0;
        int minPosition = 0;
        double currentDistance;
        int i = 0;

        while(this.exhibits.size() > 0) {
            currentDistance = this.exhibits.get(i).distance;
            if(minDistance == 0) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if(currentDistance < minDistance) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if(i == this.exhibits.size()-1) {
                tmpList.add(this.exhibits.remove(minPosition));
                minDistance = 0;
                i = 0;
            } else i++;
        }

        this.exhibits = tmpList;

        //this.exhibits = this.mergeSort(this.exhibits);
    }

    private List<Exhibit> mergeSort(List<Exhibit> list) {
        if (list.size() < 2) {
            return list;
        }
        else {
            List<Exhibit> left = list.subList(0, list.size()/2);
            List<Exhibit> right = list.subList(list.size()/2, list.size());
            left = this.mergeSort(left);
            right = this.mergeSort(right);
            return this.merge(left, right);
        }
    }

    private List<Exhibit> merge(List<Exhibit> left, List<Exhibit> right) {
        double leftDistance, rightDistance;
        List<Exhibit> list = new ArrayList<>();
        while(left.size() > 0 && right.size() > 0){
            leftDistance = SphericalUtil.computeDistanceBetween(this.position, left.get(0).latlng);
            rightDistance = SphericalUtil.computeDistanceBetween(this.position, right.get(0).latlng);
            if(leftDistance < rightDistance) {
                list.add(left.remove(0));
            } else {
                list.add(right.remove(0));
            }
        }
        while(left.size() > 0) {
            list.add(left.get(0));
            left.remove(0);
        }
        while(right.size() > 0) {
            list.add(right.get(0));
            right.remove(0);
        }
        return list;
    }

    public void addMarker(GoogleMap mMap) {
        mMap.clear();

        Iterator<Exhibit> iterator = exhibits.iterator();

        while(iterator.hasNext()) {
            Exhibit exhibit = iterator.next();
            mMap.addMarker(new MarkerOptions().position(exhibit.latlng).title(exhibit.name));
        }
    }

    public Exhibit getExhibit(int position) {
        return exhibits.get(position);
    }

    public int getSize() {
        return exhibits.size();
    }
}
