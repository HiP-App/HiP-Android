/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.upb.hip.mobile.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.model.LatLng;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.adapters.MainRecyclerAdapter;
import de.upb.hip.mobile.helpers.CustomisedIconOverlay;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.helpers.ViaPointInfoWindow;
import de.upb.hip.mobile.listeners.ExtendedLocationListener;
import de.upb.hip.mobile.listeners.RecyclerItemClickListener;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.SetMarker;
import de.upb.hip.mobile.models.Waypoint;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.ExhibitSet;


/**
 * Main Activity for the App
 */
public class MainActivity extends BaseActivity {

    //Required since the MainActivity should statically display the Karlsroute
    public static final int KARLSROUTE_DB_ID = 101;

    private DBAdapter mDatabase;
    private ExhibitSet mExhibitSet;
    private List<String> mActiveFilter = new ArrayList<>();

    // Recycler View: MainList
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    // map
    private ExtendedLocationListener mGpsTracker;
    private GeoPoint mGeoLocation;
    private MapView mMap;
    private SetMarker mMarker;
    private FolderOverlay mItineraryMarkers;
    private ViaPointInfoWindow mViaPointInfoWindow;
    private ArrayList<OverlayItem> mOverlayItemArray;

    // Refresh
    private SwipeRefreshLayout mSwipeLayout;


    /**
     * Initialises the app on startup
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if we have the necessary permissions and request them if we don't
        // Note that the app will still fail on first launch and needs to be restarted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        mGpsTracker = new ExtendedLocationListener(MainActivity.this);

        // getting location
        if (mGpsTracker.canGetLocation()) {
            mGeoLocation = new GeoPoint(mGpsTracker.getLatitude(), mGpsTracker.getLongitude());
        } else {
            // set default location Paderborn Hbf but not show it on the map
            mGeoLocation = new GeoPoint(ExtendedLocationListener.PADERBORN_HBF.latitude,
                    ExtendedLocationListener.PADERBORN_HBF.longitude);
        }

        // TODO Remove this as soon as no needs to run in emulator
        // set default coordinats for emulator
        //Always enabled for the wissenschaftstage
        if (Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK")  || true) {
            mGeoLocation = new GeoPoint(ExtendedLocationListener.PADERBORN_HBF.latitude,
                    ExtendedLocationListener.PADERBORN_HBF.longitude);
        }


        // init map and update current location overlay
        setupMap();

        // update our current location immediately, instead of waiting when it would be updated
        // from locationlistner
        updateOverlayLocation(mGeoLocation);

        // get exhibits
        mDatabase = new DBAdapter(this);
        this.mExhibitSet = new ExhibitSet(mDatabase.getView("exhibits"),
                new LatLng(mGeoLocation.getLatitude(), mGeoLocation.getLongitude()));

        // add markers on the map for exhibits
        mMarker = new SetMarker(mMap, mItineraryMarkers, mViaPointInfoWindow);
        this.mExhibitSet.addMarker(mMarker, this);

        // Recyler View
        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MainRecyclerAdapter(this.mExhibitSet, new LatLng(mGeoLocation.getLatitude(), mGeoLocation.getLongitude()), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mExhibitSet));

        // detecting that the current view is compleatly created and then
        // zoom to boundingbox on map
        // it should be done only if the view completely created
        final View view = this.findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    /**
                     * Called when layout is fully loaded, zoom then to bounding box
                     */
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        BoundingBoxE6 boundingBoxE6 = getBoundingBoxE6();
                        if (boundingBoxE6 != null) {
                            mMap.zoomToBoundingBox(boundingBoxE6, false);
                        }
                    }
                });

        //setUp navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.mainActivityDrawerLayout);
        super.setUpNavigationDrawer(this, mDrawerLayout);

        //swipe_container
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeContainer);
        mSwipeLayout.setEnabled(false);

        showRouteOnMap();

        //HockeyApp Code
        checkForUpdates();
    }

    private void showRouteOnMap() {
        RouteSet routeSet = new RouteSet(mDatabase.getView("routes"));
        Route route = null;

        // start with every tag allowed
        for (Route routeIt : routeSet.getRoutes()) {
            if (routeIt.getId() == KARLSROUTE_DB_ID) {
                route = routeIt;
            }
        }

        if (route != null) {
            //We found the route, show it on the map
            drawPathOnMap(route);
        }
    }

    /**
     * Paint simple road lines with blue color. PathOverlay is deprecated, but for drawing simple
     * path is perfect.
     * The new, not deprecated class Polylines is more complex and needs a road from RoadManager
     */
    @SuppressWarnings("deprecation")
    private void drawPathOnMap(Route mRoute) {
        PathOverlay myPath = new PathOverlay(getResources().getColor(R.color.colorPrimaryDark),
                10, new DefaultResourceProxyImpl(this));

        if (mGeoLocation != null) {
            myPath.addPoint(mGeoLocation);
        }

        if (mRoute != null && mRoute.getWayPoints() != null) {
            for (Waypoint waypoint : mRoute.getWayPoints()) {
                myPath.addPoint(new GeoPoint(waypoint.getLatitude(), waypoint.getLongitude()));
            }
        }

        mMap.getOverlays().add(myPath);
        mMap.invalidate();
    }


    /**
     * initialize the map
     */
    private void setupMap() {
        // getting the map
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.mainMap);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        mMap = genericMap.getMapView();

        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setTilesScaledToDpi(true);
        mMap.setMaxZoomLevel(RouteDetailsActivity.MAX_ZOOM_LEVEL);

        // mMap prefs:
        IMapController mapController = mMap.getController();
        mapController.setZoom(RouteDetailsActivity.ZOOM_LEVEL);
        if (mGeoLocation != null) {
            // set center to current location
            mapController.setCenter(mGeoLocation);
        }

        // init info window for the markers
        mViaPointInfoWindow = new ViaPointInfoWindow(
                R.layout.navigation_info_window, mMap, this);

        // Create location Overlay
        mOverlayItemArray = new ArrayList<>();

        DefaultResourceProxyImpl defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);

        // to use blue point (or other) as location marker set it in CustomizedIconOverlay
        // Bitmap locationMarker =
        // BitmapFactory.decodeResource(getResources(), R.drawable.ic_location);
        CustomisedIconOverlay customisedIconOverlay = new CustomisedIconOverlay(null,
                mOverlayItemArray, null, defaultResourceProxyImpl);
        mMap.getOverlays().add(customisedIconOverlay);

        // add overlay for the markers
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);

        // add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(myScaleBarOverlay);
    }


    /**
     * getting boundingbox to fit all marker on the map
     * and current location also if it is known
     *
     * @return BoundingBox
     */
    public BoundingBoxE6 getBoundingBoxE6() {
        ArrayList<GeoPoint> points = new ArrayList<>();

        if (mGpsTracker.canGetLocation() && mGeoLocation != null) {
            points.add(mGeoLocation);
        }

        for (int i = 0; i < this.mExhibitSet.getSize(); i++) {
            Exhibit exhibit = this.mExhibitSet.getExhibit(i);
            GeoPoint geo = new GeoPoint(exhibit.getLatlng().latitude, exhibit.getLatlng().longitude);

            points.add(geo);
        }

        return BoundingBoxE6.fromGeoPoints(points);
    }


    /**
     * updates the information from ExhibitSet
     */
    public void notifyExhibitSetChanged() {
        mSwipeLayout.setRefreshing(false);
        mExhibitSet = new ExhibitSet(mDatabase.getView("exhibits"),
                new LatLng(mGeoLocation.getLatitude(), mGeoLocation.getLatitude()));
        mAdapter = new MainRecyclerAdapter(mExhibitSet, new LatLng(mGeoLocation.getLatitude(), mGeoLocation.getLongitude()), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mExhibitSet.addMarker(mMarker, getApplicationContext());
    }


    /**
     * updates a categorie
     *
     * @param categorie categorie to update
     */
    public void updateCategories(String categorie) {
        if (categorie != null) {
            if (mActiveFilter.contains(categorie)) mActiveFilter.remove(categorie);
            else mActiveFilter.add(categorie);
            mExhibitSet.updateCategories(mActiveFilter);
            mAdapter.notifyDataSetChanged();
            mExhibitSet.addMarker(mMarker, getApplicationContext());
        }
    }


    /**
     * update position of device on the map
     *
     * @param location new location of the device
     */
    public void updatePosition(Location location) {
        mExhibitSet.updatePosition(new LatLng(location.getLatitude(), location.getLongitude()));
        mAdapter.notifyDataSetChanged();

        updateOverlayLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }


    /**
     * updates the map overlay with a new position
     *
     * @param geoPoint new position of the device
     */
    public void updateOverlayLocation(GeoPoint geoPoint) {
        mOverlayItemArray.clear();

        GeoPoint overlocGeoPoint = new GeoPoint(geoPoint);
        OverlayItem newMyLocationItem = new OverlayItem("", "", overlocGeoPoint);
        mOverlayItemArray.add(newMyLocationItem);

        mMap.invalidate();
    }


    /**
     * updates the position after resume
     */
    @Override
    protected void onResume() {
        mGpsTracker.getLocation();

        super.onResume();
        //HockeyApp Code
        checkForCrashes();
    }


    /**
     * destroys the app
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //HockeyApp Code
        unregisterManagers();
    }


    /**
     * stops the GPS if app is paused
     */
    @Override
    protected void onPause() {
        mGpsTracker.stopUsingGPS();

        super.onPause();

        //HockeyApp Code
        unregisterManagers();
    }


    /**
     * HockeyApp methods
     */
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}
