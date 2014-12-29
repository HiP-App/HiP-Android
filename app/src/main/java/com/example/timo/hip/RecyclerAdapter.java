package com.example.timo.hip;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ExhibitSet exhibitSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mDescription;
        public TextView mDistance;
        public ViewHolder(View v) {
            super(v);
            this.mName = (TextView) v.findViewById(R.id.txtName);
            this.mDescription = (TextView) v.findViewById(R.id.txtDescription);
            this.mDistance = (TextView) v.findViewById(R.id.txtDistance);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ExhibitSet exhibitSet) {
        this.exhibitSet = exhibitSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Exhibit exhibit = this.exhibitSet.getExhibit(position);

        holder.mName.setText(exhibit.name);
        String description;
        if(exhibit.description.length() > 64) description = exhibit.description.substring(0,64).concat("...");
        else description = exhibit.description;
        holder.mDescription.setText(description);

        // TODO: Actual Position
        LatLng paderborn = new LatLng(51.7276064, 8.7684325);
        double doubleDistance = SphericalUtil.computeDistanceBetween(paderborn, exhibit.latlng);

        int intDistance;
        String distance;
        if(doubleDistance > 1000) {
            if(doubleDistance < 10000) {
                intDistance = (int)(doubleDistance/100);
                distance = (double)(intDistance)/10 + "km";
            } else {
                distance = (int)doubleDistance/1000 + "km";
            }
        } else {
            distance = (int)doubleDistance + "m";
        }

        holder.mDistance.setText(distance);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return exhibitSet.getSize();
    }
}