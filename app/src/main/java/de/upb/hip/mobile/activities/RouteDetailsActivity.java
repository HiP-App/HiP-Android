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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.helpers.ViaPointInfoWindow;
import de.upb.hip.mobile.listeners.ExtendedLocationListener;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.SetMarker;
import de.upb.hip.mobile.models.Waypoint;

/**
 * Activity Class for the route details view, where the details of a route are show, including a
 * map and the possibility to start the navigation.
 */
public class RouteDetailsActivity extends ActionBarActivity {

    public static final int MAX_ZOOM_LEVEL = 16;
    public static final int ZOOM_LEVEL = 16;

    private GeoPoint mCurrentUserLocation;
    private MapView mMap = null;
    private SetMarker mMarker;
    private Route mRoute;

    private ExtendedLocationListener mGpsTracker;
    private boolean mCanGetLocation = true;

    private DBAdapter mDatabase;

    private ActionBar mActionBar;

    /**
     * Called when the activity is created, shows the details of the route
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        mRoute = (Route) getIntent().getSerializableExtra("route");
        mGpsTracker = new ExtendedLocationListener(RouteDetailsActivity.this);

        if (mRoute != null) {
            // getting location
            if (mGpsTracker.canGetLocation()) {
                mCurrentUserLocation = new GeoPoint(
                        mGpsTracker.getLatitude(), mGpsTracker.getLongitude());
            }

            // TODO Remove this as soon as no needs to run on emulator
            // set default coordinats for emulator
            if (Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK")) {
                mCurrentUserLocation = new GeoPoint(ExtendedLocationListener.PADERBORN_HBF.latitude,
                        ExtendedLocationListener.PADERBORN_HBF.longitude);
            }

            mDatabase = new DBAdapter(this);

            initRouteInfo();
            initMap();
            initItineraryMarkers();

            addStartPointOnMap();
            addViaPointsOnMap();
            addFinalPointOnMap();

            drawPathOnMap();
        } else {
            Toast.makeText(mMap.getContext(), R.string.empty_route, Toast.LENGTH_SHORT).show();
        }

        Button button = (Button) this.findViewById(R.id.activityRouteDetailsRouteStartButton);
        button.setOnClickListener(new View.OnClickListener() {

            /**
             * When clicking on button, check if GPS and internet is available, if both available
             * the routing is started.
             * Shows dialog for GPS settings if not available.
             * Shows no internet connection if internet not available.
             *
             * @param v View
             */
            @Override
            public void onClick(View v) {
                mGpsTracker.getLocation();
                boolean isNetworkAvailable = isNetworkAvailable();

                if (mGpsTracker.canGetLocation() && isNetworkAvailable) {
                    startRouteNavigation();
                }

                if (!isNetworkAvailable) {
                    Toast.makeText(mMap.getContext(),
                            R.string.network_message, Toast.LENGTH_LONG).show();
                    mCanGetLocation = false;
                }

                if (!mGpsTracker.canGetLocation()) {
                    mGpsTracker.showSettingsAlert();
                    mCanGetLocation = false;
                }
            }
        });

        // Detecting that the current view is completely created and then
        // zoom to bounding box on map
        // it should be done only if the view is completely created
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

        // Set back button on actionbar
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(mRoute.getTitle());
        }
    }

    /**
     * Implement the onSupportNavigateUp() method of the interface, closes the activity
     *
     * @return always true
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Init the map, set the tile source and set the zoom.
     */
    private void initMap() {
        // getting the map
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.map_route_details);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        mMap = genericMap.getMapView();
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setTilesScaledToDpi(true);
        mMap.setMaxZoomLevel(MAX_ZOOM_LEVEL);

        // mMap prefs:
        IMapController mapController = mMap.getController();
        mapController.setZoom(ZOOM_LEVEL);
        if (mCurrentUserLocation != null) {
            // set center to current location
            mapController.setCenter(mCurrentUserLocation);
        } else {
            // set center to first waypoint
            GeoPoint geoFirstWaypoint = new GeoPoint(mRoute.getWayPoints().get(0).getLatitude(),
                    mRoute.getWayPoints().get(0).getLongitude());
            mapController.setCenter(geoFirstWaypoint);
        }
    }

    /**
     * Init an overlay which is just a group of other overlays.
     */
    private void initItineraryMarkers() {

        ViaPointInfoWindow mViaPointInfoWindow = new ViaPointInfoWindow(
                R.layout.navigation_itinerary_bubble, mMap, this);

        FolderOverlay mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);

        mMarker = new SetMarker(mMap, mItineraryMarkers, mViaPointInfoWindow);
    }

    /**
     * Adds that start point to the map.
     * If the current user location is known use it as start point.
     * Else use the first way point as start point, but only if there are two or more way points
     * (else there would be no route).
     */
    private void addStartPointOnMap() {
        GeoPoint geoLocation = null;
        String title = getResources().getString(R.string.departure);
        String description = "";
        int exhibitId = -1;
        Drawable drawable = null;

        if (this.mCurrentUserLocation != null) {
            // setup our current location as start point
            geoLocation = this.mCurrentUserLocation;
        } else if (mRoute.getWayPoints().size() > 1) {
            // if no current location then use first waypoint as start point only if >=2 waypoints
            geoLocation = new GeoPoint(mRoute.getWayPoints().get(0).getLatitude(),
                    mRoute.getWayPoints().get(0).getLongitude());

            // add related data to marker if start point is first waypoint
            if (mRoute.getWayPoints().get(0).getExhibitId() != -1) {
                Exhibit exhibit = mRoute.getWayPoints().get(0).getExhibit(mDatabase);
                title = exhibit.name;
                description = exhibit.description;
                exhibitId = exhibit.id;

                drawable = DBAdapter.getImage(exhibit.id, "image.jpg", 65);
            }
        }

        if (geoLocation != null) {
            if (drawable == null) {
                // set default image in info window
                drawable = ContextCompat.getDrawable(this, R.drawable.marker_departure);
            }

            // set and fill start point with data
            addMarker(geoLocation, drawable, R.drawable.marker_departure, title, description,
                    exhibitId);
        }
    }

    /**
     * Adds the way points between start or end point to the map (excluding start and end point).
     * <p/>
     * If the current user location is known start with the first way point.
     * Else start with the second way point, but only if there are two or more way points
     * (else there would be no route).
     * Adds a single marker if the current user location is unknown and only one way point exits.
     */
    private void addViaPointsOnMap() {
        int waypointIndex = -1;

        if (this.mCurrentUserLocation != null && mRoute.getWayPoints().size() > 1) {
            waypointIndex = 0;
        } else if (this.mCurrentUserLocation == null) {
            if (mRoute.getWayPoints().size() > 2) {
                waypointIndex = 1;
            } else if (mRoute.getWayPoints().size() == 1) {
                waypointIndex = 0;
            }
        }

        if (waypointIndex > -1) {
            //Add all waypoints to the map except the last one,
            // it would be marked as destination marker
            for (int index = waypointIndex; index < mRoute.getWayPoints().size() - 1; index++) {
                Waypoint waypoint = mRoute.getWayPoints().get(index);
                if (waypoint.getExhibitId() != -1) {
                    GeoPoint geoPoint =
                            new GeoPoint(waypoint.getLatitude(), waypoint.getLongitude());
                    Exhibit exhibit = waypoint.getExhibit(mDatabase);

                    Drawable drawable = DBAdapter.getImage(exhibit.id, "image.jpg", 65);

                    // add marker on map for waypoint
                    addMarker(geoPoint, drawable, R.drawable.marker_via, exhibit.name,
                            exhibit.description, exhibit.id);
                }
            }
        }
    }

    /**
     * Adds the end point to the map.
     * Takes the the last way point, if the current user location is known and way points exits,
     * or if the current user location is unknown, but two or more way points exit.
     */
    private void addFinalPointOnMap() {
        GeoPoint geoLocation;
        String title = getResources().getString(R.string.destination);
        String description = "";
        int exhibitId = -1;
        Drawable drawable;

        int waypointIndex = -1;

        if ((this.mCurrentUserLocation != null) && (mRoute.getWayPoints().size() > 0)) {
            // if current location is not null and we have at least one waypoint, then
            // use last one as destination point
            waypointIndex = mRoute.getWayPoints().size() - 1;
        } else {
            if (mRoute.getWayPoints().size() > 1) {
                // if current location is null and we have more then one waypoint, then
                // use last waypoint as destination point
                waypointIndex = mRoute.getWayPoints().size() - 1;
            }
        }

        if (waypointIndex > -1) {
            geoLocation = new GeoPoint(mRoute.getWayPoints().get(waypointIndex).getLatitude(),
                    mRoute.getWayPoints().get(waypointIndex).getLongitude());

            // add related data to marker
            if (mRoute.getWayPoints().get(waypointIndex).getExhibitId() != -1) {
                Exhibit exhibit = mRoute.getWayPoints().get(waypointIndex).getExhibit(mDatabase);
                title = exhibit.name;
                description = exhibit.description;
                exhibitId = exhibit.id;

                drawable = DBAdapter.getImage(exhibit.id, "image.jpg", 65);
            } else {
                drawable = ContextCompat.getDrawable(this, R.drawable.marker_destination);
            }

            // set final point
            addMarker(geoLocation, drawable, R.drawable.marker_destination, title, description,
                    exhibitId);
        }
    }

    /**
     * Get and set details information for the route above the map.
     */
    private void initRouteInfo() {
        TextView descriptionView = (TextView) findViewById(
                R.id.activityRouteDetailsRouteDescription);
        TextView durationView = (TextView) findViewById(R.id.activityRouteDetailsRouteDuration);
        LinearLayout tagsLayout = (LinearLayout) findViewById(
                R.id.activityRouteDetailsRouteTagsLayout);

        descriptionView.setText(mRoute.getDescription());
        int durationInMinutes = mRoute.getDuration() / 60;
        durationView.setText(getResources().getQuantityString(
                R.plurals.route_activity_duration_minutes, durationInMinutes, durationInMinutes));

        //Add tags
        if (mRoute.getTags() != null) {
            tagsLayout.removeAllViews();
            for (RouteTag tag : mRoute.getTags()) {
                ImageView tagImageView = new ImageView(getApplicationContext());
                tagImageView.setImageDrawable(
                        tag.getImage(mRoute.getId(), getApplicationContext()));
                tagsLayout.addView(tagImageView);
            }
        }

        //Add image
        Attachment attachment = DBAdapter.getAttachment(mRoute.getId(), mRoute.getImageName());
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(attachment.getContent());
            Drawable image = new BitmapDrawable(getResources(), bitmap);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }
    }

    /**
     * Adds the marker with the data of t and put on the map.
     *
     * @param geoLocation GeoPoint of the created  marker
     * @param drawable    Drawable, image of the exhibit
     * @param markerImage int, id from drawable
     * @param title       String, title of the exhibit
     * @param description String, description of the exhibit
     * @param exhibitId   int, exhibit id
     */
    private void addMarker(GeoPoint geoLocation, Drawable drawable, int markerImage, String title,
                           String description, int exhibitId) {

        Drawable icon = ContextCompat.getDrawable(this, markerImage);

        Map<String, Integer> data = new HashMap<>();
        data.put(title, exhibitId);

        mMarker.addMarker(null, title, description, geoLocation, drawable, icon, data);
    }

    /**
     * Paint simple road lines with blue color. PathOverlay is deprecated, but for drawing simple
     * path is perfect.
     * The new, not deprecated class Polylines is more complex and needs a road from RoadManager
     */
    @SuppressWarnings("deprecation")
    private void drawPathOnMap() {
        PathOverlay myPath = new PathOverlay(getResources().getColor(R.color.colorPrimaryDark),
                10, new DefaultResourceProxyImpl(this));

        if (mCurrentUserLocation != null) {
            myPath.addPoint(mCurrentUserLocation);
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
     * Getting bounding box to fit all marker on the map
     *
     * @return BoundingBoxE6
     */
    public BoundingBoxE6 getBoundingBoxE6() {
        ArrayList<GeoPoint> points = new ArrayList<>();

        if (mCurrentUserLocation != null) {
            points.add(mCurrentUserLocation);
        }

        if (mRoute != null && mRoute.getWayPoints() != null) {
            for (Waypoint waypoint : mRoute.getWayPoints()) {
                points.add(new GeoPoint(waypoint.getLatitude(), waypoint.getLongitude()));
            }
        }

        return BoundingBoxE6.fromGeoPoints(points);
    }

    /**
     * Method bound to the button, starts the navigation.
     */
    private void startRouteNavigation() {
        if (mRoute != null) {
            Intent intent = new Intent(getApplicationContext(), RouteNavigationActivity.class);
            intent.putExtra("route", mRoute);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(mMap.getContext(), R.string.empty_route, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * When the activity is resumed, check for GPS and internet connection again.
     */
    @Override
    protected void onResume() {
        super.onResume();

        mGpsTracker.getLocation();
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!mCanGetLocation && mGpsTracker.canGetLocation() && isNetworkAvailable) {
            startRouteNavigation();
        }
    }

    /**
     * When activity is paused, stop using GPS.
     */
    @Override
    protected void onPause() {
        mGpsTracker.stopUsingGPS();
        super.onPause();
    }

    /**
     * Sets parameters to prevent the automatic start of the navigation activity, when this activity
     * is reached via back button.
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mCanGetLocation = data.getBooleanExtra("onBackPressed", false);
            }
        }
    }

    /**
     * Check for internet connection.
     *
     * @return true, if internet is available.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}