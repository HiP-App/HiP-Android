package de.upb.hip.mobile.activities;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
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

public class RouteNavigationActivity extends Activity implements MapEventsReceiver, LocationListener, SensorEventListener {
    public static Road[] mRoads;  //made static to pass between activities

    protected static int START_INDEX = -2, DEST_INDEX = -1;
    //final OnItineraryMarkerDragListener mItineraryListener = new OnItineraryMarkerDragListener();
    //------------ LocationListener implementation
    protected MapView map;
    protected GeoPoint startPoint, destinationPoint;
    protected LatLng mLatLngLocation;
    protected ArrayList<GeoPoint> viaPoints;
    protected FolderOverlay mItineraryMarkers;

    protected final static int DOWNLOAD_TO_CACHE = 0, CLEAR_CACHE = 1, USE_CACHE = 2;

    //for departure, destination and viapoints
    protected Marker markerStart, markerDestination;
    protected ViaPointInfoWindow mViaPointInfoWindow;
    protected DirectedLocationOverlay myLocationOverlay;
    protected boolean mTrackingMode = false;
    protected Polygon mDestinationPolygon; //enclosing polygon of destination location
    protected int mSelectedRoad;
    protected Polyline[] mRoadOverlays;
    protected FolderOverlay mRoadNodeMarkers;
    Button mTrackingModeButton;
    float mAzimuthAngleSpeed = 0.0f;

    public GPSTracker gps;

    private static final long POINT_RADIUS = 5; // in Meters
    private static final long PROX_ALERT_EXPIRATION = 2000;
    private int reachedNode = -2;
    private int nextNode = 0;
    private int nextVia = 0;

    private int distanceToNextLoc = -1;

    public ProximityIntentReceiver proximityIntentReceiver = new ProximityIntentReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_navigation);

        GenericMapView genericMap = (GenericMapView) findViewById(R.id.map);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        map = genericMap.getMapView();

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        gps = new GPSTracker(RouteNavigationActivity.this);
        if (!gps.canGetLocation()) {
            // wifi is disaibeled.
            gps.showSettingsAlert();
        }
        mLatLngLocation = new LatLng(gps.getLatitude(), gps.getLongitude());

        //map prefs:
        IMapController mapController = map.getController();
        mapController.setZoom(19);
        mapController.setCenter(new GeoPoint(mLatLngLocation.latitude, mLatLngLocation.longitude));

        // Itinerary markers:
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        map.getOverlays().add(mItineraryMarkers);
        mViaPointInfoWindow = new ViaPointInfoWindow(R.layout.itinerary_bubble, map);

        myLocationOverlay = new DirectedLocationOverlay(this);
        map.getOverlays().add(myLocationOverlay);

/*        GeoPoint nextLoc = getNextLocation();
        GeoPoint loc = new GeoPoint(gps.getLocation());
        if (nextLoc != null) {
            distanceToNextLoc = loc.distanceTo(nextLoc);
        }*/

        if (savedInstanceState == null) {
            Route route = (Route) getIntent().getSerializableExtra("route");
            Location location = gps.getLocation();

            if (location != null) {
                //location known:
                onLocationChanged(location);
            } else {
                //no location known: hide myLocationOverlay
                myLocationOverlay.setEnabled(false);
            }

            int size = route.waypoints.size();
            startPoint = new GeoPoint(mLatLngLocation.latitude, mLatLngLocation.longitude);
            destinationPoint = new GeoPoint(route.waypoints.get(size - 1).latitude, route.waypoints.get(size - 1).longitude);

            viaPoints = new ArrayList<>();
            for (int i = 0; i < (size - 1); i++){
                GeoPoint via = new GeoPoint(route.waypoints.get(i).latitude, route.waypoints.get(i).longitude);
                addViaPoint(via);
            }
            getRoadAsync();
        } else {
            myLocationOverlay.setLocation((GeoPoint) savedInstanceState.getParcelable("location"));
            startPoint = savedInstanceState.getParcelable("start");
            destinationPoint = savedInstanceState.getParcelable("destination");
            viaPoints = savedInstanceState.getParcelableArrayList("viapoints");
            reachedNode = savedInstanceState.getInt("reachedNode");
            nextNode = savedInstanceState.getInt("nextNode");
            nextVia = savedInstanceState.getInt("nextVia");

            setNextStepToAlert(null);
        }

        updateUIWithItineraryMarkers();

        // a scale bar in the top-left corner of the screen
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        map.getOverlays().add(scaleBarOverlay);

        //Tracking system:
        mTrackingModeButton = (Button) findViewById(R.id.buttonTrackingMode);
        mTrackingModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTrackingMode = !mTrackingMode;
                updateUIWithTrackingMode();
            }
        });

        if (savedInstanceState != null){
            mTrackingMode = savedInstanceState.getBoolean("tracking_mode");
            updateUIWithTrackingMode();
        } else
            mTrackingMode = false;

        mRoadNodeMarkers = new FolderOverlay(this);
        mRoadNodeMarkers.setName("Route Steps");
        map.getOverlays().add(mRoadNodeMarkers);

        if (savedInstanceState != null) {
            updateUIWithRoads(mRoads);
            setNextStepToAlert(new GeoPoint(gps.getLocation()));
        }

        //for test purposes. DELETE
        ImageView imageView_routeStepInfo = (ImageView) findViewById(R.id.imageView_routeStepInfo);
        imageView_routeStepInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                reachedNode += 1;
            }
        });
    }

/*    private void cacheUsage(int id) {
        switch (id) {
            case DOWNLOAD_TO_CACHE: {
                CacheManager cacheManager = new CacheManager(map);
                int zoomMin = map.getZoomLevel();
                int zoomMax = map.getZoomLevel() + 4;
                cacheManager.downloadAreaAsync(this, map.getBoundingBox(), zoomMin, zoomMax);
            }
            case CLEAR_CACHE: {
                CacheManager cacheManager = new CacheManager(map);
                int zoomMin = map.getZoomLevel();
                int zoomMax = map.getZoomLevel() + 7;
                cacheManager.cleanAreaAsync(this, map.getBoundingBox(), zoomMin, zoomMax);
            }
            case USE_CACHE: {
                CacheManager cacheManager = new CacheManager(map);
                long cacheUsage = cacheManager.currentCacheUsage() / (1024 * 1024);
                long cacheCapacity = cacheManager.cacheCapacity() / (1024 * 1024);
                float percent = 100.0f * cacheUsage / cacheCapacity;
                String message = "Cache usage:\n" + cacheUsage + " Mo / " + cacheCapacity + " Mo = " + (int) percent + "%";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }*/

    //------------ LocationListener implementation
    @Override public void onLocationChanged(final Location pLoc) {

        GeoPoint newLocation = new GeoPoint(pLoc);
        if (!myLocationOverlay.isEnabled()){
            //we get the location for the first time:
            myLocationOverlay.setEnabled(true);
            map.getController().animateTo(newLocation);
        }

        if (reachedNode == nextNode) {
            //updateUIWithRoads(new GeoPoint(pLoc));
            nextNode += 1;
            setNextStepToAlert(newLocation);
        }
        else if (reachedNode == -2){
            setNextStepToAlert(newLocation);
        }
/*
        if (distanceToNextLoc > 0){
            recalculateRoute(newLocation);
            distanceToNextLoc = -1;
        }*/

        GeoPoint prevLocation = myLocationOverlay.getLocation();
        myLocationOverlay.setLocation(newLocation);
        myLocationOverlay.setAccuracy((int) pLoc.getAccuracy());

        GeoPoint nextNearestLocation = getNextLocation();
        if (nextNearestLocation != null && pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)) {

            mAzimuthAngleSpeed = (float)prevLocation.bearingTo(nextNearestLocation);
            //mAzimuthAngleSpeed = pLoc.getBearing();
            myLocationOverlay.setBearing(mAzimuthAngleSpeed);
        }

        if (mTrackingMode){
            //keep the map view centered on current location:
            map.getController().animateTo(newLocation);
            map.setMapOrientation(-mAzimuthAngleSpeed);
        } else {
            //just redraw the location overlay:
            map.invalidate();
        }

    }

/*    void updateUIWithRoads(GeoPoint loc) {
        ArrayList<GeoPoint> mPoint = (ArrayList<GeoPoint>) mRoadOverlays[0].getPoints();

        GeoPoint nextNearestLocation = null;
        if (mRoads[0].mNodes != null && mRoads[0].mNodes.size() > 0) {
            nextNearestLocation = mRoads[0].mNodes.get(nextNode).mLocation;
        }

        int distanceFromCurrentLoc = loc.distanceTo(nextNearestLocation);
        int distanceFromPointLoc1;
        int distanceFromPointLoc2;

        for (int i = 0; i < mPoint.size(); i++){
            distanceFromPointLoc1 = loc.distanceTo(mPoint.get(i));

            if ( (i+1) < mPoint.size()) {
                distanceFromPointLoc2 = loc.distanceTo(mPoint.get(i+1));

                if ( (distanceFromPointLoc1 - distanceFromPointLoc2) > 1){
                   *//* if (!isBetween(distanceFromPointLoc1 -1, distanceFromPointLoc1 + 1, distanceFromCurrentLoc)){
                        mPoint.get(i).setCoordsE6(nextNearestLocation.getLatitudeE6(), nextNearestLocation.getLongitudeE6());
                        redrawPoly(mPoint);
                        break;
                    }*//*
                }


            }


        }


    }

    private void redrawPoly(ArrayList<GeoPoint> mPoint){

        for (int i=0; i<mRoadOverlays.length; i++){
            Paint p = mRoadOverlays[i].getPaint();
            if (i == 0)
                p.setColor(0x800000FF); //blue
            else
                p.setColor(0x90666666); //grey
        }
        map.invalidate();

    }
    public boolean isBetween(int a, int b, int c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }*/

    private GeoPoint getNextLocation(){
        if (mRoads == null){
            return null;
        }

        GeoPoint nextNearestLocation = null;

        // find next point after start:
        // firstly check minimal point (Node)
        if (mRoads[0].mNodes != null && mRoads[0].mNodes.size() > 0) {
            nextNearestLocation = mRoads[0].mNodes.get(nextNode).mLocation;
        }
        // if no node check via point
        else if (viaPoints != null && viaPoints.size() > 0){
            nextNearestLocation = viaPoints.get(nextVia);
        }
        // if no node and via => it is destination point
        else{
            nextNearestLocation = destinationPoint;
        }

        return nextNearestLocation;
    }

    /*    private void recalculateRoute(GeoPoint currentLoc){
            GeoPoint nextLoc = getNextLocation();
            int distFromCurrent = currentLoc.distanceTo(nextLoc);
            int percentChangeDistance = (distFromCurrent - distanceToNextLoc) / distFromCurrent * 100;

            if (percentChangeDistance > 25);{
                getRoadAsync();
                updateUIWithItineraryMarkers();
            }
        }
        */
    private void setNextStepToAlert(GeoPoint geo) {
        // mRoads == null    --> no sence to do anything
        // reachedNode == -1 --> that means ProximityAlert didnt happen or we didnt go through first nearest point
        if (mRoads == null || reachedNode == -1 || reachedNode >= mRoads[0].mNodes.size()){
            return;
        }

        // this "if clauses" is for first run to setup the nearest point from current point to ProximityAlert
        // reachedNode would be set to -1
        if (reachedNode == -2){ // init step
            reachedNode += 1;

            GeoPoint nextNearestLocation = getNextLocation();
            addProximityAlert(nextNearestLocation.getLatitude(), nextNearestLocation.getLongitude());
            int distanceTo = geo.distanceTo(nextNearestLocation);

            drawStepInfo(getResources().getDrawable(R.drawable.marker_departure), "Start", String.valueOf(distanceTo) + "m");
            return;
        }

        // getting direction icon depending on maneuver
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
        int iconId = iconIds.getResourceId(mRoads[0].mNodes.get(reachedNode).mManeuverType, R.drawable.ic_empty);
        Drawable image = getResources().getDrawable(iconId);
        if (iconId != R.drawable.ic_empty){
            image = getResources().getDrawable(iconId);
        }

        // getting info from the current step to next.
        String instructions = (mRoads[0].mNodes.get(reachedNode).mInstructions==null ? "" : mRoads[0].mNodes.get(reachedNode).mInstructions);
        String lenDur = Road.getLengthDurationText(this, mRoads[0].mNodes.get(reachedNode).mLength, mRoads[0].mNodes.get(reachedNode).mDuration);
        drawStepInfo(image, instructions, lenDur);

        int type = 0;
        for (int iLeg = 0; iLeg < mRoads[0].mLegs.size(); iLeg++) {

            int mStartNodeIndex = mRoads[0].mLegs.get(iLeg).mStartNodeIndex;
            int mEndNodeIndex = mRoads[0].mLegs.get(iLeg).mEndNodeIndex;

            if (reachedNode == mEndNodeIndex) {
                nextVia += 1;
                type = 0; // update via
                break;
            }

            if (reachedNode >= mStartNodeIndex && reachedNode < mEndNodeIndex) {
                nextVia = iLeg;
                type = 1; // updatenode
                break;
            }
        }

        if (nextVia >= viaPoints.size()){ // no via anymore --> destination point
            updateUIWithItineraryMarkers(nextVia);
        }

        if (nextNode >= mRoads[0].mNodes.size()){ // no nodes anymore --> destination point
            updateRoadNodes(mRoads[0], nextNode);

            //distanceToNextLoc = mRoads[0].mNodes.get(reachedNode - 1).mLocation.distanceTo(mRoads[0].mNodes.get(reachedNode).mLocation);
        }

        switch (type) {
            case 0: // update via and 1 node
                updateUIWithItineraryMarkers(nextVia);

            case 1: // update only node
                updateRoadNodes(mRoads[0], nextNode);

                GeoPoint nextNodeLocation = viaPoints.get(nextVia);
                addProximityAlert(nextNodeLocation.getLatitude(), nextNodeLocation.getLongitude());

                //distanceToNextLoc = mRoads[0].mNodes.get(reachedNode).mLocation.distanceTo(mRoads[0].mNodes.get(nextNode).mLocation);
                break;
        }
    }

    private void drawStepInfo(Drawable drawable, String instructions, String lenDur){

        // set maneuver icon
        ImageView ivManeuverIcon = (ImageView)findViewById(R.id.imageView_maneuverIcon);
        Bitmap newImage = Bitmap.createBitmap(ivManeuverIcon.getWidth(), ivManeuverIcon.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        int maneuverIconCenterX = (ivManeuverIcon.getWidth() - bitmap.getWidth()) / 2;
        int maneuverIconCenterY = (ivManeuverIcon.getHeight() - bitmap.getHeight()) / 2;

        Canvas cManeuverIcon = new Canvas(newImage);
        cManeuverIcon.drawBitmap(bitmap, maneuverIconCenterX, maneuverIconCenterY, null);

        ivManeuverIcon.setImageBitmap(newImage);

        // set maneuver info
        ImageView ivManeuverInfo = (ImageView)findViewById(R.id.imageView_routeStepInfo);
        newImage = Bitmap.createBitmap(ivManeuverInfo.getWidth(), ivManeuverInfo.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cManeuverInfo = new Canvas(newImage);

        int textSize = 50;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);

        Rect bounds = new Rect();
        paint.getTextBounds(instructions, 0, instructions.length(), bounds);

        int textY=cManeuverInfo.getHeight() / 2 - bounds.height();
        int textX=cManeuverInfo.getWidth() / 2;

        cManeuverInfo.drawText(instructions, textX, textY, paint);
        textY += paint.descent() - paint.ascent();
        cManeuverInfo.drawText(lenDur, textX, textY, paint);

        ivManeuverInfo.setImageBitmap(newImage);
    }

    public void addViaPoint(GeoPoint p){
        viaPoints.add(p);
        updateItineraryMarker(null, p, viaPoints.size() - 1,
                R.string.viapoint, R.drawable.marker_via, -1, null);
    }

    public void updateUIWithItineraryMarkers() {
        updateUIWithItineraryMarkers(0);
    }

    public void updateUIWithItineraryMarkers(int iVia){
        mItineraryMarkers.closeAllInfoWindows();
        mItineraryMarkers.getItems().clear();
        //Start marker:
        if (startPoint != null){
            markerStart = updateItineraryMarker(null, startPoint, START_INDEX,
                    R.string.departure, R.drawable.marker_departure, -1, null);
        }
        //Via-points markers if any:
        for (int index=iVia; index<viaPoints.size(); index++){
            updateItineraryMarker(null, viaPoints.get(index), index,
                    R.string.viapoint, R.drawable.marker_via, -1, null);
        }
        //Destination marker if any:
        if (destinationPoint != null){
            markerDestination = updateItineraryMarker(null, destinationPoint, DEST_INDEX,
                    R.string.destination, R.drawable.marker_destination, -1, null);
        }
    }

    /** Update (or create if null) a marker in itineraryMarkers. */
    public Marker updateItineraryMarker(Marker marker, GeoPoint p, int index,
                                        int titleResId, int markerResId, int imageResId, String address) {
        Drawable icon = getResources().getDrawable(markerResId);
        String title = getResources().getString(titleResId);
        if (marker == null){
            marker = new Marker(map);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(mViaPointInfoWindow);
            marker.setDraggable(true);
            //marker.setOnMarkerDragListener(mItineraryListener);
            mItineraryMarkers.add(marker);
        }
        marker.setTitle(title);
        marker.setPosition(p);
        marker.setIcon(icon);
        if (imageResId != -1)
            marker.setImage(getResources().getDrawable(imageResId));
        marker.setRelatedObject(index);
        map.invalidate();

        return marker;
    }

    void updateUIWithTrackingMode(){
        if (mTrackingMode){
            mTrackingModeButton.setBackgroundResource(R.drawable.btn_tracking_on);
            if (myLocationOverlay.isEnabled()&& myLocationOverlay.getLocation() != null){
                map.getController().animateTo(myLocationOverlay.getLocation());
            }
            map.setMapOrientation(-mAzimuthAngleSpeed);
            mTrackingModeButton.setKeepScreenOn(true);
        } else {
            mTrackingModeButton.setBackgroundResource(R.drawable.btn_tracking_off);
            map.setMapOrientation(0.0f);
            mTrackingModeButton.setKeepScreenOn(false);
        }
    }

    boolean startLocationUpdates(){
        boolean result = false;
        for (final String provider : gps.getLocationManager().getProviders(true)) {
            gps.getLocationManager().requestLocationUpdates(provider, 3 * 1000, 2.0f, this);
            result = true;
        }
        return result;
    }

    //------------ Route and Directions  
     public void removePoint(int index) {
         if (index == START_INDEX) {
             startPoint = null;
             if (markerStart != null) {
                 markerStart.closeInfoWindow();
                 mItineraryMarkers.remove(markerStart);
                 markerStart = null;
             }
         } else if (index == DEST_INDEX) {
             destinationPoint = null;
             if (markerDestination != null) {
                 markerDestination.closeInfoWindow();
                 mItineraryMarkers.remove(markerDestination);
                 markerDestination = null;
             }
             if (viaPoints.size() > 0){
                 destinationPoint = viaPoints.get(viaPoints.size() - 1);
                 viaPoints.remove(viaPoints.size() - 1);
                 updateUIWithItineraryMarkers();
             }
         } else {
             viaPoints.remove(index);
             updateUIWithItineraryMarkers();
         }
         getRoadAsync();
     }

    private void putRoadNodes(Road road){
        updateRoadNodes(road, 0);
    }

    private void updateRoadNodes(Road road, int index){
        mRoadNodeMarkers.getItems().clear();
        Drawable icon = getResources().getDrawable(R.drawable.marker_node);
        int n = road.mNodes.size();

        MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map);
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);

        for (int i=index; i<n; i++){
            RoadNode node = road.mNodes.get(i);
            String instructions = (node.mInstructions==null ? "" : node.mInstructions);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setTitle(getString(R.string.step)+ " " + (i+1));
            nodeMarker.setSnippet(instructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(icon);
            nodeMarker.setInfoWindow(infoWindow); //use a shared infowindow.
            int iconId = iconIds.getResourceId(node.mManeuverType, R.drawable.ic_empty);
            if (iconId != R.drawable.ic_empty){
                Drawable image = getResources().getDrawable(iconId);
                nodeMarker.setImage(image);
            }
            mRoadNodeMarkers.add(nodeMarker);
        }
        iconIds.recycle();
    }

    void selectRoad(int roadIndex){
        mSelectedRoad = roadIndex;
        putRoadNodes(mRoads[roadIndex]);

        for (int i=0; i<mRoadOverlays.length; i++){
            Paint p = mRoadOverlays[i].getPaint();
            if (i == roadIndex)
                p.setColor(0x800000FF); //blue
            else
                p.setColor(0x90666666); //grey
        }
        map.invalidate();
    }

    void updateUIWithRoads(Road[] roads){
        mRoadNodeMarkers.getItems().clear();
        List<Overlay> mapOverlays = map.getOverlays();
        if (mRoadOverlays != null){
            for (int i=0; i<mRoadOverlays.length; i++)
                mapOverlays.remove(mRoadOverlays[i]);
            mRoadOverlays = null;
        }
        if (roads == null)
            return;
        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
        mRoadOverlays = new Polyline[roads.length];
        for (int i=0; i<roads.length; i++) {
            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i], this);
            mRoadOverlays[i] = roadPolyline;

            String routeDesc = roads[i].getLengthDurationText(this, -1);
            roadPolyline.setTitle(getString(R.string.route) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            roadPolyline.setRelatedObject(i);
            mapOverlays.add(1, roadPolyline);
            //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
            //to avoid covering the other overlays.
        }
        selectRoad(0);
    }

    /**
     * Async task to get the road in a separate thread.
     */
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        private final Context mContext;
        private final ProgressDialog dialog = new ProgressDialog(RouteNavigationActivity.this);

        public UpdateRoadTask(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute(){
            this.dialog.setMessage(getString(R.string.route_calculating_dialog));
            this.dialog.setCancelable(true);
            this.dialog.show();
        }

        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
            RoadManager roadManager = new OSRMRoadManager(mContext);

            //roadManager = new MapQuestRoadManager("Fmjtd%7Cluubn10zn9%2C8s%3Do5-90rnq6");
            roadManager = new MapQuestRoadManager("VvR4CnlGhrT4AwOXLFCQPzSuuDxbfYEg");
            roadManager.addRequestOption("routeType=pedestrian");

            return roadManager.getRoads(waypoints);
        }

        protected void onPostExecute(Road[] result) {
            this.dialog.dismiss();
            mRoads = result;
            updateUIWithRoads(result);
        }
    }

    public void getRoadAsync(){
        mRoads = null;
        GeoPoint roadStartPoint = null;
        if (startPoint != null){
            roadStartPoint = startPoint;
        } else if (myLocationOverlay.isEnabled() && myLocationOverlay.getLocation() != null){
            //use my current location as itinerary start point:
            roadStartPoint = myLocationOverlay.getLocation();
        }
        if (roadStartPoint == null || destinationPoint == null){
            updateUIWithRoads(mRoads);
            updateUIWithPolygon(viaPoints, "");
            return;
        }
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>(2);
        waypoints.add(roadStartPoint);
        //add intermediate via points:
        for (GeoPoint p:viaPoints){
            waypoints.add(p);
        }
        waypoints.add(destinationPoint);
        new UpdateRoadTask(this).execute(waypoints);
    }

    /**
     * A customized InfoWindow handling "itinerary" points (start, destination and via-points).
     * We inherit from MarkerInfoWindow as it already provides most of what we want.
     * And we just add support for a "remove" button.
     *
     * @author M.Kergall
     */
    public class ViaPointInfoWindow extends MarkerInfoWindow {

        int mSelectedPoint;

        public ViaPointInfoWindow(int layoutResId, MapView mapView) {
            super(layoutResId, mapView);
            Button btnDelete = (Button) (mView.findViewById(R.id.bubble_delete));
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    //Call the removePoint method on MapActivity.
                    //TODO: find a cleaner way to do that!
                    RouteNavigationActivity mapActivity = (RouteNavigationActivity) view.getContext();
                    mapActivity.removePoint(mSelectedPoint);
                    close();
                }
            });
        }

        @Override
        public void onOpen(Object item) {
            Marker eItem = (Marker) item;
            mSelectedPoint = (Integer) eItem.getRelatedObject();
            super.onOpen(item);
        }

    }

    //add or replace the polygon overlay
    public void updateUIWithPolygon(ArrayList<GeoPoint> polygon, String name){
        List<Overlay> mapOverlays = map.getOverlays();
        int location = -1;
        if (mDestinationPolygon != null)
            location = mapOverlays.indexOf(mDestinationPolygon);
        mDestinationPolygon = new Polygon(this);
        mDestinationPolygon.setFillColor(0x15FF0080);
        mDestinationPolygon.setStrokeColor(0x800000FF);
        mDestinationPolygon.setStrokeWidth(5.0f);
        mDestinationPolygon.setTitle(name);
        if (polygon != null){
            mDestinationPolygon.setPoints(polygon);
        }
        if (location != -1)
            mapOverlays.set(location, mDestinationPolygon);
        else
            mapOverlays.add(1, mDestinationPolygon); //insert just above the MapEventsOverlay.
        map.invalidate();
    }

    public class ProximityIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = LocationManager.KEY_PROXIMITY_ENTERING;
            if (intent.getBooleanExtra(key, true)){
                reachedNode += 1;
            }
        }
    }

    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent("com.javacodegeeks.android.lbs.ProximityAlert");
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        gps.getLocationManager().addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
    }

    /**
     * callback to store activity status before a restart (orientation change for instance)
     */
    @Override
    protected void onSaveInstanceState (Bundle outState){
        outState.putParcelable("location", myLocationOverlay.getLocation());
        outState.putBoolean("tracking_mode", mTrackingMode);
        outState.putParcelable("start", startPoint);
        outState.putParcelable("destination", destinationPoint);
        outState.putParcelableArrayList("viapoints", viaPoints);
        outState.putInt("reachedNode", reachedNode);
        outState.putInt("nextNode", nextNode);
        outState.putInt("nextVia", nextVia);
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
        myLocationOverlay.setAccuracy(accuracy);
        map.invalidate();

    }

    @Override protected void onResume() {
        super.onResume();
        boolean isOneProviderEnabled = startLocationUpdates();
        myLocationOverlay.setEnabled(isOneProviderEnabled);

        IntentFilter filter = new IntentFilter("com.javacodegeeks.android.lbs.ProximityAlert");
        registerReceiver(proximityIntentReceiver, filter);

        //TODO: not used currently
        //mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        //sensor listener is causing a high CPU consumption...
    }

    @Override protected void onPause() {
        super.onPause();
        gps.getLocationManager().removeUpdates(this);

        unregisterReceiver(proximityIntentReceiver);

        //TODO: mSensorManager.unregisterListener(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        mRoads = null;
    }
}

