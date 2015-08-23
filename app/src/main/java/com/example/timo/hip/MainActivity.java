package com.example.timo.hip;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String BASE_URL = "http://tboegeholz.de/ba/index.php";
    public LatLng paderborn = new LatLng(51.7276064, 8.7684325);

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager locationManager;
    private DBAdapter database;
    private ExhibitSet exhibitSet;
    private List<String> activeFilter = new ArrayList<>();
    private Polyline newPolyline;
    private String routeMode = GMapV2Direction.MODE_WALKING;

    private ExtendedLocationListener mLocationListener = new ExtendedLocationListener(this);

    // Recycler View: MainList
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Recycler View: Filter
    private RecyclerView mFilterRecyclerView;
    private RecyclerView.Adapter mFilterAdapter;
    private RecyclerView.LayoutManager mFilterLayoutManager;

    // Refresh
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Location Manager
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        openDatabase();

        this.exhibitSet = new ExhibitSet(database.getAllRows(), paderborn);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, new ExtendedLocationListener(this));

        setUpMapIfNeeded();

        // Recyler View‚
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new RecyclerAdapter(this.exhibitSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        //getWindow().setExitTransition(new Explode());

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this));

        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycler_view);
        mFilterRecyclerView.setHasFixedSize(true);
        mFilterLayoutManager = new LinearLayoutManager(this);
        mFilterRecyclerView.setLayoutManager(mFilterLayoutManager);
        List<String> categories = this.exhibitSet.getCategories();
        for(String item: categories) this.activeFilter.add(item);
        mFilterAdapter = new FilterRecyclerAdapter(categories, this.activeFilter);
        mFilterRecyclerView.setAdapter(mFilterAdapter);
        mFilterRecyclerView.addOnItemTouchListener(new FilterRecyclerClickListener(this));

        // if(this.exhibitSet.getSize() == 0) new HttpAsyncTask(this).execute(BASE_URL);

        //swipe_container
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        final MainActivity mMainActivity = this;
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ImageLoader imgLoader = new ImageLoader(mMainActivity);
                imgLoader.clearCache();
                new HttpAsyncTask(mMainActivity).execute(BASE_URL);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("SHOW_ROUTE");
            if (value == 1){
                onShowRoute();
            }
        }

    }

    public void notifyExhibitSetChanged() {
        mSwipeLayout.setRefreshing(false);

        LatLng latLang;
        if (mLastLocation == null){
            latLang = paderborn;
        }
        else{
            latLang = new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude());
        }

        this.exhibitSet = new ExhibitSet(database.getAllRows(), latLang);
        mAdapter = new RecyclerAdapter(this.exhibitSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        this.mAdapter.notifyDataSetChanged();
        this.exhibitSet.addMarker(this.mMap);

        if( Build.PRODUCT.matches(".*_?ssdk_?.*")) {
            mMap.addMarker(getCurrentLocationMarkerOptions());
        }
    }

    public void updateCategories(String categorie) {
        if(categorie != null) {
            if(this.activeFilter.contains(categorie)) this.activeFilter.remove(categorie);
            else this.activeFilter.add(categorie);
            this.mFilterAdapter.notifyDataSetChanged();
            this.exhibitSet.updateCategories(this.activeFilter);
            this.mAdapter.notifyDataSetChanged();
            this.exhibitSet.addMarker(this.mMap);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.show_route:
                onShowRoute();
                return true;

            case R.id.quit:
                onQuit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onShowRoute() {
        if (mLastLocation == null){
            Toast.makeText(this, "mLastLocation is null!", Toast.LENGTH_LONG).show();
            return;
        }

        LatLng myLastLoc = new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude());

        for(int i = 0 ; i < this.exhibitSet.getSize(); i++)
        {
            LatLng nextLocation = this.exhibitSet.getExhibit(i).latlng;

            Map<String, String> map = new HashMap<>();
            map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(myLastLoc.latitude));
            map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(myLastLoc.longitude));
            map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(nextLocation.latitude));
            map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(nextLocation.longitude));
            map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, routeMode);

            GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
            asyncTask.execute(map);

            myLastLoc = nextLocation;
        }
    }

    public void onQuit() {
        // Quit Application
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); 
        mLastLocation = getLastKnownLocation();

        if(mLastLocation != null)
            this.updatePosition(mLastLocation);

        startLocationUpdates();

        if (Build.PRODUCT.matches(".*_?sdk_?.*")) {
            mMap.addMarker(getCurrentLocationMarkerOptions());
        }
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void updatePosition(Location position) {
        mLastLocation = position;
        this.exhibitSet.updatePosition(new LatLng(position.getLatitude(), position.getLongitude()));
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openDatabase() {
        database = new DBAdapter(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(paderborn, 12);
        mMap.animateCamera(update);

        exhibitSet.addMarker(mMap);
    }

    private MarkerOptions getCurrentLocationMarkerOptions()
    {
        return new MarkerOptions()
                .position(new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude()))
                .title("I'm here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    public Location getLastKnownLocation() {
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if (Build.PRODUCT.matches(".*_?ssdk_?.*")) {
            // set dummy location
            LatLng myStartLocation = new LatLng(51.712979, 8.740505); // Paderborn Hbf
            if( bestLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                String provider = locationManager.getBestProvider(criteria, false);
                bestLocation = new Location(provider);

                bestLocation.setLatitude(myStartLocation.latitude);
                bestLocation.setLongitude(myStartLocation.longitude);
            }
        }

        return bestLocation;
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {

        PolylineOptions rectLine = new PolylineOptions();

        if (newPolyline != null)
        {
            newPolyline.remove();
        }

        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
            newPolyline = mMap.addPolyline(rectLine);

            if (routeMode.equals(GMapV2Direction.MODE_DRIVING))
            {
                rectLine.width(10).color(Color.BLUE);
            }
            else
            {
                rectLine.width(10).color(Color.RED);
            }

            newPolyline = mMap.addPolyline(rectLine);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void openImageView() {
        Intent intent = new Intent(this, DisplayImageView.class);
        startActivity(intent);
    }
}
