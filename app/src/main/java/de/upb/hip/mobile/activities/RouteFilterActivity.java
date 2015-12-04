package de.upb.hip.mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;

public class RouteFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_filter);
        Intent intent = getIntent();
        RouteSet routeSet = (RouteSet) intent.getSerializableExtra("routeSet");
        for(Route route: routeSet.routes){
            Log.i("routes", route.title);
        }
    }
}
