package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String BASE_URL = "http://tboegeholz.de/ba/index.php";

    public LatLng paderborn = new LatLng(51.7276064, 8.7684325);

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private DBAdapter database;
    private ExhibitSet exhibitSet;
    private List<String> activeFilter = new ArrayList<>();

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
        //LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        openDatabase();
        this.exhibitSet = new ExhibitSet(database.getView("exhibits"), this.paderborn);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, new ExtendedLocationListener(this));

        setUpMapIfNeeded();

        // Set ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            //toolbar.inflateMenu(R.menu.menu_main);
            setSupportActionBar(toolbar);
        }

        // Recyler View
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


        //swipe_container
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        final MainActivity mMainActivity = this;
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ImageLoader imgLoader = new ImageLoader(mMainActivity);
                imgLoader.clearCache();
            }
        });

    }

    public void notifyExhibitSetChanged() {
        mSwipeLayout.setRefreshing(false);
        this.exhibitSet = new ExhibitSet(database.getView("exhibits"), this.paderborn);
        mAdapter = new RecyclerAdapter(this.exhibitSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        this.mAdapter.notifyDataSetChanged();
        this.exhibitSet.addMarker(this.mMap);
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
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) this.updatePosition(mLastLocation);
        startLocationUpdates();
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
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(this.paderborn, 12);
        mMap.animateCamera(update);

        exhibitSet.addMarker(mMap);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}