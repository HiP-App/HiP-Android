package de.upb.hip.mobile.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;

public class RouteDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
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
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.activityRouteDetailsMap);


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
        if (route.waypoints != null) {
            List<LatLng> waypointList = new LinkedList<>();
            for (Waypoint waypoint : route.waypoints) {
                LatLng latLng = new LatLng(waypoint.latitude, waypoint.longitude);
                waypointList.add(latLng);
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                if(waypoint.exhibit_id != -1){
                    marker.title(waypoint.getExhibit(db).name);
                }
                map.getMap().addMarker(marker);
            }
            PolylineOptions routePolyLine = new PolylineOptions().addAll(waypointList);
            map.getMap().addPolyline(routePolyLine);
        }

    }

}
