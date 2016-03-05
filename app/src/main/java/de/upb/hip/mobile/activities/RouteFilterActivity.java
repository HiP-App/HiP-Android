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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;

/**
 * Shows the activity for the filter for routes
 */
public class RouteFilterActivity extends ActionBarActivity {

    public static final int RETURN_SAVE = 1;
    public static final int RETURN_NOSAVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_filter);
        Intent intent = getIntent();

        // Get routes
        RouteSet routeSet = (RouteSet) intent.getSerializableExtra("routeSet");
        @SuppressWarnings("unchecked") // activeTags will be always a HashSet
                HashSet<String> activeTags = (HashSet<String>) intent.getSerializableExtra("activeTags");

        //There will be duplicates in the route set so we have to remove them
        HashMap<String, RouteTagHolder> uniqueTags = new HashMap<>();
        for (Route route : routeSet.routes) {
            for (RouteTag tag : route.tags) {
                if (!uniqueTags.containsKey(tag.getTag())) {
                    //Call getImage so that the route tag caches its image
                    tag.getImage(route.id, getApplicationContext());
                    uniqueTags.put(tag.getTag(),
                            new RouteTagHolder(activeTags.contains(tag.getTag()), tag));
                }
            }
        }

        // Add tags
        ListView listView = (ListView) findViewById(R.id.routeTagList);
        final ArrayAdapter<RouteTagHolder> adapter =
                new RouteTagArrayAdapter(getApplicationContext(),
                        new ArrayList<>(uniqueTags.values()));
        listView.setAdapter(adapter);

        // Add buttons
        Button closeWithoutSave = (Button) findViewById(R.id.routeTagCloseWithoutSave);
        Button closeWithSave = (Button) findViewById(R.id.routeTagCloseWithSave);

        closeWithoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RETURN_NOSAVE);
                finish();
            }
        });

        closeWithSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashSet<String> activeTags = new HashSet<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    RouteTagHolder tagHolder = adapter.getItem(i);
                    if (tagHolder.mIsSelected) {
                        activeTags.add(tagHolder.getRouteTag().getTag());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("activeTags", activeTags);
                setResult(RETURN_SAVE, intent);
                finish();
            }
        });

        // Set back button on actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Helper class for one RouteTagView
     */
    private static class RouteTagViewHolder {
        private CheckBox mCheckBox;
        private ImageView mImageView;

        /**
         * Constructor for RouteTagViewHolder
         *
         * @param checkBox  The checkbox for this tag
         * @param imageView The image for this tag
         */
        public RouteTagViewHolder(CheckBox checkBox, ImageView imageView) {
            this.mCheckBox = checkBox;
            this.mImageView = imageView;
        }

        public CheckBox getCheckBox() {
            return mCheckBox;
        }

        public ImageView getImageView() {
            return mImageView;
        }

    }

    /**
     * Helper class for a route tag
     */
    private static class RouteTagHolder {
        private boolean mIsSelected;
        private RouteTag mRouteTag;

        /**
         * Constructor for a RouteTagHolder
         *
         * @param isSelected if the tag is currently selected
         * @param routeTag   the tag
         */
        public RouteTagHolder(boolean isSelected, RouteTag routeTag) {
            this.mIsSelected = isSelected;
            this.mRouteTag = routeTag;
        }

        public boolean isSelected() {
            return mIsSelected;
        }

        public void setSelected(boolean isSelected) {
            this.mIsSelected = isSelected;
        }

        public RouteTag getRouteTag() {
            return mRouteTag;
        }
    }

    /**
     * Helper class for a route tag array
     */
    private static class RouteTagArrayAdapter extends ArrayAdapter<RouteTagHolder> {

        private LayoutInflater mInflater;

        /**
         * Constructor for RouteTagArrayAdapter
         *
         * @param context current context
         * @param tags    list of tags as RouteTagHolder
         */
        public RouteTagArrayAdapter(Context context, List<RouteTagHolder> tags) {
            super(context, R.layout.activity_route_filter_row, tags);
            mInflater = LayoutInflater.from(context);
        }

        @SuppressLint("InflateParams") /* there are no view parameters on the root element,
        passing null to the inflater is valid */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RouteTagHolder tagHolder = this.getItem(position);
            RouteTag tag = tagHolder.getRouteTag();

            CheckBox checkBox;
            ImageView imageView;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_route_filter_row, null);

                checkBox = (CheckBox) convertView.findViewById(R.id.activityRouteFilterRowCheckBox);
                imageView = (ImageView) convertView.findViewById(
                        R.id.activityRouteFilterRowImageView);

                convertView.setTag(new RouteTagViewHolder(checkBox, imageView));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        RouteTagHolder tagHolder = (RouteTagHolder) v.getTag();
                        tagHolder.setSelected(cb.isChecked());
                    }
                });
            } else {
                RouteTagViewHolder viewHolder = (RouteTagViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                imageView = viewHolder.getImageView();
            }

            checkBox.setTag(tagHolder);
            checkBox.setText(tag.getName());
            checkBox.setChecked(tagHolder.isSelected());
            imageView.setImageDrawable(tag.getImage());

            return convertView;
        }
    }
}
