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
import android.support.v7.app.ActionBarActivity;
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

/**
 * Activity Class for the route details view, where the details of a route are show, including a
 * map and the possibility to start the navigation.
 */
public class RouteDetailsActivity extends ActionBarActivity {

    //We need to store the markers we add to the map so that we can identify them in the listener
    private Map<String, Integer> mMarkerMap = new HashMap<>();

    /**
     * Called when the activity is created, shows the details of the route
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Route route = (Route) getIntent().getSerializableExtra("route");
        showRouteDetails(route);
    }

    /**
     * Sets the texts for the description, title, duration, distance and the images for the route
     * image and tag images.
     * Sets the Map including the waypoints.
     *
     * @param route {@link de.upb.hip.mobile.models.Route}
     */
    private void showRouteDetails(Route route) {
        TextView descriptionView = (TextView) findViewById(
                R.id.activityRouteDetailsRouteDescription);
        TextView titleView = (TextView) findViewById(R.id.activityRouteDetailsRouteTitle);
        TextView durationView = (TextView) findViewById(R.id.activityRouteDetailsRouteDuration);
        TextView distanceView = (TextView) findViewById(R.id.activityRouteDetailsRouteDistance);
        LinearLayout tagsLayout = (LinearLayout) findViewById(
                R.id.activityRouteDetailsRouteTagsLayout);
        ImageView imageView = (ImageView) findViewById(R.id.activityRouteDetailsRouteImageView);
        final MapFragment map = (MapFragment) getFragmentManager().findFragmentById(
                R.id.activityRouteDetailsMap);

        descriptionView.setText(route.description);
        titleView.setText(route.title);
        int durationInMinutes = route.duration / 60;
        durationView.setText(getResources().getQuantityString(
                R.plurals.route_activity_duration_minutes, durationInMinutes, durationInMinutes));
        distanceView.setText(String.format(getResources().getString(
                R.string.route_activity_distance_kilometer), route.distance));
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
        Attachment attachment = DBAdapter.getAttachment(route.id, route.imageName);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(attachment.getContent());
            Drawable image = new BitmapDrawable(getResources(), bitmap);
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
                mMarkerMap.put(marker.getId(), waypoint.exhibit_id);
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            int padding = 30; // offset from edges of the map in pixels
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            //We need to wait for the map to finish loading until we can apply the camera update
            map.getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.getMap().animateCamera(cameraUpdate);
                }
            });

            map.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (mMarkerMap.containsKey(marker.getId())
                            && mMarkerMap.get(marker.getId()) != -1) {
                        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                        intent.putExtra("exhibit-id", mMarkerMap.get(marker.getId()));
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

    /**
     * Implement the onSupportNavigateUp() for the ActionBar back button.
     * Closes the activity and goes back to the previous activity.
     *
     * @return always true
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
