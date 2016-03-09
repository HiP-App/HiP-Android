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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.helpers.ImageManipulation;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.ExhibitSet;

/**
 * Adapter for the RecyclerView
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ExhibitSet mExhibitSet;

    /**
     * Constructor for the RecyclerAdapter
     *
     * @param exhibitSet set of Exhibits to be shown
     */
    public RecyclerAdapter(ExhibitSet exhibitSet) {
        this.mExhibitSet = exhibitSet;
    }

    /**
     * Creates new views (invoked by the layout manager)
     *
     * @param parent   parent ViewGroup
     * @param viewType indicates the view type
     * @return the ViewHolder
     */
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     *
     * @param holder   the current ViewHolder
     * @param position the position in the ViewHolder
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get Exhibit from mExhibitSet at position
        Exhibit exhibit = this.mExhibitSet.getExhibit(position);

        // update the holder with new data
        holder.mName.setText(exhibit.name);
        double doubleDistance = exhibit.distance;

        int intDistance;
        String distance;
        if (doubleDistance > 1000) {
            if (doubleDistance < 10000) {
                intDistance = (int) (doubleDistance / 100);
                distance = (double) (intDistance) / 10 + "km";
            } else {
                distance = (int) doubleDistance / 1000 + "km";
            }
        } else {
            distance = (int) doubleDistance + "m";
        }

        holder.mView.setId(exhibit.id);

        holder.mDistance.setText(distance);

        Drawable d = DBAdapter.getImage(exhibit.id, "image.jpg", 64);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
        Bitmap bmp = bitmapDrawable.getBitmap();
        holder.mImage.setImageBitmap(ImageManipulation.getCroppedImage(bmp, 100));
    }

    /**
     * Calculates the size of mExhibitSet (invoked by the layout manager)
     *
     * @return size of mExhibitSet
     */
    @Override
    public int getItemCount() {
        return mExhibitSet.getSize();
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
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
            this.mName = (TextView) v.findViewById(R.id.detailsName);
            this.mDescription = (TextView) v.findViewById(R.id.detailsDescription);
            this.mDistance = (TextView) v.findViewById(R.id.txtDistance);
        }
    }
}