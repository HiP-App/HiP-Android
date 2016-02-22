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

import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.adapters.RecyclerAdapter;
import de.upb.hip.mobile.helpers.CustomisedIconOverlay;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.helpers.ImageLoader;
import de.upb.hip.mobile.helpers.ViaPointInfoWindow;
import de.upb.hip.mobile.listeners.GPSTrackerListner;
import de.upb.hip.mobile.listeners.RecyclerItemClickListener;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.ExhibitSet;
import de.upb.hip.mobile.models.SetMarker;

public class MainActivity extends BaseActivity {

    private static final String BASE_URL = "http://tboegeholz.de/ba/index.php";

    public LatLng paderborn = new LatLng(51.7276064, 8.7684325);

    private DBAdapter database;
    private ExhibitSet exhibitSet;
    private List<String> activeFilter = new ArrayList<>();

    // Recycler View: MainList
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Recycler View: Filter
    private RecyclerView mFilterRecyclerView;
    private RecyclerView.Adapter mFilterAdapter;
    private RecyclerView.LayoutManager mFilterLayoutManager;

    private DrawerLayout mDrawerLayout;

    private GPSTrackerListner mGPSTracker;
    private GeoPoint mGeoLocation;
    private MapView mMap = null;
    private SetMarker mMarker;
    private FolderOverlay mItineraryMarkers;
    private ViaPointInfoWindow mViaPointInfoWindow;
    private ArrayList<OverlayItem> overlayItemArray;

    // Refresh
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGPSTracker = new GPSTrackerListner(MainActivity.this);

        // getting location
        if (mGPSTracker.canGetLocation()) {
            mGeoLocation = new GeoPoint(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());
        }

        openDatabase();
        this.exhibitSet = new ExhibitSet(database.getView("exhibits"), this.paderborn);

        setupMap();
        exhibitSet.addMarker(mMarker, getApplicationContext());

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

        /*
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycler_view);
        mFilterRecyclerView.setHasFixedSize(true);
        mFilterLayoutManager = new LinearLayoutManager(this);
        mFilterRecyclerView.setLayoutManager(mFilterLayoutManager);
        List<String> categories = this.exhibitSet.getCategories();
        for(String item: categories) this.activeFilter.add(item);
        mFilterAdapter = new FilterRecyclerAdapter(categories, this.activeFilter);
        mFilterRecyclerView.setAdapter(mFilterAdapter);
        mFilterRecyclerView.addOnItemTouchListener(new FilterRecyclerClickListener(this));
        */

        // detecting that the current view is compleatly created and then
        // zoom to boundingbox on map
        // it should be done only if the view completely created
        final View view = this.findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setUpNavigationDrawer(this, mDrawerLayout);

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
        mSwipeLayout.setEnabled(false);
    }

    /**
     * init the map
     * set the tile source
     * set zoom
     */
    private void setupMap() {
        // getting the map
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.map_main);
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
        mViaPointInfoWindow = new ViaPointInfoWindow(R.layout.navigation_itinerary_bubble, mMap, this);

        //-- Create location Overlay
        overlayItemArray = new ArrayList<>();
        DefaultResourceProxyImpl defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);

        // to use blue point (or other) as location marker set it in CustomizedIconOverlay
        //Bitmap locationMarker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location);
        CustomisedIconOverlay customisedIconOverlay = new CustomisedIconOverlay(this, null,
                overlayItemArray, null, defaultResourceProxyImpl);
        mMap.getOverlays().add(customisedIconOverlay);
        //--

        // add overlay for the markers
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);

        mMarker = new SetMarker(mMap, mItineraryMarkers, mViaPointInfoWindow);

        // add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(myScaleBarOverlay);
    }

    /**
     * getting boundingbox to fit all marker on the map
     *
     * @return BoundingBox
     */
    public BoundingBoxE6 getBoundingBoxE6() {
        ArrayList<GeoPoint> points = new ArrayList<>();

        if (mGeoLocation != null) {
            points.add(mGeoLocation);
        }

        for (int i = 0; i < this.exhibitSet.getSize(); i++) {
            Exhibit exhibit = this.exhibitSet.getExhibit(i);
            GeoPoint geo = new GeoPoint(exhibit.latlng.latitude, exhibit.latlng.longitude);

            points.add(geo);
        }

        return BoundingBoxE6.fromGeoPoints(points);
    }

    public void notifyExhibitSetChanged() {
        mSwipeLayout.setRefreshing(false);
        this.exhibitSet = new ExhibitSet(database.getView("exhibits"), this.paderborn);
        mAdapter = new RecyclerAdapter(this.exhibitSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        this.mAdapter.notifyDataSetChanged();
        this.exhibitSet.addMarker(mMarker, getApplicationContext());
    }

    public void updateCategories(String categorie) {
        if (categorie != null) {
            if (this.activeFilter.contains(categorie)) this.activeFilter.remove(categorie);
            else this.activeFilter.add(categorie);
            this.mFilterAdapter.notifyDataSetChanged();
            this.exhibitSet.updateCategories(this.activeFilter);
            this.mAdapter.notifyDataSetChanged();
            this.exhibitSet.addMarker(mMarker, getApplicationContext());
        }
    }

    protected boolean startLocationUpdates() {
        boolean result = false;
        for (final String provider : mGPSTracker.getLocationManager().getProviders(true)) {
            mGPSTracker.getLocationManager().requestLocationUpdates(provider,
                    GPSTrackerListner.MIN_TIME_BW_UPDATES,
                    GPSTrackerListner.MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    mGPSTracker);
            result = true;
        }
        return result;
    }

    public void updatePosition(Location position) {
        this.exhibitSet.updatePosition(new LatLng(position.getLatitude(), position.getLongitude()));
        this.mAdapter.notifyDataSetChanged();

        setOverlayLoc(position);
    }

    private void setOverlayLoc(Location overlayloc) {
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);

        overlayItemArray.clear();

        OverlayItem newMyLocationItem = new OverlayItem("", "", overlocGeoPoint);
        overlayItemArray.add(newMyLocationItem);

        mMap.invalidate();
    }

    @Override
    protected void onResume() {
        startLocationUpdates();

        super.onResume();
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
        stopLocationUpdates();

        super.onPause();
    }

    protected void stopLocationUpdates() {
        mGPSTracker.stopUsingGPS();
    }
}
