package com.example.timo.hip;

import android.content.Context;

import android.graphics.drawable.Drawable;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder> {
    private RouteSet routeSet;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ImageView mImage;
        public TextView mTitle;
        public TextView mDescription;
        public TextView mDuration;
        public ViewHolder(View v) {
            super(v);
            this.mView = v;
            this.mImage = (ImageView) v.findViewById(R.id.image_route);
            this.mTitle = (TextView) v.findViewById(R.id.text_title);
            this.mDescription = (TextView) v.findViewById(R.id.text_description);
            this.mDuration = (TextView) v.findViewById(R.id.text_duration);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RouteRecyclerAdapter(RouteSet routeSet, Context context) {
        this.routeSet = routeSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RouteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_routes, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Route route = this.routeSet.getRoute(position);

        holder.mTitle.setText(route.title);
        String description;
        if(route.description.length() > 32) description = route.description.substring(0,32).concat("...");
        else description = route.description;
        holder.mDescription.setText(description);
        holder.mDuration.setText((route.duration/60) + " Minuten");

        holder.mView.setId(route.id);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return routeSet.getSize();
    }
}