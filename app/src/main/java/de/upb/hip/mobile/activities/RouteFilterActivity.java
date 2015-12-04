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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;

public class RouteFilterActivity extends Activity {

    public static final int RETURN_SAVE = 0;
    public static final int RETURN_NOSAVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_filter);
        Intent intent = getIntent();
        RouteSet routeSet = (RouteSet) intent.getSerializableExtra("routeSet");
        HashSet<String> activeTags = (HashSet<String>) intent.getSerializableExtra("activeTags");
        //There will be duplicates in the route set so we have to remove them
        HashMap<String, RouteTagHolder> uniqueTags = new HashMap<>();
        for (Route route : routeSet.routes) {
            for (RouteTag tag : route.tags) {
                if (!uniqueTags.containsKey(tag.getTag())) {
                    //Call getImage so that the route tag caches its image
                    tag.getImage(route.id, getApplicationContext());
                    uniqueTags.put(tag.getTag(), new RouteTagHolder(activeTags.contains(tag.getTag()), tag));
                }
            }
        }
        ListView listView = (ListView) findViewById(R.id.routeTagList);
        final ArrayAdapter<RouteTagHolder> adapter = new RouteTagArrayAdapter(getApplicationContext(), new ArrayList<RouteTagHolder>(uniqueTags.values()));
        listView.setAdapter(adapter);


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
                    if (tagHolder.isSelected) {
                        activeTags.add(tagHolder.getRouteTag().getTag());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("activeTags", activeTags);
                setResult(RETURN_SAVE, intent);
                finish();
            }
        });
    }


    private static class RouteTagViewHolder {
        private CheckBox checkBox;
        private TextView textView;
        private ImageView imageView;
        private String tagId;

        public RouteTagViewHolder(CheckBox checkBox, ImageView imageView, String tagId) {
            this.checkBox = checkBox;
            this.imageView = imageView;
            this.tagId = tagId;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public String getTagId() {
            return tagId;
        }
    }

    private static class RouteTagHolder {
        private boolean isSelected;
        private RouteTag routeTag;

        public RouteTagHolder(boolean isSelected, RouteTag routeTag) {
            this.isSelected = isSelected;
            this.routeTag = routeTag;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public RouteTag getRouteTag() {
            return routeTag;
        }
    }

    private static class RouteTagArrayAdapter extends ArrayAdapter<RouteTagHolder> {

        private LayoutInflater inflater;

        public RouteTagArrayAdapter(Context context, List<RouteTagHolder> tags) {
            super(context, R.layout.activity_route_filter_row, tags);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RouteTagHolder tagHolder = this.getItem(position);
            RouteTag tag = tagHolder.getRouteTag();

            CheckBox checkBox;
            ImageView imageView;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_route_filter_row, null);

                checkBox = (CheckBox) convertView.findViewById(R.id.activityRouteFilterRowCheckBox);
                imageView = (ImageView) convertView.findViewById(R.id.activityRouteFilterRowImageView);

                convertView.setTag(new RouteTagViewHolder(checkBox, imageView, tag.getTag()));

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
