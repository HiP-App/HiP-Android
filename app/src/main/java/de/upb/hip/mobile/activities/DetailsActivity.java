package de.upb.hip.mobile.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.db.ExhibitDeserializer;
import de.upb.hip.mobile.models.exhibit.Exhibit;

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

/**
 * The Details Activity is opened upon clicking onto an exhibit in the Main Activity and
 * Main Activity only. It shows a detailed description and image of an exhibit.
 */
public class DetailsActivity extends ActionBarActivity {

    public static final String INTENT_EXHIBIT_ID = "exhibit-id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_IMAGE = "detail:image";
    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_TITLE = "detail:title";
    private DBAdapter mDatabase;
    private ImageView mImageView;
    private TextView mTextView;

    private int mExhibitId;
    private boolean mIsSlider;
    private String mImageName;

    private ActionBar mActionBar;


    /**
     * Set up the Details. Load the correct image and text.
     * Add a transitionListener, if necessary
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        openDatabase();

        mImageView = (ImageView) findViewById(R.id.detailsImageView);
        //TODO: Get rid of this hardcoded constant
        mImageName = "image.jpg";
        mImageView.setImageDrawable(mDatabase.getImage(1, mImageName));
        mTextView = (TextView) findViewById(R.id.detailsName);

        if (Build.VERSION.SDK_INT >= 21) {
            /**
             * Set the name of the view's which will be transitioned to,
             * using the static values above.
             * This could be done in the layout XML,
             * but exposing it via static variables allows easy
             * querying from other Activities
             */
            ViewCompat.setTransitionName(mImageView, VIEW_NAME_IMAGE);
            ViewCompat.setTransitionName(mTextView, VIEW_NAME_TITLE);

            addTransitionListener();
        }

        mExhibitId = getIntent().getIntExtra(INTENT_EXHIBIT_ID, 0);

        //TODO: Remove hardcoded string constant
        Drawable d = mDatabase.getImage(mExhibitId, "image.jpg");
        mImageView.setImageDrawable(d);

        Document document = mDatabase.getDocument(mExhibitId);
        Exhibit exhibit = ExhibitDeserializer.deserializeExhibit(document);

        mTextView.setText(exhibit.getName());
        //TODO: Remove this workaround code as exhibits can't have sliders anymore
        mIsSlider = false;

        TextView txtDescription = (TextView) findViewById(R.id.detailsDescription);
        txtDescription.setText(exhibit.getDescription());

        //TODO: is this needed?
//        // Set ActionBar
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            toolbar.setTitle(exhibit.name);
//            setSupportActionBar(toolbar);
//        }

        // Set back button on actionbar
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(exhibit.getName());
        }

        //TODO: is this needed?
        //ImageButton fab = (ImageButton) findViewById(R.id.fab);
//        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                // Or read size directly from the view's width/height
//                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
//                outline.setOval(0, 0, size, size);
//            }
//        };
//        fab.setOutlineProvider(viewOutlineProvider);

    }

    /**
     * Try and add a {@link android.transition.Transition.TransitionListener}
     * to the entering shared element
     * {@link android.transition.Transition}.
     * We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the entered transition
     */
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    Drawable d = mDatabase.getImage(mExhibitId, "image.jpg");
                    mImageView.setImageDrawable(d);

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach this then we have not added a listener
        return false;
    }

    /**
     * Closes the DetailsActivity
     */
    public void onClick_back(View view) {
        this.finish();
    }

    /**
     * Opens a bigger version of the image when pressed
     *
     * @param view
     */
    public void onClick_detailsImageView(View view) {
        Intent intent = new Intent(this, DisplaySingleImageActivity.class);
        intent.putExtra(DisplaySingleImageActivity.INTENT_EXHIBIT_ID, mExhibitId);
        intent.putExtra(DisplaySingleImageActivity.INTENT_IMAGE_NAME, mImageName);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openDatabase() {
        mDatabase = new DBAdapter(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    //TODO: is this needed?
    /*  Check later if this is needed
    public void onStop() {
        mImageView.destroyDrawingCache();
        super.onStop();
    }
    */

}
