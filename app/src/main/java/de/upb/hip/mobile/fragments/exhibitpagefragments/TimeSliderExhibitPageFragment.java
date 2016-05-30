/*
 * Copyright (c) 2016 History in Paderborn App - Universität Paderborn
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

package de.upb.hip.mobile.fragments.exhibitpagefragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.helpers.CustomSeekBar;
import de.upb.hip.mobile.models.exhibit.Page;
import de.upb.hip.mobile.models.exhibit.TimeSliderPage;

/**
 * A {@link ExhibitPageFragment} subclass for the {@link TimeSliderPage}.
 */
public class TimeSliderExhibitPageFragment extends ExhibitPageFragment {

    public static final String INSTANCE_STATE_PAGE = "insanceStatePage";

    private TimeSliderPage page;

    private ImageView mFirstImageView;
    private ImageView mNextImageView;
    private TextView mThumbSlidingText;
    private TextView mImageDescription;
    private CustomSeekBar mSeekBar;

    private View view;

    private List<PictureData> mPicDataList = new ArrayList<>();

    public TimeSliderExhibitPageFragment() {

    }

    @Override
    public void setPage(Page page) {
        this.page = (TimeSliderPage) page;
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        SimpleBottomSheetFragment bottomSheetFragment = new SimpleBottomSheetFragment();
        bottomSheetFragment.setTitle(page.getTitle());
        bottomSheetFragment.setDescription(page.getText());
        return new BottomSheetConfig.Builder().displayBottomSheet(true).bottomSheetFragment(bottomSheetFragment).getBottomSheetConfig();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_exhibitpage_timeslider, container, false);


        if (savedInstanceState != null && savedInstanceState.getSerializable(INSTANCE_STATE_PAGE) != null) {
            page = (TimeSliderPage) savedInstanceState.getSerializable(INSTANCE_STATE_PAGE);
        }
        setData();
        init();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(INSTANCE_STATE_PAGE, page);
    }


    /**
     * initializes the activity, calculates and sets the dots on the slider
     */
    private void init() {
        calcDotPositions(mPicDataList);

        // set the dots
        mSeekBar = (CustomSeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setDots(getListOfDotPositions(mPicDataList));
        mSeekBar.setProgressDrawable(view.getResources().getDrawable(R.drawable.customseekbar));

        // set the first picture
        mFirstImageView = (ImageView) view.findViewById(R.id.displayImageSliderFirstImageView);
        mFirstImageView.setImageDrawable(mPicDataList.get(0).mDrawable);

        // set the next picture
        mNextImageView = (ImageView) view.findViewById(R.id.displayImageSliderNextImageView);
        mNextImageView.setImageDrawable(mPicDataList.get(1).mDrawable);
        mFirstImageView.bringToFront();

        // set start year on the slider
        TextView seekBarFirstText =
                (TextView) view.findViewById(R.id.displayImageSliderSeekBarFirstText);
        seekBarFirstText.setText(String.valueOf(mPicDataList.get(0).mYear) + " " + getString(R.string.after_christ));

        // set end year on the slider
        TextView seekBarEndText = (TextView) view.findViewById(R.id.displayImageSliderSeekBarEndText);
        seekBarEndText.setText(String.valueOf(mPicDataList.get(mPicDataList.size() - 1).mYear) + " " + getString(R.string.after_christ));

        mThumbSlidingText = (TextView) view.findViewById(R.id.displayImageSliderThumbSlidingText);

        mImageDescription = (TextView) view.findViewById(R.id.displayImageSliderDescriptionText);

        addSeekBarListener();
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
                int range = (mPicDataList.get(mPicDataList.size() - 1).mYear) - mPicDataList.get(0).mYear;

                // decide the direction (forward or backward)
                forward = progressStart <= progressValue;

                // find closest startNode and nextNode, according to the direction
                // (forward or backward)
                int[] result = getNodes(progressValue, forward);
                startNode = result[0];
                nextNode = result[1];

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

                // for showcase image: get the closest node to actual progress
                nearest = findClosestNode(result, progressValue);

                mImageDescription.setText("String: " + page.getImages().get(nearest).getDescription());
                Log.i("slider", page.getImages().get(nearest).getDescription());

                // set year over the thumb except first and last picture
                if (progressValue != 0 && progressValue != 100) {
                    int xPos = ((seekBar.getRight() - seekBar.getLeft()) / seekBar.getMax()) *
                            seekBar.getProgress();
                    mThumbSlidingText.setPadding(xPos, 0, 0, 0);
                    //mThumbSlidingText.setText(String.valueOf(mPicDataList.get(nearest).mYear));
                    mThumbSlidingText.setText(String.valueOf((int) (mPicDataList.get(0).mYear + range * ((float) progressValue) / 100.0)));
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
                seekBar.setProgress(mPicDataList.get(nearest).mDotPosition);
                mFirstImageView.setImageDrawable(mPicDataList.get(nearest).mDrawable);
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

    /**
     * Set the mPicDataList Array with data of the database
     */
    private void setData() {
        for (int i = 0; i < page.getImages().size(); i++) {
            PictureData picture = new PictureData(page.getImages().get(i).
                    getDawableImage(view.getContext()), page.getDates().get(i).intValue());
            mPicDataList.add(picture);
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
