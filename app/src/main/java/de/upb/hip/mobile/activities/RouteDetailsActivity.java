package de.upb.hip.mobile.activities;

import android.os.Bundle;
import android.app.Activity;

import de.upb.hip.mobile.activities.R;

public class RouteDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
