package de.upb.hip.mobile.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.adapters.DBAdapter;


public class ExhibitSet {

    private List<Exhibit> initSet = new ArrayList<>();
    private List<Exhibit> activeSet = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> activeFilter;
    private LatLng position;

    public ExhibitSet (List<Map<String, Object>> list, LatLng position){
        this.position = position;

        for (int i=0; i<list.size(); i++) {
            Map<String, Object> properties = list.get(i);
            int id = Integer.valueOf((String)properties.get(DBAdapter.KEY_ID));
            String name = (String)properties.get(DBAdapter.KEY_EXHIBIT_NAME);
            String description = (String)properties.get(DBAdapter.KEY_EXHIBIT_DESCRIPTION);
            double lat = (double)properties.get(DBAdapter.KEY_EXHIBIT_LAT);
            double lng = (double)properties.get(DBAdapter.KEY_EXHIBIT_LNG);
            String categories = (String)properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES);
            String tags = (String)properties.get(DBAdapter.KEY_EXHIBIT_TAGS);

            Exhibit exhibit = new Exhibit(id, name, description, lat, lng, categories, tags);
            exhibit.setDistance(this.position);

            for(String categorie: exhibit.categories) {
                if(!this.categories.contains(categorie)) this.categories.add(categorie);
            }

            this.initSet.add(exhibit);
        }

        for(Exhibit item: initSet) activeSet.add(item);

        this.orderByDistance();
    }

    public List<String> getCategories() {
        return categories;
    }

    public void updateCategories(List<String> strArray) {

        this.activeFilter = strArray;
        this.activeSet = new ArrayList<>();

        for(int i=0; i < strArray.size(); i++ ){
            Iterator<Exhibit> iterator = initSet.iterator();

            while(iterator.hasNext()) {
                Exhibit exhibit = iterator.next();

                if(Arrays.asList(exhibit.categories).contains(strArray.get(i))) {
                    this.activeSet.add(exhibit);
                }
            }
        }

        this.orderByDistance();

    }

    public void updatePosition (LatLng position) {
        this.position = position;

        Iterator<Exhibit> iterator = initSet.iterator();

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

        while(this.activeSet.size() > 0) {
            currentDistance = this.activeSet.get(i).distance;
            if(minDistance == 0) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if(currentDistance < minDistance) {
                minDistance = currentDistance;
                minPosition = i;
            }
            if(i == this.activeSet.size()-1) {
                tmpList.add(this.activeSet.remove(minPosition));
                minDistance = 0;
                i = 0;
            } else i++;
        }

        this.activeSet = tmpList;

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

    public void addMarker(SetMarker mMarker, Context ctx) {
        mMarker.mFolderOverlay.closeAllInfoWindows();
        mMarker.mFolderOverlay.getItems().clear();

        Iterator<Exhibit> iterator = activeSet.iterator();

        while(iterator.hasNext()) {
            Exhibit exhibit = iterator.next();
            Drawable d = DBAdapter.getImage(exhibit.id, "image.jpg", 32);

            Map<String, Integer> data = new HashMap<>();
            data.put(exhibit.name, exhibit.id);

            Drawable icon = ContextCompat.getDrawable(ctx, R.drawable.marker_via);

            mMarker.addMarker(null, exhibit.name, exhibit.description,
                    new GeoPoint(exhibit.latlng.latitude, exhibit.latlng.longitude), d, icon, data);

        }
    }

    public Exhibit getExhibit(int position) {
        return activeSet.get(position);
    }

    public int getSize() {
        return activeSet.size();
    }
}
