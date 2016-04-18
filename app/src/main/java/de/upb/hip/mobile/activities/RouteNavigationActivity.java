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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.helpers.ViaPointInfoWindow;
import de.upb.hip.mobile.listeners.ExtendedLocationListener;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.SetMarker;
import de.upb.hip.mobile.models.ViaPointData;

/**
 * Create the route on the map and show step by step instruction for the navigation
 */

public class RouteNavigationActivity extends Activity implements MapEventsReceiver,
        LocationListener, SensorEventListener {


    public static final String INTENT_ROUTE = "route";


    //Constants for saving the instance state
    private static final String SAVEDSTATE_LOCATION = "location";
    private static final String SAVEDSTATE_TRACKING_MODE = "tracking_mode";
    private static final String SAVEDSTATE_START = "start";
    private static final String SAVEDSTATE_DESTINATION = "destination";
    private static final String SAVEDSTATE_VIAPOINTS = "viapoints";
    private static final String SAVEDSTATE_REACHED_NODE = "mReachedNode";
    private static final String SAVEDSTATE_NEXT_NODE = "mNextNode";
    private static final String SAVEDSTATE_NEXT_VIA_POINT = "mNextViaPoint";


    protected final int ROUTE_REJECT = 25; //in meters
    protected final String PROX_ALERT = "de.upb.hip.mobile.activities.PROX_ALERT";
    protected final long POINT_RADIUS = 5; // in Meters
    protected final long PROX_ALERT_EXPIRATION = -1; //indicate no expiration
    protected Road[] mRoads;

    protected MapView mMap;
    protected SetMarker mMarker;
    protected GeoPoint mStartPoint;
    protected ArrayList<ViaPointData> mViaPoints;
    protected FolderOverlay mItineraryMarkers;
    protected ViaPointInfoWindow mViaPointInfoWindow;
    protected DirectedLocationOverlay mLocationOverlay;
    protected Polygon mDestinationPolygon; //enclosing polygon of destination location
    protected Polyline[] mRoadOverlays;
    protected FolderOverlay mRoadNodeMarkers;
    protected Button mTrackingModeButton;
    protected boolean mTrackingMode;
    protected float mAzimuthAngleSpeed = 0.0f;
    protected int mSelectedRoad;
    protected int mReachedNode = -1;
    protected int mNextNode = 0;
    protected int mNextViaPoint = 0;
    // setup the UI before the reaching the start point only once
    protected boolean mUpdateStartPointOnce = true;
    // for location, location manager and setting dialog if no location
    protected ExtendedLocationListener mGpsTracker;
    // for the recalculation the route
    protected int mDistanceBetweenLoc = -1;
    // shows creation and loading route and map steps
    protected ProgressDialog mProgressDialog;
    // for the detecting that location is reached
    protected ProximityIntentReceiver mProximityIntentReceiver = new ProximityIntentReceiver();
    protected PendingIntent mProximityIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_navigation);

        // init progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);

        // getting the map
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.routeNavigationMap);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        mMap = genericMap.getMapView();
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setTilesScaledToDpi(true);
        mMap.setMaxZoomLevel(RouteDetailsActivity.MAX_ZOOM_LEVEL);

        // getting location
        mGpsTracker = new ExtendedLocationListener(RouteNavigationActivity.this);
        GeoPoint geoLocation = new GeoPoint(mGpsTracker.getLatitude(), mGpsTracker.getLongitude());

        // TODO Remove this as soon as no needs to run in emulator
        // set default coordinats for emulator
        if (Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK")) {
            geoLocation = new GeoPoint(ExtendedLocationListener.PADERBORN_HBF.latitude,
                    ExtendedLocationListener.PADERBORN_HBF.longitude);
        }

        // mMap prefs:
        IMapController mapController = mMap.getController();
        mapController.setZoom(RouteDetailsActivity.ZOOM_LEVEL);
        mapController.setCenter(geoLocation);

        // Itinerary markers:
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);
        mViaPointInfoWindow = new ViaPointInfoWindow(R.layout.navigation_info_window, mMap, this);
        mMarker = new SetMarker(mMap, mItineraryMarkers, mViaPointInfoWindow);
        mLocationOverlay = new DirectedLocationOverlay(this);
        mMap.getOverlays().add(mLocationOverlay);

        //getting route from intent
        Route route = (Route) getIntent().getSerializableExtra(INTENT_ROUTE);
        DBAdapter db = new DBAdapter(this);
        // init start
        mStartPoint = geoLocation;

        // add viapoints
        mViaPoints = new ArrayList<>();
        for (int i = 0; i < (route.getWayPoints().size()); i++) {

            GeoPoint geoPoint = new GeoPoint(route.getWayPoints().get(i).getLatitude(),
                    route.getWayPoints().get(i).getLongitude());

            ViaPointData viaPointsData = new ViaPointData();
            // add related data to marker if start point is first waypoint
            if (route.getWayPoints().get(i).getExhibitId() != -1) {
                Exhibit exhibit = route.getWayPoints().get(i).getExhibit(db);

                viaPointsData.setViaPointData
                        (geoPoint, exhibit.getName(), exhibit.getDescription(), exhibit.getId());
            } else {
                if (i == route.getWayPoints().size() - 1) {
                    viaPointsData.setViaPointData(geoPoint,
                            getResources().getString(R.string.destination), "", -1);
                } else {
                    viaPointsData.setViaPointData(geoPoint,
                            getResources().getString(R.string.via_point), "", -1);
                }
            }
            mViaPoints.add(viaPointsData);
        }

        if (savedInstanceState == null) {
            getRoadAsync(0);
        } else {
            mLocationOverlay.setLocation((GeoPoint) savedInstanceState
                    .getParcelable(SAVEDSTATE_LOCATION));
            mStartPoint = savedInstanceState.getParcelable(SAVEDSTATE_START);
            //mDestinationPoint = savedInstanceState.getParcelable(SAVEDSTATE_DESTINATION);
            //mViaPoints = savedInstanceState.getParcelableArrayList(SAVEDSTATE_VIAPOINTS);
            mReachedNode = savedInstanceState.getInt(SAVEDSTATE_REACHED_NODE);
            mNextNode = savedInstanceState.getInt(SAVEDSTATE_NEXT_NODE);
            mNextViaPoint = savedInstanceState.getInt(SAVEDSTATE_NEXT_VIA_POINT);
        }

        // calculate distance between current location and start point
        // if the start location was not reached
        mDistanceBetweenLoc = geoLocation.distanceTo(mStartPoint);

        updateUIWithItineraryMarkers();

        //Add the POIs around the starting point of the map
        if (route.getWayPoints().size() > 0) {
            addPOIsToMap(mMap, new GeoPoint(route.getWayPoints().get(0).getLatitude(),
                    route.getWayPoints().get(0).getLongitude()));
        }

        // a scale bar in the top-left corner of the screen
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        //Tracking system:
        mTrackingModeButton = (Button) findViewById(R.id.routeNavigationTrackingModeButton);
        mTrackingModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTrackingMode = !mTrackingMode;
                updateUIWithTrackingMode();
            }
        });

        if (savedInstanceState != null) {
            mTrackingMode = savedInstanceState.getBoolean(SAVEDSTATE_TRACKING_MODE);
            updateUIWithTrackingMode();
        } else
            mTrackingMode = false;

        mRoadNodeMarkers = new FolderOverlay(this);
        //mRoadNodeMarkers.setName("Route Steps");
        mMap.getOverlays().add(mRoadNodeMarkers);

        if (savedInstanceState != null) {
            updateUiWithRoads(mRoads);
        }
    }

    private void addPOIsToMap(final MapView map, final GeoPoint position) {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                NominatimPOIProvider poiProvider = new NominatimPOIProvider("Uni-Paderborn HiP App");
                final ArrayList<POI> pois = poiProvider.getPOICloseTo(position, "restaurant", 50, 0.1);

                final FolderOverlay poiMarkers = new FolderOverlay(RouteNavigationActivity.this);
                RouteNavigationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        map.getOverlays().add(poiMarkers);

                        Drawable poiIcon = getResources().getDrawable(R.drawable.route_tag_restaurant);
                        for (POI poi : pois) {
                            Marker poiMarker = new Marker(map);
                            poiMarker.setTitle(poi.mType);
                            poiMarker.setSnippet(poi.mDescription);
                            poiMarker.setPosition(poi.mLocation);
                            poiMarker.setIcon(poiIcon);
                            /*if (poi.mThumbnail != null){
                                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
                            }*/
                            poiMarkers.add(poiMarker);
                        }
                    }
                });


                return null;
            }
        }.execute();

    }


    /**
     * LocationListener implementation
     */
    @Override
    public void onLocationChanged(final Location location) {

        GeoPoint newLocation = new GeoPoint(location);

        if (!mLocationOverlay.isEnabled()) {
            //we get the location for the first time:
            mLocationOverlay.setEnabled(true);
            mMap.getController().animateTo(newLocation);
        }

        if (mReachedNode == mNextNode) {
            mNextNode += 1;
            setNextStepToAlert(newLocation);
        } else if (mReachedNode == -1 && mUpdateStartPointOnce) {
            // start node is not reached, update only once
            setNextStepToAlert(newLocation);
        }

        recalculateRoute(newLocation);

        GeoPoint prevLocation = mLocationOverlay.getLocation();
        mLocationOverlay.setLocation(newLocation);
        mLocationOverlay.setAccuracy((int) location.getAccuracy());

        GeoPoint nextNearestLocation = getNextNodeLocation();
        if (nextNearestLocation != null && prevLocation != null) {
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                // setup next node(location) to the north
                mAzimuthAngleSpeed = (float) prevLocation.bearingTo(nextNearestLocation);
                mLocationOverlay.setBearing(mAzimuthAngleSpeed);
            }
        }

        if (mTrackingMode) {
            //keep the mMap view centered on current location:
            mMap.getController().animateTo(newLocation);
            mMap.setMapOrientation(-mAzimuthAngleSpeed);
        } else {
            //just redraw the location overlay:
            mMap.invalidate();
        }
    }

    /**
     * Returns the geopoint of next node
     */
    private GeoPoint getNextNodeLocation() {
        if (mRoads == null) {
            return null;
        }

        GeoPoint nextNodeLocation;

        if (mNextNode < mRoads[0].mNodes.size()) {
            // find next point
            nextNodeLocation = mRoads[0].mNodes.get(mNextNode).mLocation;
        } else {
            nextNodeLocation = mViaPoints.get(mViaPoints.size() - 1).getGeoPoint();
        }

        return nextNodeLocation;
    }

    /**
     * Recalculate the route if
     * distance between current location and next node >= 'ROUTE_REJECT' (20m)
     * distance between reached node and and next node
     */
    private void recalculateRoute(GeoPoint currentLoc) {
        GeoPoint nextLoc = getNextNodeLocation();
        if (nextLoc == null) {
            return;
        }

        // distance from current loc to next node
        int distFromCurrent = currentLoc.distanceTo(nextLoc);

        // update distance info on imageview
        updateDistanceInfo(String.valueOf(distFromCurrent) + " m");

        // check if difference >= 'ROUTE_REJECT' (20m)
        if ((distFromCurrent - mDistanceBetweenLoc) >= ROUTE_REJECT) {

            // set to default
            mReachedNode = -1;
            mNextNode = 0;
            mNextViaPoint = 0;
            mStartPoint = currentLoc;

            // get route
            getRoadAsync(mNextViaPoint);
            // update markers on the map
            updateUIWithItineraryMarkers();

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * Setup next node for the proximity alert
     * set instruction maneuver and distance in imageview
     */
    private void setNextStepToAlert(GeoPoint geo) {
        if (mRoads == null || mReachedNode >= mRoads[0].mNodes.size()) {
            return;
        }

        // setup the nearest point from current point to ProximityAlert
        if (mReachedNode == -1) {
            GeoPoint nextLocation = new GeoPoint(mRoads[0].mNodes.get(0).mLocation.getLatitude(),
                    mRoads[0].mNodes.get(0).mLocation.getLongitude());
            addProximityAlert(nextLocation.getLatitude(), nextLocation.getLongitude());
            int distToStartLoc = geo.distanceTo(nextLocation);

            drawStepInfo(ContextCompat.getDrawable(this, R.drawable.marker_departure),
                    getString(R.string.start_point), distToStartLoc + " m");

            mUpdateStartPointOnce = false;
            mProgressDialog.dismiss();

            return;
        }

        // getting direction icon depending on maneuver
        @SuppressLint("Recycle")
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
        int iconId = iconIds.getResourceId(mRoads[0].mNodes.get(mReachedNode).mManeuverType,
                R.drawable.ic_empty);
        Drawable image = ContextCompat.getDrawable(this, iconId);
        if (iconId != R.drawable.ic_empty) {
            image = ContextCompat.getDrawable(this, iconId);
        }

        // getting info from the current step to next.
        String instructions = "";
        if (mRoads[0].mNodes.get(mReachedNode).mInstructions != null) {
            instructions = mRoads[0].mNodes.get(mReachedNode).mInstructions;
        }
        String length = String.valueOf((int) (mRoads[0]
                .mNodes.get(mReachedNode).mLength * 1000)) + " m";
        drawStepInfo(image, instructions, length);

        int type = -1;
        for (int iLeg = 0; iLeg < mRoads[0].mLegs.size(); iLeg++) {

            int mStartNodeIndex = mRoads[0].mLegs.get(iLeg).mStartNodeIndex;
            int mEndNodeIndex = mRoads[0].mLegs.get(iLeg).mEndNodeIndex;

            if (mReachedNode == mEndNodeIndex) {
                mNextViaPoint += 1;
                type = 0; // update via and node
                break;
            }

            if (mReachedNode >= mStartNodeIndex && mReachedNode < mEndNodeIndex) {
                mNextViaPoint = iLeg;
                type = 1; // update node
                break;
            }
        }

        // no via anymore --> destination point
        if (mNextViaPoint == mViaPoints.size()) {
            // delete last viaPoint from mapOverlay
            updateUIWithItineraryMarkers(mNextViaPoint);
        }

        // no nodes anymore --> destination point
        if (mNextNode == mRoads[0].mNodes.size()) {
            // delete last node from mapOverlay
            updateRoadNodes(mRoads[0], mNextNode);

            // set distance between current node and destination point
            GeoPoint prevReachedNodeLoc = mRoads[0].mNodes.get(mReachedNode - 1).mLocation;
            GeoPoint lastReachedNodeLoc = mRoads[0].mNodes.get(mReachedNode).mLocation;
            mDistanceBetweenLoc = prevReachedNodeLoc.distanceTo(lastReachedNodeLoc);

            // add alert for the last point
            addProximityAlert(lastReachedNodeLoc.getLatitude(), lastReachedNodeLoc.getLongitude());
        }

        switch (type) {
            case 0:
                // update viaPoints and 1 node
                updateUIWithItineraryMarkers(mNextViaPoint);

            case 1:
                if (mReachedNode == 0) {
                    updateUIWithItineraryMarkers(mNextViaPoint);
                }
                // update nodes on map overlay
                updateRoadNodes(mRoads[0], mNextNode);

                // add alert for next location
                GeoPoint nextNodeLocation = mRoads[0].mNodes.get(mNextNode).mLocation;
                addProximityAlert(nextNodeLocation.getLatitude(), nextNodeLocation.getLongitude());

                // set new distance between current node and next node
                GeoPoint reachedNodeLocation = mRoads[0].mNodes.get(mReachedNode).mLocation;
                mDistanceBetweenLoc = reachedNodeLocation.distanceTo(nextNodeLocation);
                break;
            default:
                break;
        }

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * setup UI info about the route for the user
     */
    private void drawStepInfo(Drawable drawable, String instructions, String length) {
        // set maneuver icon
        updateManeuverIcon(drawable);

        // set maneuver instruction
        updateInstructionInfo(instructions);

        // set maneuver distance
        updateDistanceInfo(length);
    }

    /**
     * update maneuver icon until the next step
     */
    private void updateManeuverIcon(Drawable drawable) {
        ImageView ivManeuverIcon = (ImageView) findViewById(R.id.routeNavigationManeuverIcon);
        ivManeuverIcon.setImageBitmap(((BitmapDrawable) drawable).getBitmap());
    }

    /**
     * update textual instruction until the next step
     */
    //TODO calculate position for instruction if instruction too long for one line
    private void updateInstructionInfo(String instructions) {
        TextView ivManeuverInstruction = (TextView) findViewById(R.id.routeNavigationInstruction);
        ivManeuverInstruction.setText(instructions);
    }

    /**
     * update textual distance for user information
     */
    private void updateDistanceInfo(String length) {
        TextView ivManeuverDistance = (TextView) findViewById(R.id.routeNavigationDistance);
        ivManeuverDistance.setText(length);
    }

    /**
     * update all itinenary markers
     */
    public void updateUIWithItineraryMarkers() {
        updateUIWithItineraryMarkers(0);
    }

    /**
     * update itinenary markers from specific one
     */
    public void updateUIWithItineraryMarkers(int iVia) {
        mItineraryMarkers.closeAllInfoWindows();
        mItineraryMarkers.getItems().clear();

        //Start marker:
        if (mStartPoint != null) {
            ViaPointData viaPointData = new ViaPointData();
            viaPointData.setViaPointData(mStartPoint,
                    getResources().getString(R.string.departure), "", -1);
            if (mReachedNode >= 0) {
                // set start marker as visited if we reached the first node
                updateItineraryMarker(null, viaPointData, R.drawable.marker_departure_visited);
            } else {
                updateItineraryMarker(null, viaPointData, R.drawable.marker_departure);
            }
        }

        // update via markers before specific one as visited
        for (int index = 0; index < iVia; index++) {
            updateItineraryMarker(null, mViaPoints.get(index), R.drawable.marker_via_visited);
        }
        // update via markers after specific one as non-visited
        for (int index = iVia; index < mViaPoints.size() - 1; index++) {
            updateItineraryMarker(null, mViaPoints.get(index), R.drawable.marker_via);
        }

        // Destination marker: (as visited would be set
        updateItineraryMarker(null, mViaPoints.get(mViaPoints.size() - 1),
                R.drawable.marker_destination);

        if (mViaPoints.size() > 0 && mRoads != null) {
            if (mNextNode >= mRoads[0].mNodes.size()) {
                updateItineraryMarker(null, mViaPoints.get(mViaPoints.size() - 1),
                        R.drawable.marker_destination_visited);
            }
        }
    }

    /**
     * Update (or create if null) a marker in itineraryMarkers.
     */
    public Marker updateItineraryMarker(Marker marker, ViaPointData viaPointData, int markerResId) {
        Drawable icon = ContextCompat.getDrawable(this, markerResId);
        Drawable drawable = null;

        Map<String, Integer> data = new HashMap<>();
        data.put(viaPointData.getTitle(), viaPointData.getExhibitsId());

        if (viaPointData.getExhibitsId() > -1) {
            drawable = DBAdapter.getImage(viaPointData.getExhibitsId(), "image.jpg", 65);
        }

        marker = mMarker.addMarker(null, viaPointData.getTitle(), viaPointData.getDescription(),
                viaPointData.getGeoPoint(), drawable, icon, data);

        return marker;
    }

    /**
     * setup map orientation if tracking mode is on
     */
    void updateUIWithTrackingMode() {
        if (mTrackingMode) {
            mTrackingModeButton.setBackgroundResource(R.drawable.btn_tracking_on);
            if (mLocationOverlay.isEnabled() && mLocationOverlay.getLocation() != null) {
                mMap.getController().animateTo(mLocationOverlay.getLocation());
            }
            mMap.setMapOrientation(-mAzimuthAngleSpeed);
            mTrackingModeButton.setKeepScreenOn(true);
        } else {
            mTrackingModeButton.setBackgroundResource(R.drawable.btn_tracking_off);
            mMap.setMapOrientation(0.0f);
            mTrackingModeButton.setKeepScreenOn(false);
        }
    }

    boolean startLocationUpdates() {
        boolean result = false;
        for (final String provider : mGpsTracker.getLocationManager().getProviders(true)) {
            mGpsTracker.getLocationManager().requestLocationUpdates(
                    provider,
                    ExtendedLocationListener.MIN_TIME_BW_UPDATES,
                    ExtendedLocationListener.MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
            result = true;
        }
        return result;
    }

    /**
     * show all nodes on map overlay
     */
    private void putRoadNodes(Road road) {
        updateRoadNodes(road, 0);
    }

    /**
     * update nodes on map overlay from specific one
     */
    private void updateRoadNodes(Road road, int index) {
        mRoadNodeMarkers.getItems().clear();
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.marker_node);
        int n = road.mNodes.size();

        MarkerInfoWindow infoWindow = new MarkerInfoWindow(
                org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mMap);
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);

        for (int i = index; i < n; i++) {
            RoadNode node = road.mNodes.get(i);
            String instructions = (node.mInstructions == null ? "" : node.mInstructions);
            Marker nodeMarker = new Marker(mMap);
            nodeMarker.setTitle(getString(R.string.step) + " " + (i + 1));
            nodeMarker.setSnippet(instructions);
            nodeMarker.setSubDescription(
                    Road.getLengthDurationText(this, node.mLength, node.mDuration));
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(icon);
            nodeMarker.setInfoWindow(infoWindow); //use a shared infowindow.
            int iconId = iconIds.getResourceId(node.mManeuverType, R.drawable.ic_empty);
            if (iconId != R.drawable.ic_empty) {
                Drawable image = ContextCompat.getDrawable(this, iconId);
                nodeMarker.setImage(image);
            }
            mRoadNodeMarkers.add(nodeMarker);
        }
        iconIds.recycle();
    }

    /**
     * paint road lines with colors blue or red
     */
    void selectRoad(int roadIndex) {
        mSelectedRoad = roadIndex;
        putRoadNodes(mRoads[roadIndex]);

        for (int i = 0; i < mRoadOverlays.length; i++) {
            Paint p = mRoadOverlays[i].getPaint();
            if (i == roadIndex)
                p.setColor(getResources().getColor(R.color.colorPrimaryDark)); //blue
            else
                p.setColor(getResources().getColor(R.color.colorAccent)); // red
        }
        mMap.invalidate();
    }

    void updateUiWithRoads(Road[] roads) {
        mRoadNodeMarkers.getItems().clear();
        List<Overlay> mapOverlays = mMap.getOverlays();
        if (mRoadOverlays != null) {
            for (Polyline mRoadOverlay : mRoadOverlays) {
                mapOverlays.remove(mRoadOverlay);
            }
            mRoadOverlays = null;
        }
        if (roads == null || roads[0] == null) {
            return;
        }

        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(mMap.getContext(), R.string.technical_issue,
                    Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(mMap.getContext(), R.string.no_route, Toast.LENGTH_SHORT).show();
        mRoadOverlays = new Polyline[roads.length];
        for (int i = 0; i < roads.length; i++) {
            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i], this);
            mRoadOverlays[i] = roadPolyline;

            String routeDesc = roads[i].getLengthDurationText(this, -1);
            roadPolyline.setTitle(getString(R.string.route) + " - " + routeDesc);
            roadPolyline.setInfoWindow(
                    new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mMap));
            roadPolyline.setRelatedObject(i);
            mapOverlays.add(1, roadPolyline);
            //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
            //to avoid covering the other overlays.
        }
        selectRoad(0);
    }


    /**
     * Gets a road in the background and notifies the listener when its ready
     *
     * @param index The first waypoint of the road
     */
    public void getRoadAsync(int index) {
        mRoads = null;
        GeoPoint roadStartPoint = null;
        if (mStartPoint != null) {
            roadStartPoint = mStartPoint;
        } else if (mLocationOverlay.isEnabled() && mLocationOverlay.getLocation() != null) {
            //use my current location as itinerary start point:
            roadStartPoint = mLocationOverlay.getLocation();
        }

        if (roadStartPoint == null) {
            updateUiWithRoads(null);
            updateUIWithPolygon(mViaPoints, "");
            return;
        }
        ArrayList<GeoPoint> waypoints = new ArrayList<>(2);
        waypoints.add(roadStartPoint);

        //add intermediate via points:
        for (int i = index; i < mViaPoints.size(); i++) {
            waypoints.add(mViaPoints.get(i).getGeoPoint());
        }

        //waypoints.add(mDestinationPoint);
        new UpdateRoadTask().execute(waypoints);
    }

    /**
     * add or replace the polygon overlay
     */
    public void updateUIWithPolygon(ArrayList<ViaPointData> viaPoints, String name) {
        List<Overlay> mapOverlays = mMap.getOverlays();
        int location = -1;
        if (mDestinationPolygon != null)
            location = mapOverlays.indexOf(mDestinationPolygon);
        mDestinationPolygon = new Polygon(this);
        mDestinationPolygon.setFillColor(0x15FF0080);
        mDestinationPolygon.setStrokeColor(0x800000FF);
        mDestinationPolygon.setStrokeWidth(5.0f);
        mDestinationPolygon.setTitle(name);

        ArrayList<GeoPoint> polygon = new ArrayList<>();
        for (ViaPointData viaPoint : viaPoints) {
            polygon.add(viaPoint.getGeoPoint());
        }

        if (polygon.size() > 0) {
            mDestinationPolygon.setPoints(polygon);
        }

        if (location != -1) {
            mapOverlays.set(location, mDestinationPolygon);
        } else {
            mapOverlays.add(1, mDestinationPolygon); //insert just above the MapEventsOverlay.
        }

        mMap.invalidate();
    }

    /**
     * Adds an alert that triggers when a user is within a defined range of a specific coordinate
     *
     * @param latitude  the latitude of the alert point
     * @param longitude the longitude of the alert point
     */
    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent(PROX_ALERT);

        mProximityIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mGpsTracker.getLocationManager().addProximityAlert(
                // the latitude of the central point of the alert region
                latitude,
                // the longitude of the central point of the alert region
                longitude,
                // the radius of the central point of the alert region, in meters
                POINT_RADIUS,
                // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                PROX_ALERT_EXPIRATION,
                // will be used to generate an Intent to fire when entry to
                // or exit from the alert region is detected
                mProximityIntent
        );
    }

    /**
     * callback to store activity status before a restart (orientation change for instance)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVEDSTATE_LOCATION, mLocationOverlay.getLocation());
        outState.putBoolean(SAVEDSTATE_TRACKING_MODE, mTrackingMode);
        outState.putParcelable(SAVEDSTATE_START, mStartPoint);
        //outState.putParcelable(SAVEDSTATE_DESTINATION, mDestinationPoint);
        //outState.putParcelableArrayList(SAVEDSTATE_VIAPOINTS, mViaPoints);
        outState.putInt(SAVEDSTATE_REACHED_NODE, mReachedNode);
        outState.putInt(SAVEDSTATE_NEXT_NODE, mNextNode);
        outState.putInt(SAVEDSTATE_NEXT_VIA_POINT, mNextViaPoint);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mLocationOverlay.setAccuracy(accuracy);
        mMap.invalidate();

    }

    @Override
    protected void onResume() {
        boolean isOneProviderEnabled = startLocationUpdates();
        mLocationOverlay.setEnabled(isOneProviderEnabled);

        IntentFilter filter = new IntentFilter(PROX_ALERT);
        registerReceiver(mProximityIntentReceiver, filter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mGpsTracker.getLocationManager().removeUpdates(this);
        unregisterReceiver(mProximityIntentReceiver);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("onBackPressed", true);
        setResult(RESULT_OK, mIntent);

        super.onBackPressed();
    }

    /**
     * Async task to get the road in a separate thread.
     */
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        protected void onPreExecute() {
            mProgressDialog.setMessage(getString(R.string.download_road));
            mProgressDialog.show();
        }

        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new MapQuestRoadManager(getString(R.string.map_quest_key));
            roadManager.addRequestOption("routeType=pedestrian");
            roadManager.addRequestOption("locale=de_DE");

            return roadManager.getRoads(waypoints);
        }

        protected void onPostExecute(Road[] result) {
            mProgressDialog.setMessage(getString(R.string.creating_map));
            mRoads = result;
            if (mRoads != null) {
                updateUiWithRoads(result);

                // TODO Remove this as soon as no needs to run on emulator
                // needed to set route info and dismiss busy waiting dialog on emulator
                if (Build.MODEL.contains("google_sdk") ||
                        Build.MODEL.contains("Emulator") ||
                        Build.MODEL.contains("Android SDK")) {
                    if (mRoads[0] != null && mRoads[0].mNodes != null) {
                        setNextStepToAlert(mRoads[0].mNodes.get(0).mLocation);
                    }
                }

            }
        }
    }

    /**
     * Gets called when the user approaches a node
     */
    public class ProximityIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = LocationManager.KEY_PROXIMITY_ENTERING;
            Boolean entering = intent.getBooleanExtra(key, false);
            if (entering) {
                //We entered near a node, count up the reached nodes
                mReachedNode++;
                mGpsTracker.getLocationManager().removeProximityAlert(mProximityIntent);
            }
        }
    }
}

