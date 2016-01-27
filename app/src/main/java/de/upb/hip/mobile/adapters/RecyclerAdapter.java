package de.upb.hip.mobile.adapters;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.helpers.ImageManipulation;
import de.upb.hip.mobile.models.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ExhibitSet exhibitSet;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ImageView mImage;
        public TextView mName;
        public TextView mDescription;
        public TextView mDistance;
        public ViewHolder(View v) {
            super(v);
            this.mView = v;
            this.mImage = (ImageView) v.findViewById(R.id.imageViewMain);
            this.mName = (TextView) v.findViewById(R.id.txtName);
            this.mDescription = (TextView) v.findViewById(R.id.txtDescription);
            this.mDistance = (TextView) v.findViewById(R.id.txtDistance);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ExhibitSet exhibitSet, Context context) {
        this.exhibitSet = exhibitSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
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
        if(exhibit.description.length() > 32) description = exhibit.description.substring(0,32).concat("...");
        else description = exhibit.description;
        //holder.mDescription.setText(description);

        double doubleDistance = exhibit.distance;

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

        holder.mView.setId(exhibit.id);

        holder.mDistance.setText(distance);

        Drawable d = DBAdapter.getImage(exhibit.id, 64);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
        Bitmap bmp = bitmapDrawable.getBitmap();
        ImageManipulation IM = new ImageManipulation();
        holder.mImage.setImageBitmap(IM.getCroppedImage(bmp, 100));
        //holder.mImage.setImageDrawable(d);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return exhibitSet.getSize();
    }
}