package de.upb.hip.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;

public class RouteFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_filter);
        Intent intent = getIntent();
        RouteSet routeSet = (RouteSet) intent.getSerializableExtra("routeSet");
        //There will be duplicates in the route set so we have to remove them
        HashMap<String, RouteTag> uniqueTags = new HashMap<>();
        for (Route route : routeSet.routes) {
            for (RouteTag tag : route.tags) {
                if (!uniqueTags.containsKey(tag.getTag())) {
                    //Call getImage so that the route tag caches its image
                    tag.getImage(route.id, getApplicationContext());
                    uniqueTags.put(tag.getTag(), tag);
                }
            }
        }
        ListView listView = (ListView) findViewById(R.id.routeTagList);
        ArrayAdapter<RouteTag> adapter = new RouteTagArrayAdapter(getApplicationContext(), new ArrayList<RouteTag>(uniqueTags.values()));
        listView.setAdapter(adapter);
    }

    private static class RouteTagViewHolder {
        private CheckBox checkBox;
        private TextView textView;
        private ImageView imageView;
        private String tagId;

        public RouteTagViewHolder(CheckBox checkBox, TextView textView, ImageView imageView, String tagId) {
            this.checkBox = checkBox;
            this.textView = textView;
            this.imageView = imageView;
            this.tagId = tagId;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public String getTagId() {
            return tagId;
        }
    }

    private static class RouteTagArrayAdapter extends ArrayAdapter<RouteTag> {

        private LayoutInflater inflater;

        public RouteTagArrayAdapter(Context context, List<RouteTag> tags) {
            super(context, R.layout.activity_route_filter_row, tags);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RouteTag tag = this.getItem(position);

            CheckBox checkBox;
            TextView textView;
            ImageView imageView;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_route_filter_row, null);

                checkBox = (CheckBox) convertView.findViewById(R.id.activityRouteFilterRowCheckBox);
                imageView = (ImageView) convertView.findViewById(R.id.activityRouteFilterRowImageView);

                convertView.setTag(new RouteTagViewHolder(checkBox, null, imageView, tag.getTag()));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        RouteTag tag = (RouteTag) v.getTag();
                        Log.i("routes", tag.getTag());
                    }
                });
            }
            else {
                RouteTagViewHolder viewHolder = (RouteTagViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
                imageView = viewHolder.getImageView();
            }
            checkBox.setTag(tag);

            checkBox.setText(tag.getName());
            imageView.setImageDrawable(tag.getImage());

            return convertView;
        }
    }
}
