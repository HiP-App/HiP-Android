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

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.upb.hip.mobile.adapters.NavigationDrawerAdapter;

/**
 * Extends the ActionBarActivity to set up the NavigationDrawer
 * Adds click listeners
 */
public abstract class BaseActivity extends ActionBarActivity {
    private ListView mNavigationDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    /**
     * Sets up the navigation drawer and creates the click listeners for it.
     *
     * @param activity     Activity
     * @param drawerLayout DrawerLayout
     */
    public void setUpNavigationDrawer(final Activity activity, DrawerLayout drawerLayout) {

        mDrawerLayout = drawerLayout;
        mNavigationDrawerList = (ListView) findViewById(R.id.navigation_drawer);

        //get the drawer entries
        Resources res = getResources();
        String[] mDrawerDescriptions = res.getStringArray(R.array.nav_drawer_entries);

        final NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(
                this, mDrawerLayout, mNavigationDrawerList, mDrawerDescriptions);

        mNavigationDrawerList.setAdapter(navigationDrawerAdapter);
        mNavigationDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        final Class thisClass = this.getClass();

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                activity,              // host Activity
                mDrawerLayout,         // DrawerLayout object
                R.string.drawer_open,  // "open drawer" description for accessibility
                R.string.drawer_close  // "close drawer" description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mNavigationDrawerList.setItemChecked(navigationDrawerAdapter
                        .getId(thisClass), true);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Sync the toggle state after onRestoreInstanceState has occurred.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //
        mDrawerToggle.syncState();
    }

    /**
     * Pass any configuration change to the drawer toggles
     *
     * @param newConfig Configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * The action bar home/up action should open or close the drawer.
     * ActionBarDrawerToggle will take care of this.
     *
     * @param item Menu item
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The click listner for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mNavigationDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mNavigationDrawerList);
        }
    }
}
