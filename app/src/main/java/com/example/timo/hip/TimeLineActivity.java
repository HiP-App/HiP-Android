package com.example.timo.hip;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends Activity {
    private DBAdapter database;
    private int exhibitId;
    private ImageView mImageViewTimeLine;
    private ImageView mImageViewTimeLine2;
    private TextView txtMiddleSeekBar;
    private CustomSeekBar mSeekBar;
    private boolean fontFading = false;
    private TimeLineActivity mTimeLineActivity;

    private List<PictureDataHelp> mPicDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // TODO read and set data from Database

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fontFading = extras.getBoolean("FONT_FADING");
        }

        mTimeLineActivity = this;

        setDataShowCase();

        init();
    }

    private void init(){
        calcProgressSeekBarAccordingToPicDate(mPicDataList);

        mSeekBar = (CustomSeekBar)findViewById(R.id.seekBar);
        mSeekBar.setProgress(getPicDataProgressList(mPicDataList));
        mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.customseekbar));

        mImageViewTimeLine = (ImageView) findViewById(R.id.imageViewTimeLine);
        mImageViewTimeLine.setImageResource(mPicDataList.get(0).picID);

        if (fontFading) {
            mImageViewTimeLine2 = (ImageView) findViewById(R.id.imageViewTimeLine2);
            mImageViewTimeLine2.setImageResource(mPicDataList.get(1).picID);
            mImageViewTimeLine.bringToFront();
        }

        TextView txtStartSeekBar = (TextView) findViewById(R.id.txtStartSeekBar);
        txtStartSeekBar.setTextColor(Color.WHITE);
        txtStartSeekBar.setTextSize(10);
        txtStartSeekBar.setText(String.valueOf(mPicDataList.get(0).picYear));

        TextView txtEndSeekBar = (TextView) findViewById(R.id.txtEndSeekBar);
        txtEndSeekBar.setTextColor(Color.WHITE);
        txtEndSeekBar.setTextSize(10);
        txtEndSeekBar.setText(String.valueOf(mPicDataList.get(mPicDataList.size() - 1).picYear));

        txtMiddleSeekBar = (TextView) findViewById(R.id.txtMiddleSeekBar);

        addSeekBarListner();
        openDatabase();
    }

    private void calcProgressSeekBarAccordingToPicDate(List<PictureDataHelp> list){
        // set progress for the first picture
        list.get(0).picProgress = 0;

        // set progress for other pictures
        int lSize = list.size();
        for (int i = 1; i < lSize; i++){
            if (i + 1 < lSize){
                int progress = 100 * (list.get(i).picYear - list.get(i - 1).picYear) /
                               (list.get(lSize - 1).picYear - list.get(0).picYear);
                list.get(i).picProgress = (progress + list.get(i - 1).picProgress);
            }
            else{
                // set progress for last picture
                list.get(i).picProgress = 100;
            }
        }
    }

    private void addSeekBarListner() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressStart = 0;
            boolean forward   = true;
            int nearest = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                int startNode = 0, nextNode = 0;

                // deсide the direction (forward or backward)
                if (progressStart > progresValue){
                    forward = false;
                }
                else{
                    forward = true;
                }

                // find closest startNode and nextNode, according to the direction (forward or backward)
                int[] result  = getNodes(progresValue, forward);
                startNode = result[0];
                nextNode  = result[1];

                if (fontFading) {
                    int actProgressAccordingStartNextNode = Math.abs(progresValue - mPicDataList.get(startNode).picProgress);
                    int differenceStartNextNode = Math.abs(mPicDataList.get(nextNode).picProgress - mPicDataList.get(startNode).picProgress);
                    float alpha = (float) actProgressAccordingStartNextNode / differenceStartNextNode;

                    mImageViewTimeLine.setImageResource(mPicDataList.get(startNode).picID);
                    mImageViewTimeLine.setAlpha(1 - alpha);

                    mImageViewTimeLine2.setImageResource(mPicDataList.get(nextNode).picID);
                    mImageViewTimeLine2.setAlpha(alpha);

                    mImageViewTimeLine.bringToFront();
                }

                // for showcase image: get the closest node to actual progress
                nearest = findClosestNode(result, progresValue);

                // set year over the thumb except first and last picture
                if (progresValue != 0 && progresValue != 100) {
                    int xPos = ((seekBar.getRight() - seekBar.getLeft()) / seekBar.getMax()) * seekBar.getProgress();
                    txtMiddleSeekBar.setPadding(xPos, 0, 0, 0);
                    txtMiddleSeekBar.setText(String.valueOf(mPicDataList.get(nearest).picYear));

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
                if ( !fontFading) {
                    seekBar.setProgress(mPicDataList.get(nearest).picProgress);
                    mImageViewTimeLine.setImageResource(mPicDataList.get(nearest).picID);
                }
            }

            private void showCaseImage(int progresValue){
            }

            private void showCaseFont(int progresValue){
            }

            private int[] getNodes(int progressStop, boolean forward) {
                for (int i = 0; i < mPicDataList.size(); i++) {
                    if (forward){
                        if ((progressStop >= mPicDataList.get(i).picProgress) &&
                                (progressStop <= mPicDataList.get(i + 1).picProgress)) {
                            return new int[]{i, i + 1};
                        }
                    }
                    else {
                        if ( i == 0) i = 1;

                        if ( progressStop <= mPicDataList.get(i).picProgress &&
                                (progressStop >= mPicDataList.get(i - 1).picProgress)) {
                            return new int[]{i, i - 1};
                        }
                    }
                }
                return new int[]{0, 0};
            }

            private int findClosestNode(int[] array, int progress) {
                int min = 0, max = 0, closestNode;

                for(int i = 0; i < array.length; i++) {
                    if(mPicDataList.get(array[i]).picProgress < progress) {
                        if(min == 0) {
                            min = array[i];
                        }
                        else if(mPicDataList.get(array[i]).picProgress > mPicDataList.get(min).picProgress) {
                            min = array[i];
                        }
                    }
                    else if(mPicDataList.get(array[i]).picProgress > progress) {
                        if(max == 0) {
                            max = array[i];
                        }
                        else if(mPicDataList.get(array[i]).picProgress < mPicDataList.get(max).picProgress) {
                            max = array[i];
                        }
                    }
                    else {
                        return array[i];
                    }
                }

                if(Math.abs(progress - mPicDataList.get(min).picProgress) <
                        Math.abs(progress - mPicDataList.get(max).picProgress)) {
                    closestNode = min;
                }
                else {
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

    // set test data to show images and calculate progress
    private void setDataShowCase(){
        if (fontFading){
            //Also show Pfalz pictures here for demonstration purposes
            mPicDataList.add(new PictureDataHelp("phasei", 776));
            mPicDataList.add(new PictureDataHelp("phaseii", 799));
            mPicDataList.add(new PictureDataHelp("phaseiii", 836));
            mPicDataList.add(new PictureDataHelp("phaseiv", 900));
            mPicDataList.add(new PictureDataHelp("phasev", 938));
            //mPicDataList.add(new PictureDataHelp("newsweek_1949", 1949));
            //mPicDataList.add(new PictureDataHelp("newsweek_1970", 1970));
            //mPicDataList.add(new PictureDataHelp("newsweek_1986", 1986));
            //mPicDataList.add(new PictureDataHelp("newsweek_2011", 2011));
        }
        else{
            mPicDataList.add(new PictureDataHelp("phasei", 776));
            mPicDataList.add(new PictureDataHelp("phaseii", 799));
            mPicDataList.add(new PictureDataHelp("phaseiii", 836));
            mPicDataList.add(new PictureDataHelp("phaseiv", 900));
            mPicDataList.add(new PictureDataHelp("phasev", 938));
        }
    }

    // create list for setting dots on seekbar
    private List<Integer> getPicDataProgressList(List<PictureDataHelp> list){
        List<Integer> mPicDataProgressList = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            mPicDataProgressList.add(list.get(i).picProgress);
        }
        return mPicDataProgressList;
    }

    private class PictureDataHelp{
        private int picID;
        private int picYear;
        private int picProgress;

        public PictureDataHelp(String strName, int iYear){
            picID = getResources().getIdentifier(strName, "drawable", getPackageName());
            picYear = iYear;
            picProgress = 0;
        }

    }
}
