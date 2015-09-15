package com.example.timo.hip;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

public class GMapOldActivity extends FragmentActivity {

    public LatLng myStartLocation = new LatLng(51.7276064, 8.7684326); // Paderborn Hbf
    GroundOverlay mImageOverlay;
    GroundOverlayOptions mOverlay;
    double lat = 51.719391; // increase = move to north
    double lng = 8.754148;
    float zoom1 = 1223f; // increase = enlarge overlay
    float zoom2 = 399f;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap_old);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

        mMap.setMyLocationEnabled(true);
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
    }

    private void setUpMap() {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myStartLocation, 12);
        mMap.animateCamera(update);

        mOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map))
                .position(new LatLng(lat, lng), zoom1);
        mImageOverlay = mMap.addGroundOverlay(mOverlay);

    }

    private void perform_move(double x, double y, float zoom){
        mImageOverlay.remove();
        //mOverlay.visible(false);
        lat = lat+x;
        lng = lng+y;
        zoom1 = zoom1+zoom;
        mOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map))
                .position(new LatLng(lat, lng), zoom1);
        //TextView tlat = (TextView)findViewById(R.id.textView_lat);
        //TextView tlng = (TextView)findViewById(R.id.textView_lng);
        //tlat.setText(new Double(lat).toString());
        //tlng.setText(new Double(lng).toString());

        mImageOverlay = mMap.addGroundOverlay(mOverlay);
    }

    public void btn_in(View v) {
        perform_move(0,0,10);

    }

    public void btn_out(View v) {

    }

    public void btn_up(View v) {
        perform_move(0.001,0,0);
    }

    public void btn_down(View v) {
        perform_move(-0.001,0,0);
    }

    public void btn_left(View v) {
        perform_move(0,-0.001,0);
    }

    public void btn_right(View v) {
        perform_move(0,0.001,0);
    }
}
