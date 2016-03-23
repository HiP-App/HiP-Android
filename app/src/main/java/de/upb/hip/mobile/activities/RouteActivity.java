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
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.adapters.RouteRecyclerAdapter;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;

/**
 * Displays an overview of all available routes in the database
 * Also provides functionalities to filter for specific tags
 */
public class RouteActivity
        extends BaseActivity implements RouteRecyclerAdapter.RouteSelectedListener {

    public static final int ACTIVITY_FILTER_RESULT = 0;
    private final HashSet<String> activeTags = new HashSet<>();
    private DBAdapter mDatabase;
    private RouteSet mRouteSet;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawerLayout;

    /**
     * Get routes from the database and set up the recyclerView.
     * Also pre-processing of the tags
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mDatabase = new DBAdapter(this);
        mRouteSet = new RouteSet(mDatabase.getView("routes"));

        // start with every tag allowed
        for (Route route : mRouteSet.getRoutes()) {
            for (RouteTag tag : route.getTags()) {
                activeTags.add(tag.getTag());
            }
        }

        // Recyler View
        mRecyclerView = (RecyclerView) findViewById(R.id.routeRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        RouteRecyclerAdapter adapter =
                new RouteRecyclerAdapter(this.mRouteSet, getApplicationContext(), activeTags);
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
        adapter.registerRouteSelectedListener(this);

        // setUp navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.routeActivityDrawerLayout);
        super.setUpNavigationDrawer(this, mDrawerLayout);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu Menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_route_menu, menu);
        return true;
    }

    /**
     * Switch for the filtering of the routes
     * @param item Menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_route_filter:
                Intent intent = new Intent(getApplicationContext(), RouteFilterActivity.class);
                intent.putExtra("RouteSet", mRouteSet);
                intent.putExtra("activeTags", activeTags);
                startActivityForResult(intent, ACTIVITY_FILTER_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method for saving the selected tags
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_FILTER_RESULT:
                if (resultCode == RouteFilterActivity.RETURN_NOSAVE) {
                    // User choosed not to save changes, don't do anything
                } else if (resultCode == RouteFilterActivity.RETURN_SAVE) {
                    HashSet<String> activeTags =
                            (HashSet<String>) data.getSerializableExtra("activeTags");
                    this.activeTags.clear();
                    this.activeTags.addAll(activeTags);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * Starts the RouteDetailsActivity for a specific route
     * @param route
     */
    @Override
    public void onRouteSelected(Route route) {
        Intent intent = new Intent(getApplicationContext(), RouteDetailsActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Getter for RouteSet
     * @return RouteSet
     */
    public RouteSet getRouteSet() {
        return mRouteSet;
    }
}
