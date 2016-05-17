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

package de.upb.hip.mobile.listeners;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;

import de.upb.hip.mobile.activities.DetailsActivity;
import de.upb.hip.mobile.activities.ExhibitDetailsActivity;
import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.ExhibitSet;

/**
 * Listener for the Recycler View in MainActivity
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private MainActivity mMainActivity;
    private ExhibitSet mExhibitSet;
    private GestureDetector mGestureDetector;


    /**
     * Constructor, starts the gesture listener
     *
     * @param mMainActivity MainActivity of the app
     */
    public RecyclerItemClickListener(MainActivity mMainActivity, ExhibitSet mExhibitSet) {
        this.mMainActivity = mMainActivity;
        this.mExhibitSet = mExhibitSet;
        mGestureDetector = new GestureDetector(mMainActivity,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
    }


    /**
     * starts the DetailsActivity on touch on an element in Recycler view
     *
     * @param view RecyclerView in MainActivity
     * @param e    MotionEvent
     * @return always false
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mGestureDetector.onTouchEvent(e)) {

            Intent intent = new Intent(this.mMainActivity, ExhibitDetailsActivity.class);

            Exhibit exhibit = null;
            for (int i = 0; i < mExhibitSet.getSize(); ++i) {
                exhibit = mExhibitSet.getExhibit(i);
                if (exhibit.getId() == childView.getId())
                    break;
            }

            if (exhibit != null) {
                Serializable pageList = (Serializable) exhibit.getPages();
                String exhibitName = exhibit.getName();
                intent.putExtra(ExhibitDetailsActivity.INTENT_EXTRA_EXHIBIT_NAME, exhibitName);
                intent.putExtra(ExhibitDetailsActivity.INTENT_EXTRA_EXHIBIT_PAGES, pageList);
                ActivityCompat.startActivity(this.mMainActivity, intent, null);
            }
        }
        return false;
    }


    /**
     * empty stub for the onTouchEvent
     *
     * @param view        RecyclerView in MainActivity
     * @param motionEvent MotionEvent
     */
    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // intentionally left blank
    }
}
