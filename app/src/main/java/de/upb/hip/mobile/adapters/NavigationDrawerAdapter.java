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

package de.upb.hip.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.activities.RouteActivity;


/**
 * An adapter for the items shown in the navigation drawer
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    /** Hardcoded menu items */
    private Integer[] mIcones =
            {
                    R.drawable.ic_home_black_48dp,
                    R.drawable.ic_directions_black_48dp
            };

    private Class[] mActivities =
            {
                    MainActivity.class,
                    RouteActivity.class
            };

    private String[] mDescriptions;

    private Context mContext;
    private LayoutInflater mInflater;
    private DrawerLayout mDrawerLayout;
    private ListView mNavigationDrawerList;

    public NavigationDrawerAdapter(Context c, DrawerLayout mDrawerLayout, ListView mNavigationDrawerList, String[] mDescriptions) {
        this.mContext = c;
        this.mInflater = LayoutInflater.from(c);
        this.mDrawerLayout = mDrawerLayout;
        this.mNavigationDrawerList = mNavigationDrawerList;
        this.mDescriptions = new String[mDescriptions.length];
        System.arraycopy(mDescriptions, 0, this.mDescriptions, 0, mDescriptions.length);
    }

    @Override
    public int getCount() {
        return mActivities.length;
    }


    @Override
    public Object getItem(int position) {
        return mActivities[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the ID for a class
     * @param activityClass
     * @return The id of the class for -1 if it wasn't found
     */
    public int getId(Class activityClass) {
        for (int id = 0; id < mActivities.length; id++) {
            if (mActivities[id].equals(activityClass)) {
                return id;
            }
        }
        return -1;
    }

    /**
     * Sets up an entry in the navigation drawer incl. a listener for it
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.navigation_drawer_row_item, null);
        TextView textView = (TextView) convertView.findViewById(R.id.navigationDrawerRowItemText);
        textView.setText(mDescriptions[position]);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.navigationDrawerRowItemIcon);
        imageView.setImageResource(mIcones[position]);

        convertView.setId(position);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, mActivities[v.getId()]);
                mDrawerLayout.closeDrawer(mNavigationDrawerList);

                mContext.startActivity(intent);

            }
        });

        return convertView;
    }
}
