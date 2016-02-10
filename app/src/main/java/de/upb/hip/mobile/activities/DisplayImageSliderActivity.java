package de.upb.hip.mobile.activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.helpers.CustomSeekBar;
import de.upb.hip.mobile.models.Exhibit;
import de.upb.hip.mobile.models.SliderImage;

public class DisplayImageSliderActivity extends ActionBarActivity {
    private DBAdapter database;
    private Exhibit exhibit;
    private int exhibitId;
    private ImageView mImageViewTimeLine;
    private ImageView mImageViewTimeLine2;
    private TextView mTextView;
    private TextView txtMiddleSeekBar;
    private CustomSeekBar mSeekBar;
    private boolean fontFading = true;

    private List<PictureData> mPicDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image_slider);

        // TODO read and set data from Database
        database = new DBAdapter(this);
        exhibitId = getIntent().getIntExtra("exhibit-id", 0);
        exhibit = new Exhibit(database.getDocument(exhibitId));

        setData();

        init();

        mTextView = (TextView) findViewById(R.id.TextView01);
        mTextView.setText(exhibit.pictureDescriptions.get(getIntent().getStringExtra("imageName")));

        // Set back button on actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(exhibit.name);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void init(){
        calcDotPositions(mPicDataList);

        mSeekBar = (CustomSeekBar)findViewById(R.id.seekBar);
        mSeekBar.setDots(getListOfDotPositions(mPicDataList));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.customseekbar, getTheme()));
        } else {
            mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.customseekbar));
        }

        mImageViewTimeLine = (ImageView) findViewById(R.id.imageViewTimeLine);
        mImageViewTimeLine.setImageDrawable(mPicDataList.get(0).drawable);

        if (fontFading) {
            mImageViewTimeLine2 = (ImageView) findViewById(R.id.imageViewTimeLine2);
            mImageViewTimeLine2.setImageDrawable(mPicDataList.get(1).drawable);
            mImageViewTimeLine.bringToFront();
        }

        TextView txtStartSeekBar = (TextView) findViewById(R.id.txtStartSeekBar);
        txtStartSeekBar.setText(String.valueOf(mPicDataList.get(0).year));

        TextView txtEndSeekBar = (TextView) findViewById(R.id.txtEndSeekBar);
        txtEndSeekBar.setText(String.valueOf(mPicDataList.get(mPicDataList.size() - 1).year));

        txtMiddleSeekBar = (TextView) findViewById(R.id.txtMiddleSeekBar);

        addSeekBarListener();
        openDatabase();
    }

    private void calcDotPositions(List<PictureData> list){
        // set progress for the first picture
        list.get(0).dotPosition = 0;

        // set progress for other pictures
        int lSize = list.size();
        for (int i = 1; i < lSize; i++){
            if (i + 1 < lSize){
                int progress = 100 * (list.get(i).year - list.get(i - 1).year) /
                        (list.get(lSize - 1).year - list.get(0).year);
                list.get(i).dotPosition = (progress + list.get(i - 1).dotPosition);
            }
            else{
                // set progress for last picture
                list.get(i).dotPosition = 100;
            }
        }
    }

    private void addSeekBarListener() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressStart = 0;
            boolean forward = true;
            int nearest = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                int startNode, nextNode;

                // deсide the direction (forward or backward)
                if (progressStart > progresValue) {
                    forward = false;
                } else {
                    forward = true;
                }

                // find closest startNode and nextNode, according to the direction (forward or backward)
                int[] result = getNodes(progresValue, forward);
                startNode = result[0];
                nextNode = result[1];

                if (fontFading) {
                    int actProgressAccordingStartNextNode = Math.abs(progresValue - mPicDataList.get(startNode).dotPosition);
                    int differenceStartNextNode = Math.abs(mPicDataList.get(nextNode).dotPosition - mPicDataList.get(startNode).dotPosition);
                    float alpha = (float) actProgressAccordingStartNextNode / differenceStartNextNode;

                    mImageViewTimeLine.setImageDrawable(mPicDataList.get(startNode).drawable);
                    mImageViewTimeLine.setAlpha(1 - alpha);

                    mImageViewTimeLine2.setImageDrawable(mPicDataList.get(nextNode).drawable);
                    mImageViewTimeLine2.setAlpha(alpha);

                    mImageViewTimeLine.bringToFront();
                }

                // for showcase image: get the closest node to actual progress
                nearest = findClosestNode(result, progresValue);

                // set year over the thumb except first and last picture
                if (progresValue != 0 && progresValue != 100) {
                    int xPos = ((seekBar.getRight() - seekBar.getLeft()) / seekBar.getMax()) * seekBar.getProgress();
                    txtMiddleSeekBar.setPadding(xPos, 0, 0, 0);
                    txtMiddleSeekBar.setText(String.valueOf(mPicDataList.get(nearest).year));

                } else {
                    // set empty text for first and last position
                    txtMiddleSeekBar.setText("");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressStart = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!fontFading) {
                    seekBar.setProgress(mPicDataList.get(nearest).dotPosition);
                    mImageViewTimeLine.setImageDrawable(mPicDataList.get(nearest).drawable);
                }
            }

            private int[] getNodes(int progressStop, boolean forward) {
                for (int i = 0; i < mPicDataList.size(); i++) {
                    if (forward) {
                        if ((progressStop >= mPicDataList.get(i).dotPosition) &&
                                (progressStop <= mPicDataList.get(i + 1).dotPosition)) {
                            return new int[]{i, i + 1};
                        }
                    } else {
                        if (i == 0) i = 1;

                        if (progressStop <= mPicDataList.get(i).dotPosition &&
                                (progressStop >= mPicDataList.get(i - 1).dotPosition)) {
                            return new int[]{i, i - 1};
                        }
                    }
                }
                return new int[]{0, 0};
            }

            private int findClosestNode(int[] array, int progress) {
                int min = 0, max = 0, closestNode;

                for (int i = 0; i < array.length; i++) {
                    if (mPicDataList.get(array[i]).dotPosition < progress) {
                        if (min == 0) {
                            min = array[i];
                        } else if (mPicDataList.get(array[i]).dotPosition > mPicDataList.get(min).dotPosition) {
                            min = array[i];
                        }
                    } else if (mPicDataList.get(array[i]).dotPosition > progress) {
                        if (max == 0) {
                            max = array[i];
                        } else if (mPicDataList.get(array[i]).dotPosition < mPicDataList.get(max).dotPosition) {
                            max = array[i];
                        }
                    } else {
                        return array[i];
                    }
                }

                if (Math.abs(progress - mPicDataList.get(min).dotPosition) <
                        Math.abs(progress - mPicDataList.get(max).dotPosition)) {
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

    private void openDatabase() {
        database = new DBAdapter(this);
    }

    private void setData(){

        int sliderID = exhibit.sliderID;
        ArrayList<Map<String, Object>> images = (ArrayList<Map<String, Object>>)database.getDocument(sliderID).getProperty(DBAdapter.KEY_SLIDER_IMAGES);

        for(int i = 0; i < images.size(); i++){
            Map<String, Object> properties = images.get(i);
            mPicDataList.add(new PictureData(DBAdapter.getImage(sliderID, (String)properties.get(DBAdapter.KEY_SLIDER_IMAGE_NAME)), (int)properties.get(DBAdapter.KEY_SLIDER_IMAGE_YEAR)));
        }
    }

    // create list for setting dots on seekbar
    private List<Integer> getListOfDotPositions(List<PictureData> list){
        List<Integer> mPicDataProgressList = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            mPicDataProgressList.add(list.get(i).dotPosition);
        }
        return mPicDataProgressList;
    }

    private class PictureData {
        private Drawable drawable;
        private int year;
        private int dotPosition;

        public PictureData(Drawable drawable, int iYear){
            this.drawable = drawable;
            year = iYear;
            dotPosition = 0;
        }
    }
}
