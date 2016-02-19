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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.GPSTracker;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;

public class RouteDetailsActivity extends BaseActivity {

    public static final int MAX_ZOOM_LEVEL = 16;
    public static final int ZOOM_LEVEL = 16;
    private FolderOverlay mItineraryMarkers;
    private GeoPoint mGeoLocation;
    private MapView mMap = null;
    private Route mRoute;
    private DrawerLayout mDrawerLayout;

    private GPSTracker mGPSTracker;
    private boolean mCanGetLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        mRoute = (Route) getIntent().getSerializableExtra("route");

        if (mRoute != null) {
            mGPSTracker = new GPSTracker(RouteDetailsActivity.this);

            // getting location
            if (mGPSTracker.canGetLocation()) {
                mGeoLocation = new GeoPoint(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());
            }

            initRouteInfo();
            initMap();
            initItineraryMarkers();

            addStartPointOnMap();
            addViaPoints();
            addFinalPointOnMap();

            drawPathOnMap();
        } else {
            Toast.makeText(mMap.getContext(), R.string.empty_route, Toast.LENGTH_SHORT).show();
        }

        Button button = (Button) this.findViewById(R.id.activityRouteDetailsRouteStartButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGPSTracker = new GPSTracker(RouteDetailsActivity.this);
                if (mGPSTracker.canGetLocation()) {
                    startRouteNavigation();
                } else {
                    mGPSTracker.showSettingsAlert();
                    mCanGetLocation = false;
                }
            }
        });

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
                            mMap.zoomToBoundingBox(boundingBoxE6, true);
                        }
                    }
                });

        //setUp navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setUpNavigationDrawer(this, mDrawerLayout);
    }

    /**
     * init the map
     * set the tile source
     * set zoom
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
        if (mGeoLocation != null) {
            // set center to current location
            mapController.setCenter(mGeoLocation);
        } else {
            // set center to first waypoint
            GeoPoint geoFirstWaypoint = new GeoPoint(mRoute.waypoints.get(0).latitude,
                    mRoute.waypoints.get(0).longitude);
            mapController.setCenter(geoFirstWaypoint);
        }
    }

    /**
     * init an overlay which is just a group of other overlays
     */
    private void initItineraryMarkers() {
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);
    }

    /**
     * add start point (our location) in FolderOverlay and refresh the map
     */
    private void addStartPointOnMap() {
        GeoPoint geoLocation = null;

        if (this.mGeoLocation != null) {
            // setup our current location as start point
            geoLocation = this.mGeoLocation;
        } else if (mRoute.waypoints.size() > 0) {
            // if no current location then use first waypoint as start point
            geoLocation = new GeoPoint(mRoute.waypoints.get(0).latitude,
                    mRoute.waypoints.get(0).longitude);
        }

        if (geoLocation != null) {
            // init start point
            Drawable markerIcon = ContextCompat.getDrawable(this, R.drawable.marker_departure);
            Marker marker = new Marker(mMap);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(getString(R.string.departure));
            marker.setPosition(geoLocation);
            marker.setIcon(markerIcon);
            //marker.setRelatedObject(index);
            mItineraryMarkers.add(marker);
            mMap.invalidate();
        }
    }

    /**
     * add final point or one of the waypoint in FolderOverlay and refresh the map
     */
    private void addFinalPointOnMap() {
        GeoPoint geoLastWaypoint = null;

        if (this.mGeoLocation != null) {
            if (mRoute.waypoints.size() == 1) {
                // if current location is not null and we have at least one waypoint, then
                // use this waypoint as destination point
                geoLastWaypoint = new GeoPoint(mRoute.waypoints.get(0).latitude,
                        mRoute.waypoints.get(0).longitude);
            } else if (mRoute.waypoints.size() > 1) {
                // if current location is not null and we have more than two waypoints, then
                // use last waypoint as destination point
                int size = mRoute.waypoints.size();

                geoLastWaypoint = new GeoPoint(mRoute.waypoints.get(size - 1).latitude,
                        mRoute.waypoints.get(size - 1).longitude);
            }
        } else if (mRoute.waypoints.size() > 0) {
            // if current location is null and we have at least one waypoint, then
            // use last waypoint as destination point
            int size = mRoute.waypoints.size();

            geoLastWaypoint = new GeoPoint(mRoute.waypoints.get(size - 1).latitude,
                    mRoute.waypoints.get(size - 1).longitude);
        }

        if (geoLastWaypoint != null) {
            // set final point
            Drawable markerIcon = ContextCompat.getDrawable(this, R.drawable.marker_destination);
            Marker marker = new Marker(mMap);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(getString(R.string.destination));
            marker.setPosition(geoLastWaypoint);
            marker.setIcon(markerIcon);
            //marker.setRelatedObject(index);
            mItineraryMarkers.add(marker);
            mMap.invalidate();
        }
    }

    /**
     * init details information for the route
     */
    private void initRouteInfo() {
        TextView description = (TextView) findViewById(R.id.activityRouteDetailsRouteDescription);
        TextView title = (TextView) findViewById(R.id.activityRouteDetailsRouteTitle);
        TextView duration = (TextView) findViewById(R.id.activityRouteDetailsRouteDuration);
        LinearLayout tagsLayout = (LinearLayout) findViewById(R.id.activityRouteDetailsRouteTagsLayout);
        ImageView imageView = (ImageView) findViewById(R.id.activityRouteDetailsRouteImageView);

        description.setText(mRoute.description);
        title.setText(mRoute.title);
        int durationInMinutes = mRoute.duration / 60;
        duration.setText(getResources().getQuantityString(R.plurals.route_activity_duration_minutes, durationInMinutes, durationInMinutes));

        //Add tags
        if (mRoute.tags != null) {
            tagsLayout.removeAllViews();
            for (RouteTag tag : mRoute.tags) {
                ImageView tagImageView = new ImageView(getApplicationContext());
                tagImageView.setImageDrawable(tag.getImage(mRoute.id, getApplicationContext()));
                tagsLayout.addView(tagImageView);
            }
        }

        //Add image
        Attachment att = DBAdapter.getAttachment(mRoute.id, mRoute.imageName);
        try {
            Bitmap b = BitmapFactory.decodeStream(att.getContent());
            Drawable image = new BitmapDrawable(getResources(), b);
            imageView.setImageDrawable(image);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }
    }

    private void addViaPoints() {

        DBAdapter db = new DBAdapter(this);
        if (mRoute.waypoints != null && mRoute.waypoints.size() > 0) {
            //Add all the waypoints to the map
            for (Waypoint waypoint : mRoute.waypoints) {
                GeoPoint geoPoint = new GeoPoint(waypoint.latitude, waypoint.longitude);

                Drawable markerIcon = ContextCompat.getDrawable(this, R.drawable.marker_via);
                Marker marker = new Marker(mMap);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                if (waypoint.exhibit_id != -1) {
                    Exhibit exhibit = waypoint.getExhibit(db);
                    marker.setTitle(exhibit.name + "\n" + exhibit.description);
                }

                marker.setPosition(geoPoint);
                marker.setIcon(markerIcon);
                //marker.setRelatedObject(index);
                mItineraryMarkers.add(marker);
                mMap.invalidate();
            }

/*            mItineraryMarkers.
                    map.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (markerMap.containsKey(marker.getId()) && markerMap.get(marker.getId()) != -1) {
                        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                        intent.putExtra("exhibit-id", markerMap.get(marker.getId()));
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });*/

        }
    }

    /**
     * paint simple road lines with blue color
     * PathOverlay is depricated, but for drawing simple path is perfect
     * not depricated class Polylines is more complex and needed Road from RoadManager
     */
    @SuppressWarnings("deprecation")
    private void drawPathOnMap() {
        PathOverlay myPath = new PathOverlay(getResources().getColor(R.color.colorPrimaryDark),
                10, new DefaultResourceProxyImpl(this));

        if (mGeoLocation != null) {
            myPath.addPoint(mGeoLocation);
        }

        if (mRoute != null && mRoute.waypoints != null) {
            for (Waypoint waypoint : mRoute.waypoints) {
                myPath.addPoint(new GeoPoint(waypoint.latitude, waypoint.longitude));
            }
        }

        mMap.getOverlays().add(myPath);
        mMap.invalidate();
    }

    public BoundingBoxE6 getBoundingBoxE6() {
        ArrayList<GeoPoint> points = new ArrayList<>();

        if (mGeoLocation != null) {
            points.add(mGeoLocation);
        }

        if (mRoute != null && mRoute.waypoints != null) {
            for (Waypoint waypoint : mRoute.waypoints) {
                points.add(new GeoPoint(waypoint.latitude, waypoint.longitude));
            }
        }

        return BoundingBoxE6.fromGeoPoints(points);
    }

    private void startRouteNavigation() {
        if (mRoute != null) {
            Intent intent = new Intent(getApplicationContext(), RouteNavigationActivity.class);
            intent.putExtra("route", mRoute);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(mMap.getContext(), R.string.empty_route, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGPSTracker = new GPSTracker(RouteDetailsActivity.this);
        if (!mCanGetLocation && mGPSTracker.canGetLocation()) {
            startRouteNavigation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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
}