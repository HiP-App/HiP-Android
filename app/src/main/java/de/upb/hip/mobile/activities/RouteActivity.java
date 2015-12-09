package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.Console;
import java.util.HashSet;
import java.util.Set;

public class RouteActivity extends ActionBarActivity implements RouteRecyclerAdapter.RouteSelectedListener{

    private DBAdapter database;
    private RouteSet routeSet;
    //A set of the tags that should currently be displayed
    private final HashSet<String> activeTags = new HashSet<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final int ACTIVITY_FILTER_RESULT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);


        database = new DBAdapter(this);

        routeSet = new RouteSet(database.getView("routes"));

        //We start with every tag allowed
        for (Route route : routeSet.routes) {
            for (RouteTag tag : route.tags) {
                activeTags.add(tag.getTag());
            }
        }

        // Recyler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_route);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //routeSet = new RouteSet();

        // specify an adapter
        RouteRecyclerAdapter adapter = new RouteRecyclerAdapter(this.routeSet, getApplicationContext(), activeTags);
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
        adapter.registerRouteSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_route_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_route_filter:
                Intent intent = new Intent(getApplicationContext(), RouteFilterActivity.class);
                intent.putExtra("routeSet", routeSet);
                intent.putExtra("activeTags", activeTags);
                startActivityForResult(intent, ACTIVITY_FILTER_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_FILTER_RESULT:
                if (resultCode == RouteFilterActivity.RETURN_NOSAVE) {
                    // User choosed not to save changes, don't do anything
                } else if (resultCode == RouteFilterActivity.RETURN_SAVE) {
                    HashSet<String> activeTags = (HashSet<String>) data.getSerializableExtra("activeTags");
                    this.activeTags.clear();
                    this.activeTags.addAll(activeTags);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRouteSelected(Route route) {
        Log.i("routes", route.title);
    }
}
