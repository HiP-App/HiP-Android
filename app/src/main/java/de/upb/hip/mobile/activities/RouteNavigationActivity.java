package de.upb.hip.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.upb.hip.mobile.helpers.GPSTracker;
import de.upb.hip.mobile.helpers.GenericMapView;
import de.upb.hip.mobile.models.Route;

public class RouteNavigationActivity extends Activity implements MapEventsReceiver, LocationListener, SensorEventListener {
    protected static final int ROUTE_REQUEST = 1;
    protected static final int POIS_REQUEST = 2;
    static final int OSRM = 0;
    static final String userAgent = "OsmNavigator/1.0";
    public static Road[] mRoads;  //made static to pass between activities

    protected static int START_INDEX = -2, DEST_INDEX = -1;
    //final OnItineraryMarkerDragListener mItineraryListener = new OnItineraryMarkerDragListener();
    //------------ LocationListener implementation
    private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
    protected MapView map;
    protected GeoPoint startPoint, destinationPoint;
    protected LatLng mLatLngLocation;
    protected ArrayList<GeoPoint> viaPoints;
    protected FolderOverlay mItineraryMarkers;

    //for departure, destination and viapoints
    protected Marker markerStart, markerDestination;
    protected ViaPointInfoWindow mViaPointInfoWindow;
    protected DirectedLocationOverlay myLocationOverlay;
    protected boolean mTrackingMode;
    protected Polygon mDestinationPolygon; //enclosing polygon of destination location
    protected int mSelectedRoad;
    protected Polyline[] mRoadOverlays;
    protected FolderOverlay mRoadNodeMarkers;
    Button mTrackingModeButton;
    float mAzimuthAngleSpeed = 0.0f;

    int mWhichRouteProvider;
    GPSTracker gps;

    long mLastTime = 0; // milliseconds
    double mSpeed = 0.0; // km/h
    private Route route;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_navigation);

        route = (Route) getIntent().getSerializableExtra("route");

        //map = (MapView) findViewById(R.id.map);
        GenericMapView genericMap = (GenericMapView) findViewById(R.id.map);
        MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
        genericMap.setTileProvider(bitmapProvider);
        map = genericMap.getMapView();

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();

        //To use MapEventsReceiver methods, we add a MapEventsOverlay:
        MapEventsOverlay overlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(overlay);

        gps = new GPSTracker(RouteNavigationActivity.this);
        if (!gps.canGetLocation()) {
            finish();
        }
        mLatLngLocation = new LatLng(gps.getLatitude(), gps.getLongitude());

        //map prefs:
        mapController.setZoom(19);
        mapController.animateTo(new GeoPoint(mLatLngLocation.latitude, mLatLngLocation.longitude));
        //mapController.setCenter(new GeoPoint(mLatLngLocation.latitude, mLatLngLocation.longitude));

        myLocationOverlay = new DirectedLocationOverlay(this);
        map.getOverlays().add(myLocationOverlay);

        // Itinerary markers:
        mItineraryMarkers = new FolderOverlay(this);
        mItineraryMarkers.setName(getString(R.string.itinerary_markers_title));
        map.getOverlays().add(mItineraryMarkers);
        mViaPointInfoWindow = new ViaPointInfoWindow(R.layout.itinerary_bubble, map);
        //updateUIWithItineraryMarkers();

        if (savedInstanceState == null) {
            Location location = gps.getLocation();

            if (location != null) {
                //location known:
                onLocationChanged(location);
            } else {
                //no location known: hide myLocationOverlay
                myLocationOverlay.setEnabled(false);
            }
            GeoPoint start = new GeoPoint(mLatLngLocation.latitude, mLatLngLocation.longitude);
            startPoint = new GeoPoint(start);

            int size = route.waypoints.size();
            GeoPoint dest = new GeoPoint(route.waypoints.get(size - 1).latitude, route.waypoints.get(size - 1).longitude);
            destinationPoint = dest;

            viaPoints = new ArrayList<GeoPoint>();
            for (int i = 0; i < (size - 1); i++){
                GeoPoint via = new GeoPoint(route.waypoints.get(i).latitude, route.waypoints.get(i).longitude);
                addViaPoint(via);
            }
        } else {
            myLocationOverlay.setLocation((GeoPoint) savedInstanceState.getParcelable("location"));
            //TODO: restore other aspects of myLocationOverlay...
            startPoint = savedInstanceState.getParcelable("start");
            destinationPoint = savedInstanceState.getParcelable("destination");
            viaPoints = savedInstanceState.getParcelableArrayList("viapoints");
        }



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

        mTrackingMode = false;
        updateUIWithTrackingMode();
        updateUIWithItineraryMarkers();

        //context menu for clicking on the map is registered on this button.
        //(a little bit strange, but if we register it on mapView, it will catch map drag events)

        //Route and Directions
        mWhichRouteProvider = OSRM;

        mRoadNodeMarkers = new FolderOverlay(this);
        mRoadNodeMarkers.setName("Route Steps");
        map.getOverlays().add(mRoadNodeMarkers);

        updateUIWithRoads(mRoads);
        getRoadAsync();
    }

    //------------ LocationListener implementation
    @Override public void onLocationChanged(final Location pLoc) {
        long currentTime = System.currentTimeMillis();
        if (mIgnorer.shouldIgnore(pLoc.getProvider(), currentTime))
            return;
        double dT = currentTime - mLastTime;
        if (dT < 100.0){
            //Toast.makeText(this, pLoc.getProvider()+" dT="+dT, Toast.LENGTH_SHORT).show();
            return;
        }
        mLastTime = currentTime;

        GeoPoint newLocation = new GeoPoint(pLoc);
        if (!myLocationOverlay.isEnabled()){
            //we get the location for the first time:
            myLocationOverlay.setEnabled(true);
            map.getController().animateTo(newLocation);
        }

        GeoPoint prevLocation = myLocationOverlay.getLocation();
        myLocationOverlay.setLocation(newLocation);
        myLocationOverlay.setAccuracy((int)pLoc.getAccuracy());

        if (prevLocation != null && pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
			/*
			double d = prevLocation.distanceTo(newLocation);
			mSpeed = d/dT*1000.0; // m/s
			mSpeed = mSpeed * 3.6; //km/h
			*/
            mSpeed = pLoc.getSpeed() * 3.6;
            long speedInt = Math.round(mSpeed);
/*            TextView speedTxt = (TextView)findViewById(R.id.speed);
            speedTxt.setText(speedInt + " km/h");*/

            //TODO: check if speed is not too small
            if (mSpeed >= 0.1){
                //mAzimuthAngleSpeed = (float)prevLocation.bearingTo(newLocation);
                mAzimuthAngleSpeed = (float)pLoc.getBearing();
                myLocationOverlay.setBearing(mAzimuthAngleSpeed);
            }
        }

        if (mTrackingMode){
            //keep the map view centered on current location:
            map.getController().animateTo(newLocation);
            map.setMapOrientation(-mAzimuthAngleSpeed);

            getRoadAsync();
        } else {
            //just redraw the location overlay:
            map.invalidate();
        }

    }

    public void updateUIWithItineraryMarkers(){
        mItineraryMarkers.closeAllInfoWindows();
        mItineraryMarkers.getItems().clear();
        //Start marker:
        if (startPoint != null){
            markerStart = updateItineraryMarker(null, startPoint, START_INDEX,
                    R.string.departure, R.drawable.marker_departure, -1, null);
        }
        //Via-points markers if any:
        for (int index=0; index<viaPoints.size(); index++){
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

        if (address != null)
            marker.setSnippet(address);
        else
            //Start geocoding task to get the address and update the Marker description:
            new ReverseGeocodingTask().execute(marker);
        return marker;
    }

    //Async task to reverse-geocode the marker position in a separate thread:
    private class ReverseGeocodingTask extends AsyncTask<Object, Void, String> {
        Marker marker;
        protected String doInBackground(Object... params) {
            marker = (Marker)params[0];
            return getAddress(marker.getPosition());
        }
        protected void onPostExecute(String result) {
            marker.setSnippet(result);
            marker.showInfoWindow();
        }
    }

    public void addViaPoint(GeoPoint p){
        viaPoints.add(p);
        updateItineraryMarker(null, p, viaPoints.size() - 1,
                R.string.viapoint, R.drawable.marker_via, -1, null);
    }

    //------------- Geocoding and Reverse Geocoding

    /**
     * Reverse Geocoding
     */
    public String getAddress(GeoPoint p){
        GeocoderNominatim geocoder = new GeocoderNominatim(this, userAgent);
        String theAddress;
        try {
            double dLatitude = p.getLatitude();
            double dLongitude = p.getLongitude();
            List<Address> addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for (int i=0; i<=n; i++) {
                    if (i!=0)
                        sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
                theAddress = sb.toString();
            } else {
                theAddress = null;
            }
        } catch (IOException e) {
            theAddress = null;
        }
        if (theAddress != null) {
            return theAddress;
        } else {
            return "";
        }
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
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                if (mSpeed < 0.1) {
                }
                //at higher speed, we use speed vector, not phone orientation. 
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        myLocationOverlay.setAccuracy(accuracy);
        map.invalidate();

    }

    boolean startLocationUpdates(){
        boolean result = false;
        for (final String provider : gps.getLocationManager().getProviders(true)) {
            gps.getLocationManager().requestLocationUpdates(provider, 2 * 1000, 0.0f, this);
            result = true;
        }
        return result;
    }

    @Override protected void onResume() {
        super.onResume();
        boolean isOneProviderEnabled = startLocationUpdates();
        myLocationOverlay.setEnabled(isOneProviderEnabled);
        //TODO: not used currently
        //mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        //sensor listener is causing a high CPU consumption...
    }

    @Override protected void onPause() {
        super.onPause();
        gps.getLocationManager().removeUpdates(this);
        //TODO: mSensorManager.unregisterListener(this);
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
         } else {
             viaPoints.remove(index);
             updateUIWithItineraryMarkers();
         }
         getRoadAsync();
     }

    private void putRoadNodes(Road road){
        mRoadNodeMarkers.getItems().clear();
        Drawable icon = getResources().getDrawable(R.drawable.marker_node);
        int n = road.mNodes.size();
        MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map);
        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
        for (int i=0; i<n; i++){
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
        //Set route info in the text view:
        //TextView textView = (TextView)findViewById(R.id.routeInfo);
        //textView.setText(mRoads[roadIndex].getLengthDurationText(this, -1));

        for (int i=0; i<mRoadOverlays.length; i++){
            Paint p = mRoadOverlays[i].getPaint();
            if (i == roadIndex)
                p.setColor(0x800000FF); //blue
            else
                p.setColor(0x90666666); //grey
        }
        map.invalidate();
    }

    class RoadOnClickListener implements Polyline.OnClickListener{
        @Override public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos){
            int selectedRoad = (Integer)polyline.getRelatedObject();
            selectRoad(selectedRoad);
            polyline.showInfoWindow(eventPos);
            return true;
        }
    };

    void updateUIWithRoads(Road[] roads){
        mRoadNodeMarkers.getItems().clear();
        //TextView textView = (TextView)findViewById(R.id.routeInfo);
        //textView.setText("");
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
/*            if (mWhichRouteProvider == GRAPHHOPPER_BICYCLE || mWhichRouteProvider == GRAPHHOPPER_PEDESTRIAN) {
                Paint p = roadPolyline.getPaint();
                p.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
            }*/
            String routeDesc = roads[i].getLengthDurationText(this, -1);
            roadPolyline.setTitle(getString(R.string.route) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            roadPolyline.setRelatedObject(i);
            roadPolyline.setOnClickListener(new RoadOnClickListener());
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

        public UpdateRoadTask(Context context) {
            this.mContext = context;
        }

        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
            RoadManager roadManager;
            Locale locale = Locale.getDefault();
            switch (mWhichRouteProvider){
                case OSRM:
                    roadManager = new OSRMRoadManager(mContext);
                    //roadManager = new MapQuestRoadManager("Fmjtd%7Cluubn10zn9%2C8s%3Do5-90rnq6");
                    //roadManager = new MapQuestRoadManager("VvR4CnlGhrT4AwOXLFCQPzSuuDxbfYEg");
                    //roadManager.addRequestOption("routeType=pedestrian");
                    break;
                default:
                    return null;
            }
            return roadManager.getRoads(waypoints);
        }

        protected void onPostExecute(Road[] result) {
            mRoads = result;
            updateUIWithRoads(result);
            //getPOIAsync(poiTagText.getText().toString());
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
            //updateUIWithPolygon(viaPoints, "");
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
/*
    private class GeocodingTask extends AsyncTask<Object, Void, List<Address>> {
        int mIndex;
        protected List<Address> doInBackground(Object... params) {
            String locationAddress = (String)params[0];
            mIndex = (Integer)params[1];
            GeocoderNominatim geocoder = new GeocoderNominatim(getApplicationContext(), userAgent);
            geocoder.setOptions(true); //ask for enclosing polygon (if any)
            try {
                BoundingBoxE6 viewbox = map.getBoundingBox();
                List<Address> foundAdresses = geocoder.getFromLocationName(locationAddress, 1,
                        viewbox.getLatSouthE6()*1E-6, viewbox.getLonEastE6()*1E-6,
                        viewbox.getLatNorthE6()*1E-6, viewbox.getLonWestE6()*1E-6, false);
                return foundAdresses;
            } catch (Exception e) {
                return null;
            }
        }
        protected void onPostExecute(List<Address> foundAdresses) {
            if (foundAdresses == null) {
                Toast.makeText(getApplicationContext(), "Geocoding error", Toast.LENGTH_SHORT).show();
            } else if (foundAdresses.size() == 0) { //if no address found, display an error
                Toast.makeText(getApplicationContext(), "Address not found.", Toast.LENGTH_SHORT).show();
            } else {
                Address address = foundAdresses.get(0); //get first address
                String addressDisplayName = address.getExtras().getString("display_name");
                if (mIndex == START_INDEX){
                    startPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                    markerStart = updateItineraryMarker(markerStart, startPoint, START_INDEX,
                            R.string.departure, R.drawable.marker_departure, -1, addressDisplayName);
                    map.getController().setCenter(startPoint);
                } else if (mIndex == DEST_INDEX){
                    destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                    markerDestination = updateItineraryMarker(markerDestination, destinationPoint, DEST_INDEX,
                            R.string.destination, R.drawable.marker_destination, -1, addressDisplayName);
                    map.getController().setCenter(destinationPoint);
                }
                getRoadAsync();
                //get and display enclosing polygon:
                Bundle extras = address.getExtras();
                if (extras != null && extras.containsKey("polygonpoints")){
                    ArrayList<GeoPoint> polygon = extras.getParcelableArrayList("polygonpoints");
                    //Log.d("DEBUG", "polygon:"+polygon.size());
                    updateUIWithPolygon(polygon, addressDisplayName);
                } else {
                    updateUIWithPolygon(null, "");
                }
            }
        }
    }

    *//**
     * Geocoding of the departure or destination address
     *//*
    public void handleSearchButton(int index, int editResId){
        EditText locationEdit = (EditText)findViewById(editResId);
        //Hide the soft keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationEdit.getWindowToken(), 0);

        String locationAddress = locationEdit.getText().toString();

        if (locationAddress.equals("")){
            removePoint(index);
            map.invalidate();
            return;
        }

        Toast.makeText(this, "Searching:\n"+locationAddress, Toast.LENGTH_LONG).show();
        new GeocodingTask().execute(locationAddress, index);
    }
*/
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
        BoundingBoxE6 bb = null;
        if (polygon != null){
            mDestinationPolygon.setPoints(polygon);
            bb = BoundingBoxE6.fromGeoPoints(polygon);
        }
        if (location != -1)
            mapOverlays.set(location, mDestinationPolygon);
        else
            mapOverlays.add(1, mDestinationPolygon); //insert just above the MapEventsOverlay.
        //setViewOn(bb);
        map.invalidate();
    }

}

