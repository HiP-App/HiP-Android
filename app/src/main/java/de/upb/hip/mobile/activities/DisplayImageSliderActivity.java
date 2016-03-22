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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.CustomSeekBar;
import de.upb.hip.mobile.models.Exhibit;

/**
 * Activity Class for the Image-Slider View, where a picture at the top can be changed with a fading
 * effect through a slider at the bottom
 */
public class DisplayImageSliderActivity extends ActionBarActivity {
    public static final String INTENT_EXHIBIT_ID = "exhibit-id";
    public static final String INTENT_IMAGE_NAME = "imageName";

    private DBAdapter mDatabase;
    private Exhibit mExhibit;
    private ImageView mFirstImageView;
    private ImageView mNextImageView;
    private TextView mThumbSlidingText;
    private CustomSeekBar mSeekBar;
    private boolean mFontFading = true;

    private List<PictureData> mPicDataList = new ArrayList<>();


    /**
     * Called when the activity is created, get the exhibit information of the database and
     * initializes the view with that data
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image_slider);

        // read mExhibit with given exhibit-id from Database
        mDatabase = new DBAdapter(this);
        int exhibitId = getIntent().getIntExtra("exhibit-id", 0);
        mExhibit = new Exhibit(mDatabase.getDocument(exhibitId));

        setData();

        init();

        // set picture description in view
        TextView mDescriptionTextView =
                (TextView) findViewById(R.id.displayImageSliderDescriptionText);
        mDescriptionTextView.setText(mExhibit.pictureDescriptions.get(getIntent().
                getStringExtra("imageName")));

        // modify action bar with back button and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mExhibit.name);
    }

    /**
     * Implement the onSupportNavigateUp() method of the interface, closes the activity
     *
     * @return always true
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * initializes the activity, calculates and sets the dots on the slider
     */
    private void init() {
        calcDotPositions(mPicDataList);

        // set the dots
        mSeekBar = (CustomSeekBar) findViewById(R.id.seekBar);
        mSeekBar.setDots(getListOfDotPositions(mPicDataList));
        mSeekBar.setProgressDrawable(ContextCompat.getDrawable(this.getBaseContext(),
                R.drawable.customseekbar));

        // set the first picture
        mFirstImageView = (ImageView) findViewById(R.id.displayImageSliderFirstImageView);
        mFirstImageView.setImageDrawable(mPicDataList.get(0).mDrawable);

        // set the next picture
        if (mFontFading) {
            mNextImageView = (ImageView) findViewById(R.id.displayImageSliderNextImageView);
            mNextImageView.setImageDrawable(mPicDataList.get(1).mDrawable);
            mFirstImageView.bringToFront();
        }

        // set start year on the slider
        TextView seekBarFirstText =
                (TextView) findViewById(R.id.displayImageSliderSeekBarFirstText);
        seekBarFirstText.setText(String.valueOf(mPicDataList.get(0).mYear));

        // set end year on the slider
        TextView seekBarEndText = (TextView) findViewById(R.id.displayImageSliderSeekBarEndText);
        seekBarEndText.setText(String.valueOf(mPicDataList.get(mPicDataList.size() - 1).mYear));

        mThumbSlidingText = (TextView) findViewById(R.id.displayImageSliderThumbSlidingText);

        addSeekBarListener();
        openDatabase();
    }

    /**
     * Calculates the positions of the dots on the slider regarding the years of the pictures
     *
     * @param list List of PictureData Elements
     */
    private void calcDotPositions(List<PictureData> list) {
        // set progress for the first picture
        list.get(0).mDotPosition = 0;

        // set progress for other pictures
        int lSize = list.size();
        for (int i = 1; i < lSize; i++) {
            if (i + 1 < lSize) {
                int progress = 100 * (list.get(i).mYear - list.get(i - 1).mYear) /
                        (list.get(lSize - 1).mYear - list.get(0).mYear);
                list.get(i).mDotPosition = (progress + list.get(i - 1).mDotPosition);
            } else {
                // set progress for last picture
                list.get(i).mDotPosition = 100;
            }
        }
    }

    /**
     * add a Listener to the Slider to react to changes
     */
    private void addSeekBarListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressStart = 0;
            boolean forward = true;
            int nearest = 0;

            /**
             * called when the slider is moved
             *
             * @param seekBar       slider bar
             * @param progressValue new progress value
             * @param fromUser      user, who changed it
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                int startNode, nextNode;

                // decide the direction (forward or backward)
                forward = progressStart <= progressValue;

                // find closest startNode and nextNode, according to the direction
                // (forward or backward)
                int[] result = getNodes(progressValue, forward);
                startNode = result[0];
                nextNode = result[1];

                if (mFontFading) {
                    int actProgressAccordingStartNextNode = Math.abs(progressValue - mPicDataList.
                            get(startNode).mDotPosition);
                    int differenceStartNextNode = Math.abs(mPicDataList.get(nextNode).mDotPosition -
                            mPicDataList.get(startNode).mDotPosition);
                    float alpha =
                            (float) actProgressAccordingStartNextNode / differenceStartNextNode;

                    // set current image
                    mFirstImageView.setImageDrawable(mPicDataList.get(startNode).mDrawable);
                    mFirstImageView.setAlpha(1 - alpha);

                    // set next image
                    mNextImageView.setImageDrawable(mPicDataList.get(nextNode).mDrawable);
                    mNextImageView.setAlpha(alpha);

                    mFirstImageView.bringToFront();
                }

                // for showcase image: get the closest node to actual progress
                nearest = findClosestNode(result, progressValue);

                // set year over the thumb except first and last picture
                if (progressValue != 0 && progressValue != 100) {
                    int xPos = ((seekBar.getRight() - seekBar.getLeft()) / seekBar.getMax()) *
                            seekBar.getProgress();
                    mThumbSlidingText.setPadding(xPos, 0, 0, 0);
                    mThumbSlidingText.setText(String.valueOf(mPicDataList.get(nearest).mYear));
                } else {
                    // set empty text for first and last position
                    mThumbSlidingText.setText("");
                }
            }

            /**
             * Sets the start point of the movement on the slider
             * @param seekBar Slider bar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressStart = seekBar.getProgress();
            }

            /**
             * Set the image if fading is disabled
             * @param seekBar Slider bar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mFontFading) {
                    seekBar.setProgress(mPicDataList.get(nearest).mDotPosition);
                    mFirstImageView.setImageDrawable(mPicDataList.get(nearest).mDrawable);
                }
            }

            /**
             * returns the two pictures left and right of the current position on the slider
             * @param progressStop endpoint of the movement on the slider
             * @param forward indicates the direction of the movement
             * @return the two ids of the pictures left and right of the current position
             */
            private int[] getNodes(int progressStop, boolean forward) {
                for (int i = 0; i < mPicDataList.size(); i++) {
                    if (forward) {
                        if ((progressStop >= mPicDataList.get(i).mDotPosition) &&
                                (progressStop <= mPicDataList.get(i + 1).mDotPosition)) {
                            return new int[]{i, i + 1};
                        }
                    } else {
                        if (i == 0) i = 1;

                        if (progressStop <= mPicDataList.get(i).mDotPosition &&
                                (progressStop >= mPicDataList.get(i - 1).mDotPosition)) {
                            return new int[]{i, i - 1};
                        }
                    }
                }
                return new int[]{0, 0};
            }

            /**
             * find the closest node in the array to progress
             * @param array array of points on the slider
             * @param progress current progress on the slider
             * @return id of the closest point on slider
             */
            private int findClosestNode(int[] array, int progress) {
                int min = 0, max = 0, closestNode;

                // calculate left node of progress (min) and right node of progress (max)
                for (int anArray : array) {
                    if (mPicDataList.get(anArray).mDotPosition < progress) {
                        if (min == 0) {
                            min = anArray;
                        } else if (mPicDataList.get(anArray).mDotPosition > mPicDataList.get(min).
                                mDotPosition) {
                            min = anArray;
                        }
                    } else if (mPicDataList.get(anArray).mDotPosition > progress) {
                        if (max == 0) {
                            max = anArray;
                        } else if (mPicDataList.get(anArray).mDotPosition <
                                mPicDataList.get(max).mDotPosition) {
                            max = anArray;
                        }
                    } else {
                        return anArray;
                    }
                }

                // calculate which node is nearest to progress (min or max)
                if (Math.abs(progress - mPicDataList.get(min).mDotPosition) <
                        Math.abs(progress - mPicDataList.get(max).mDotPosition)) {
                    closestNode = min;
                } else {
                    closestNode = max;
                }

                return closestNode;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * open the database connection
     */
    private void openDatabase() {
        mDatabase = new DBAdapter(this);
    }

    /**
     * Set the mPicDataList Array with data of the database
     */
    private void setData() {
        int sliderID = mExhibit.sliderID;
        @SuppressWarnings("unchecked") // getProperty returns always Maps with String and Object
                ArrayList<Map<String, Object>> images = (ArrayList<Map<String, Object>>)
                mDatabase.getDocument(sliderID).getProperty(DBAdapter.KEY_SLIDER_IMAGES);

        // add all pictures to the mPicDataList
        for (int i = 0; i < images.size(); i++) {
            Map<String, Object> properties = images.get(i);
            mPicDataList.add(new PictureData(DBAdapter.getImage(sliderID,
                    (String) properties.get(DBAdapter.KEY_SLIDER_IMAGE_NAME)),
                    (int) properties.get(DBAdapter.KEY_SLIDER_IMAGE_YEAR)));
        }
    }

    /**
     * creates a list for setting dots on Slider bar
     *
     * @param list List of PictureData
     * @return list of dot points
     */
    private List<Integer> getListOfDotPositions(List<PictureData> list) {
        List<Integer> mPicDataProgressList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            mPicDataProgressList.add(list.get(i).mDotPosition);
        }
        return mPicDataProgressList;
    }

    /**
     * helper class for PictureData information
     */
    private class PictureData {
        private Drawable mDrawable;
        private int mYear;
        private int mDotPosition;

        /**
         * Constructor to store the PictureData information
         *
         * @param drawable drawable
         * @param iYear    year
         */
        public PictureData(Drawable drawable, int iYear) {
            this.mDrawable = drawable;
            mYear = iYear;
            mDotPosition = 0;
        }
    }
}
