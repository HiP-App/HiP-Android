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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
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
import java.util.List;

import de.upb.hip.mobile.helpers.GPSTracker;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.models.Route;

/**
 * Create the route on the map and show step by step instruction for the navigation
 */

public class RouteNavigationActivity extends Activity implements MapEventsReceiver, LocationListener, SensorEventListener {
    protected final int START_INDEX = -2, DEST_INDEX = -1;
    protected final long LOCATION_UPDATES_TIME = 2000; //in miliseconds
    protected final float LOCATION_UPDATES_DIST = 2; // in meters
    protected final int ROUTE_REJECT = 25; //in meters
    protected final String PROX_ALERT = getPackageName() + ".PROX_ALERT";
    protected final long POINT_RADIUS = 5; // in Meters
    protected final long PROX_ALERT_EXPIRATION = -1; //indicate no expiration
    protected Road[] mRoads;
    // LocationListener implementation
    protected MapView mMap;
    protected GeoPoint mStartPoint, mDestinationPoint;
    protected ArrayList<GeoPoint> mViaPoints;
    protected FolderOverlay mItineraryMarkers;
    // for departure, destination and viapoints
    protected Marker mMarkerStart, mMarkerDestination;
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
    protected GPSTracker mGPSTracker;
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
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.map);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        mMap = genericMap.getMapView();

        mMap.setTileSource(TileSourceFactory.MAPNIK);

        // getting location
        mGPSTracker = new GPSTracker(RouteNavigationActivity.this);
        GeoPoint geoLocation = new GeoPoint(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());

        // mMap prefs:
        IMapController mapController = mMap.getController();
        mapController.setZoom(19);
        mapController.setCenter(geoLocation);

        // Itinerary markers:
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        mMap.getOverlays().add(mItineraryMarkers);
        mViaPointInfoWindow = new ViaPointInfoWindow(R.layout.navigation_itinerary_bubble, mMap);

        mLocationOverlay = new DirectedLocationOverlay(this);
        mMap.getOverlays().add(mLocationOverlay);

        if (savedInstanceState == null) {
            //getting route from intent
            Route route = (Route) getIntent().getSerializableExtra("route");

            // init start and end point
            int size = route.waypoints.size();
            mStartPoint = geoLocation;
            mDestinationPoint = new GeoPoint(route.waypoints.get(size - 1).latitude,
                    route.waypoints.get(size - 1).longitude);

            // add viapoints
            mViaPoints = new ArrayList<>();
            for (int i = 0; i < (size - 1); i++) {
                GeoPoint via = new GeoPoint(route.waypoints.get(i).latitude,
                        route.waypoints.get(i).longitude);
                addViaPoint(via);
            }

            getRoadAsync();

        } else {
            mLocationOverlay.setLocation((GeoPoint) savedInstanceState.getParcelable("location"));
            mStartPoint = savedInstanceState.getParcelable("start");
            mDestinationPoint = savedInstanceState.getParcelable("destination");
            mViaPoints = savedInstanceState.getParcelableArrayList("viapoints");
            mReachedNode = savedInstanceState.getInt("mReachedNode");
            mNextNode = savedInstanceState.getInt("mNextNode");
            mNextViaPoint = savedInstanceState.getInt("mNextViaPoint");
        }

        // calculate distance between current location and start point
        // if the start location was not reached
        mDistanceBetweenLoc = geoLocation.distanceTo(mStartPoint);

        updateUIWithItineraryMarkers();

        // a scale bar in the top-left corner of the screen
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        //Tracking system:
        mTrackingModeButton = (Button) findViewById(R.id.buttonTrackingMode);
        mTrackingModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTrackingMode = !mTrackingMode;
                updateUIWithTrackingMode();
            }
        });

        if (savedInstanceState != null) {
            mTrackingMode = savedInstanceState.getBoolean("tracking_mode");
            updateUIWithTrackingMode();
        } else
            mTrackingMode = false;

        mRoadNodeMarkers = new FolderOverlay(this);
        //mRoadNodeMarkers.setName("Route Steps");
        mMap.getOverlays().add(mRoadNodeMarkers);

        if (savedInstanceState != null) {
            updateUIWithRoads(mRoads);
        }
    }

    /**
     * LocationListener implementation
     */
    @Override
    public void onLocationChanged(final Location pLoc) {

        GeoPoint newLocation = new GeoPoint(pLoc);
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
        mLocationOverlay.setAccuracy((int) pLoc.getAccuracy());

        GeoPoint nextNearestLocation = getNextNodeLocation();
        if (nextNearestLocation != null && prevLocation != null) {
            if (pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
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

        if (mRoads[0].mNodes != null && mRoads[0].mNodes.size() > 0) {
            // find next point
            nextNodeLocation = mRoads[0].mNodes.get(mNextNode).mLocation;
        } else {
            // if no node anymore => destination point
            nextNodeLocation = mDestinationPoint;
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
            GeoPoint nextLocation = new GeoPoint(mRoads[0].mNodes.get(0).mLocation.getLatitude(), mRoads[0].mNodes.get(0).mLocation.getLongitude());
            addProximityAlert(nextLocation.getLatitude(), nextLocation.getLongitude());
            int distToStartLoc = geo.distanceTo(nextLocation);

            drawStepInfo(ContextCompat.getDrawable(this, R.drawable.marker_departure), getString(R.string.start_point), distToStartLoc + " m");

            mUpdateStartPointOnce = false;
            mProgressDialog.dismiss();

            return;
        }

        // getting direction icon depending on maneuver
        @SuppressLint("Recycle")
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
        int iconId = iconIds.getResourceId(mRoads[0].mNodes.get(mReachedNode).mManeuverType, R.drawable.ic_empty);
        Drawable image = ContextCompat.getDrawable(this, iconId);
        if (iconId != R.drawable.ic_empty) {
            image = ContextCompat.getDrawable(this, iconId);
        }

        // getting info from the current step to next.
        String instructions = (mRoads[0].mNodes.get(mReachedNode).mInstructions == null ? "" : mRoads[0].mNodes.get(mReachedNode).mInstructions);
        String length = String.valueOf(mRoads[0].mNodes.get(mReachedNode).mLength) + " m";
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
            addProximityAlert(mDestinationPoint.getLatitude(), mDestinationPoint.getLongitude());
        }

        if (mNextNode >= mRoads[0].mNodes.size()) {
            type = 2;
        }

        switch (type) {
            case 0:
                // update viaPoints and 1 node
                updateUIWithItineraryMarkers(mNextViaPoint);

            case 1:
                // update only node and if needed start marker
                if (mReachedNode == 0) {
                    if (mStartPoint != null) {
                        // set start marker as visited if we reached the first node
                        mMarkerStart = updateItineraryMarker(null, mStartPoint, START_INDEX,
                                R.string.departure, R.drawable.marker_departure_visited, -1, null);
                    }
                }

                // update nodes on map overlay
                updateRoadNodes(mRoads[0], mNextNode);

                // add alert for next location
                GeoPoint nextNodeLocation = mRoads[0].mNodes.get(mNextNode).mLocation;
                addProximityAlert(nextNodeLocation.getLatitude(), nextNodeLocation.getLongitude());

                // set new distance between current node and next node
                mDistanceBetweenLoc = mRoads[0].mNodes.get(mReachedNode).mLocation.distanceTo(mRoads[0].mNodes.get(mNextNode).mLocation);
                break;
            case 2:
                if (mDestinationPoint != null) {
                    mMarkerDestination = updateItineraryMarker(null, mDestinationPoint, DEST_INDEX,
                            R.string.destination, R.drawable.marker_destination_visited, -1, null);
                }
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

        // set maneuver icon
        ImageView ivManeuverIcon = (ImageView) findViewById(R.id.imageView_maneuverIcon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap newImage = Bitmap.createBitmap(ivManeuverIcon.getWidth(), ivManeuverIcon.getHeight(), Bitmap.Config.ARGB_8888);

        int maneuverIconCenterX = (ivManeuverIcon.getWidth() - bitmap.getWidth()) / 2;
        int maneuverIconCenterY = (ivManeuverIcon.getHeight() - bitmap.getHeight()) / 2;

        Canvas cManeuverIcon = new Canvas(newImage);
        cManeuverIcon.drawBitmap(bitmap, maneuverIconCenterX, maneuverIconCenterY, null);

        ivManeuverIcon.setImageBitmap(newImage);
    }

    /**
     * update textual instruction until the next step
     */
    //TODO calculate position for instruction if instruction too long for one line
    private void updateInstructionInfo(String instructions) {

        Paint paint = getPaintForTextInstructions();

        // set maneuver instruction
        ImageView ivManeuverInstruction = (ImageView) findViewById(R.id.imageView_routeInstruction);
        Bitmap newImage = Bitmap.createBitmap(ivManeuverInstruction.getWidth(), ivManeuverInstruction.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cManeuverInfo = new Canvas(newImage);

        Rect bounds = new Rect();
        paint.getTextBounds(instructions, 0, instructions.length(), bounds);

        int textY = cManeuverInfo.getHeight() / 2 - bounds.height() / 2;
        int textX = cManeuverInfo.getWidth() / 2;

        cManeuverInfo.drawText(instructions, textX, textY, paint);
        ivManeuverInstruction.setImageBitmap(newImage);
    }

    /**
     * update textual distance for user information
     */
    private void updateDistanceInfo(String length) {

        Paint paint = getPaintForTextInstructions();

        ImageView ivManeuverDistance = (ImageView) findViewById(R.id.imageView_routeDistance);
        Bitmap newImage = Bitmap.createBitmap(ivManeuverDistance.getWidth(), ivManeuverDistance.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cManeuverDistance = new Canvas(newImage);

        Rect bounds = new Rect();
        paint.getTextBounds(length, 0, length.length(), bounds);

        int textY = cManeuverDistance.getHeight() / 2;
        int textX = cManeuverDistance.getWidth() / 2;

        cManeuverDistance.drawText(length, textX, textY, paint);
        ivManeuverDistance.setImageBitmap(newImage);
    }

    /**
     * get style for drawing the text
     */
    private Paint getPaintForTextInstructions() {
        int textSize = 50;
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);

        return paint;
    }

    /**
     * add via point on map overlay
     */
    public void addViaPoint(GeoPoint p) {
        mViaPoints.add(p);
        updateItineraryMarker(null, p, mViaPoints.size() - 1,
                R.string.viapoint, R.drawable.marker_via, -1, null);
    }

    /** update all itinenary markers */
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
            if (mReachedNode >= 0) {
                // set start marker as visited if we reached the first node
                mMarkerStart = updateItineraryMarker(null, mStartPoint, START_INDEX,
                        R.string.departure, R.drawable.marker_departure_visited, -1, null);
            } else {
                mMarkerStart = updateItineraryMarker(null, mStartPoint, START_INDEX,
                        R.string.departure, R.drawable.marker_departure, -1, null);
            }
        }

        // update via markers before specific one as visited
        for (int index = 0; index < iVia; index++) {
            updateItineraryMarker(null, mViaPoints.get(index), index,
                    R.string.viapoint, R.drawable.marker_via_visited, -1, null);
        }
        // update via markers after specific one as non-visited
        for (int index = iVia; index < mViaPoints.size(); index++) {
            updateItineraryMarker(null, mViaPoints.get(index), index,
                    R.string.viapoint, R.drawable.marker_via, -1, null);
        }

        // Destination marker: (as visited would be set
        if (mDestinationPoint != null) {
            mMarkerDestination = updateItineraryMarker(null, mDestinationPoint, DEST_INDEX,
                    R.string.destination, R.drawable.marker_destination, -1, null);
        }
    }

    /**
     * Update (or create if null) a marker in itineraryMarkers.
     */
    public Marker updateItineraryMarker(Marker marker, GeoPoint p, int index,
                                        int titleResId, int markerResId, int imageResId, String address) {
        Drawable icon = ContextCompat.getDrawable(this, markerResId);
        String title = getResources().getString(titleResId);
        if (marker == null) {
            marker = new Marker(mMap);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(mViaPointInfoWindow);
            marker.setDraggable(true);
            mItineraryMarkers.add(marker);
        }
        marker.setTitle(title);
        marker.setPosition(p);
        marker.setIcon(icon);
        if (imageResId != -1)
            marker.setImage(ContextCompat.getDrawable(this, imageResId));
        marker.setRelatedObject(index);
        mMap.invalidate();

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
        for (final String provider : mGPSTracker.getLocationManager().getProviders(true)) {
            mGPSTracker.getLocationManager().requestLocationUpdates(provider, LOCATION_UPDATES_TIME, LOCATION_UPDATES_DIST, this);
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

        MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mMap);
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);

        for (int i = index; i < n; i++) {
            RoadNode node = road.mNodes.get(i);
            String instructions = (node.mInstructions == null ? "" : node.mInstructions);
            Marker nodeMarker = new Marker(mMap);
            nodeMarker.setTitle(getString(R.string.step) + " " + (i + 1));
            nodeMarker.setSnippet(instructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
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

    /** paint road lines with colors blue or red */
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

    void updateUIWithRoads(Road[] roads) {
        mRoadNodeMarkers.getItems().clear();
        List<Overlay> mapOverlays = mMap.getOverlays();
        if (mRoadOverlays != null) {
            for (Polyline mRoadOverlay : mRoadOverlays) {
                mapOverlays.remove(mRoadOverlay);
            }
            mRoadOverlays = null;
        }
        if (roads == null)
            return;
        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(mMap.getContext(), R.string.technical_issue, Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(mMap.getContext(), R.string.no_route, Toast.LENGTH_SHORT).show();
        mRoadOverlays = new Polyline[roads.length];
        for (int i = 0; i < roads.length; i++) {
            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i], this);
            mRoadOverlays[i] = roadPolyline;

            String routeDesc = roads[i].getLengthDurationText(this, -1);
            roadPolyline.setTitle(getString(R.string.route) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mMap));
            roadPolyline.setRelatedObject(i);
            mapOverlays.add(1, roadPolyline);
            //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
            //to avoid covering the other overlays.
        }
        selectRoad(0);
    }

    public void getRoadAsync() {
        getRoadAsync(0);
    }

    public void getRoadAsync(int index) {
        mRoads = null;
        GeoPoint roadStartPoint = null;
        if (mStartPoint != null) {
            roadStartPoint = mStartPoint;
        } else if (mLocationOverlay.isEnabled() && mLocationOverlay.getLocation() != null) {
            //use my current location as itinerary start point:
            roadStartPoint = mLocationOverlay.getLocation();
        }
        if (roadStartPoint == null || mDestinationPoint == null) {
            updateUIWithRoads(null);
            updateUIWithPolygon(mViaPoints, "");
            return;
        }
        ArrayList<GeoPoint> waypoints = new ArrayList<>(2);
        waypoints.add(roadStartPoint);

        //add intermediate via points:
        for (int i = index; i < mViaPoints.size(); i++) {
            waypoints.add(mViaPoints.get(i));
        }

        waypoints.add(mDestinationPoint);
        new UpdateRoadTask(this).execute(waypoints);
    }

    /**
     * add or replace the polygon overlay
     */
    public void updateUIWithPolygon(ArrayList<GeoPoint> polygon, String name) {
        List<Overlay> mapOverlays = mMap.getOverlays();
        int location = -1;
        if (mDestinationPolygon != null)
            location = mapOverlays.indexOf(mDestinationPolygon);
        mDestinationPolygon = new Polygon(this);
        mDestinationPolygon.setFillColor(0x15FF0080);
        mDestinationPolygon.setStrokeColor(0x800000FF);
        mDestinationPolygon.setStrokeWidth(5.0f);
        mDestinationPolygon.setTitle(name);
        if (polygon != null) {
            mDestinationPolygon.setPoints(polygon);
        }
        if (location != -1)
            mapOverlays.set(location, mDestinationPolygon);
        else
            mapOverlays.add(1, mDestinationPolygon); //insert just above the MapEventsOverlay.
        mMap.invalidate();
    }

    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent(PROX_ALERT);

        mProximityIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mGPSTracker.getLocationManager().addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                mProximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
    }

    /**
     * callback to store activity status before a restart (orientation change for instance)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("location", mLocationOverlay.getLocation());
        outState.putBoolean("tracking_mode", mTrackingMode);
        outState.putParcelable("start", mStartPoint);
        outState.putParcelable("destination", mDestinationPoint);
        outState.putParcelableArrayList("viapoints", mViaPoints);
        outState.putInt("mReachedNode", mReachedNode);
        outState.putInt("mNextNode", mNextNode);
        outState.putInt("mNextViaPoint", mNextViaPoint);
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

        mGPSTracker.getLocationManager().removeUpdates(this);
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
        private final Context mContext;

        private UpdateRoadTask(Context mContext) {
            this.mContext = mContext;
        }

        protected void onPreExecute() {
            mProgressDialog.setMessage(getString(R.string.downlod_road));
            mProgressDialog.show();
        }

        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new MapQuestRoadManager(getString(R.string.mapquest_key));
            roadManager.addRequestOption("routeType=pedestrian");
            roadManager.addRequestOption("locale=de_DE");

            return roadManager.getRoads(waypoints);
        }

        protected void onPostExecute(Road[] result) {
            mProgressDialog.setMessage(getString(R.string.creating_map));
            mRoads = result;
            if (mRoads != null) {
                updateUIWithRoads(result);
            }
        }
    }

    /**
     * A customized InfoWindow handling "itinerary" points (start, destination and via-points).
     * We inherit from MarkerInfoWindow as it already provides most of what we want.
     */
    public class ViaPointInfoWindow extends MarkerInfoWindow {

        int mSelectedPoint;

        public ViaPointInfoWindow(int layoutResId, MapView mapView) {
            super(layoutResId, mapView);
        }

        @Override
        public void onOpen(Object item) {
            Marker eItem = (Marker) item;
            mSelectedPoint = (Integer) eItem.getRelatedObject();
            super.onOpen(item);
        }
    }

    public class ProximityIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = LocationManager.KEY_PROXIMITY_ENTERING;
            Boolean entering = intent.getBooleanExtra(key, false);
            if (entering) {
                mReachedNode++;
                mGPSTracker.getLocationManager().removeProximityAlert(mProximityIntent);
            }
        }
    }
}

