package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.Console;

public class RouteActivity extends ActionBarActivity {

    private DBAdapter database;
    private RouteSet routeSet;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        database = new DBAdapter(this);

        routeSet = new RouteSet(database.getView("routes"));

        Log.i("routes", "test-log");
        Log.i("routes", new Integer(routeSet.getSize()).toString());
        for(Route r: routeSet.routes) {
            Log.i("routes", r.title);
        }

        // Recyler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_route);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //routeSet = new RouteSet();

        // specify an adapter
        mAdapter = new RouteRecyclerAdapter(this.routeSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
    }

}
