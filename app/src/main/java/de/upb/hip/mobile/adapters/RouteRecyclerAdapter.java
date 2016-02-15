package de.upb.hip.mobile.adapters;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.models.*;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder> implements Filterable {
    private RouteSet routeSet;
    private Context context;

    private final Set<String> activeTags;

    private List<RouteSelectedListener> routeSelectedListeners = new LinkedList<>();


    // Provide a suitable constructor (depends on the kind of dataset)
    public RouteRecyclerAdapter(RouteSet routeSet, Context context, Set<String> activeTags) {
        this.routeSet = routeSet;
        this.context = context;
        this.activeTags = activeTags;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ImageView mImage;
        public TextView mTitle;
        public TextView mDescription;
        public TextView mDuration;
        public LinearLayout mTagsLayout;

        public ViewHolder(View v) {
            super(v);
            this.mView = v;
            this.mImage = (ImageView) v.findViewById(R.id.image_route);
            this.mTitle = (TextView) v.findViewById(R.id.text_title);
            this.mDescription = (TextView) v.findViewById(R.id.text_description);
            this.mDuration = (TextView) v.findViewById(R.id.text_duration);
            this.mTagsLayout = (LinearLayout) v.findViewById(R.id.tags_layout);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyRouteSelectedListeners(routeSet.getRouteById(v.getId()));
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RouteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_route_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Route route = getFilteredRoutes().getRouteByPosition(position);
        holder.mTitle.setText(route.title);
        String description;
        description = route.description;
        holder.mDescription.setText(description);
        int durationInMinutes = route.duration / 60;
        holder.mDuration.setText(context.getResources().getQuantityString(R.plurals.route_activity_duration_minutes, durationInMinutes, durationInMinutes));
        //Check if there are actually tags for this route
        if (route.tags != null) {
            holder.mTagsLayout.removeAllViews();
            for (RouteTag tag : route.tags) {
                ImageView tagImageView = new ImageView(context);
                tagImageView.setImageDrawable(tag.getImage(route.id, context));
                holder.mTagsLayout.addView(tagImageView);
            }

        }
        Attachment att = DBAdapter.getAttachment(route.id, route.imageName);
        try {
            Bitmap b = BitmapFactory.decodeStream(att.getContent());
            Drawable image = new BitmapDrawable(context.getResources(), b);
            holder.mImage.setImageDrawable(image);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }
        holder.mView.setId(route.id);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getFilteredRoutes().getSize();
    }

    public RouteSet getFilteredRoutes() {
        List<Route> result = new LinkedList<>();

        routeLoop:
        for (Route route : this.routeSet.routes) {
            for (RouteTag tag : route.tags) {
                if (activeTags.contains(tag.getTag())) {
                    result.add(route);
                    continue routeLoop;
                }
            }
        }
        RouteSet set = new RouteSet();
        set.setRoutes(result);
        return set;
    }

    public void registerRouteSelectedListener(RouteSelectedListener listener){
        routeSelectedListeners.add(listener);
    }

    public void removeRouteSelectedListener(RouteSelectedListener listener){
        routeSelectedListeners.remove(listener);
    }

    private void notifyRouteSelectedListeners(Route route){
        for(RouteSelectedListener listener: routeSelectedListeners){
            listener.onRouteSelected(route);
        }
    }

    public interface RouteSelectedListener{
        public void onRouteSelected(Route route);
    }
}