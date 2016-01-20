package de.upb.hip.mobile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;

public class RouteDetailsActivity extends BaseActivity {

    //We need to store the markers we add to the map so that we can identify them in the listener
    private Map<String, Integer> markerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        //setUp navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setUpNavigationDrawer(this, mDrawerLayout);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Route route = (Route) getIntent().getSerializableExtra("route");
        showRouteDetails(route);
    }

    private void showRouteDetails(Route route) {
        TextView description = (TextView) findViewById(R.id.activityRouteDetailsRouteDescription);
        TextView title = (TextView) findViewById(R.id.activityRouteDetailsRouteTitle);
        TextView duration = (TextView) findViewById(R.id.activityRouteDetailsRouteDuration);
        LinearLayout tagsLayout = (LinearLayout) findViewById(R.id.activityRouteDetailsRouteTagsLayout);
        ImageView imageView = (ImageView) findViewById(R.id.activityRouteDetailsRouteImageView);
        final MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.activityRouteDetailsMap);


        description.setText(route.description);
        title.setText(route.title);
        int durationInMinutes = route.duration / 60;
        duration.setText(getResources().getQuantityString(R.plurals.route_activity_duration_minutes, durationInMinutes, durationInMinutes));

        //Add tags
        if (route.tags != null) {
            tagsLayout.removeAllViews();
            for (RouteTag tag : route.tags) {
                ImageView tagImageView = new ImageView(getApplicationContext());
                tagImageView.setImageDrawable(tag.getImage(route.id, getApplicationContext()));
                tagsLayout.addView(tagImageView);
            }
        }


        //Add image
        Attachment att = DBAdapter.getAttachment(route.id, route.imageName);
        try {
            Bitmap b = BitmapFactory.decodeStream(att.getContent());
            Drawable image = new BitmapDrawable(getResources(), b);
            imageView.setImageDrawable(image);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }

        DBAdapter db = new DBAdapter(this);
        if (route.waypoints != null && route.waypoints.size() > 0) {
            List<LatLng> waypointList = new LinkedList<>();
            //To move the camera such that it includes all the waypoints
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //Add all the waypoints to the map
            for (Waypoint waypoint : route.waypoints) {
                LatLng latLng = new LatLng(waypoint.latitude, waypoint.longitude);
                waypointList.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if (waypoint.exhibit_id != -1) {
                    Exhibit exhibit = waypoint.getExhibit(db);
                    markerOptions.title(exhibit.name);
                    markerOptions.snippet(exhibit.description);
                }
                Marker marker = map.getMap().addMarker(markerOptions);
                markerMap.put(marker.getId(), waypoint.exhibit_id);
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            int padding = 30; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            //We need to wait for the map to finish loading until we can apply the camera update
            map.getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.getMap().animateCamera(cu);
                }
            });

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
            });

            //Add a line representing the route to the map
            PolylineOptions routePolyLine = new PolylineOptions().addAll(waypointList);
            map.getMap().addPolyline(routePolyLine);
        }
        map.getMap().setMyLocationEnabled(true);
    }

}
