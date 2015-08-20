package com.example.timo.hip;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends Activity {
    private DBAdapter database;
    private int exhibitId;
    private ImageView mImageViewTimeLine;
    private TextView txtMiddleSeekBar;
    private SeekBar mSeekBarTimeLine;
    private CustomSeekBar mSeekBar = null;

    private List<PictureDataTemp> mPicDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        init();

        // TODO read and set data from Database
        /*exhibitId = getIntent().getIntExtra("exhibit-id", 1);
        Drawable d = DBAdapter.getImage(exhibitId);
        mImageViewTimeLine.setImageDrawable(d);*/
    }

    private void init(){
        setDataShowCase();
        calcProgressSeekBarAccordingToPicDate(mPicDataList);

        mSeekBar = (CustomSeekBar)findViewById(R.id.seekBar);
        mSeekBar.setProgress(getPicDataProgressList(mPicDataList));

        mImageViewTimeLine = (ImageView) findViewById(R.id.imageViewTimeLine);
        mImageViewTimeLine.setImageResource(mPicDataList.get(0).getPicID());
        mImageViewTimeLine.setImageResource(mPicDataList.get(0).getPicID());

        mSeekBarTimeLine   = (CustomSeekBar) findViewById(R.id.seekBar);
        mSeekBarTimeLine.setProgressDrawable(getResources().getDrawable(R.drawable.customseekbar));

        TextView txtStartSeekBar = (TextView) findViewById(R.id.txtStartSeekBar);
        txtStartSeekBar.setTextColor(Color.WHITE);
        txtStartSeekBar.setTextSize(10);
        txtStartSeekBar.setText(String.valueOf(mPicDataList.get(0).getPicYear()));

        TextView txtEndSeekBar = (TextView) findViewById(R.id.txtEndSeekBar);
        txtEndSeekBar.setTextColor(Color.WHITE);
        txtEndSeekBar.setTextSize(10);
        txtEndSeekBar.setText(String.valueOf(mPicDataList.get(mPicDataList.size() - 1).getPicYear()));

        txtMiddleSeekBar = (TextView) findViewById(R.id.txtMiddleSeekBar);

        addSeekBarListner();
        openDatabase();
    }

    private void calcProgressSeekBarAccordingToPicDate(List<PictureDataTemp> list){
        // set progress for the first picture
        list.get(0).setPicProgress(0);

        // set progress for other pictures
        int lSize = list.size();
        for (int i = 1; i < lSize; i++){
            if (i + 1 < lSize){
                int progress = 100 * (list.get(i).getPicYear() - list.get(i - 1).getPicYear()) /
                               (list.get(lSize - 1).getPicYear() - list.get(0).getPicYear());
                list.get(i).setPicProgress(progress + list.get(i - 1).getPicProgress());
            }
            else{
                // set progress for last picture
                list.get(i).setPicProgress(100);
            }
        }
    }

    private void addSeekBarListner() {

        mSeekBarTimeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                // find nearest progress according to received ProgressValue
                int nearestMatchIndex = 0;
                for (int i = 1; i < mPicDataList.size(); i++) {
                    if (Math.abs(progresValue - mPicDataList.get(nearestMatchIndex).getPicProgress())
                            > Math.abs(progresValue - mPicDataList.get(i).getPicProgress())) {
                        nearestMatchIndex = i;
                    }
                }
                // set image
                mImageViewTimeLine.setImageResource(mPicDataList.get(nearestMatchIndex).getPicID());

                // set image year over thumb except first and last position
                if ((nearestMatchIndex > 0) && (nearestMatchIndex < mPicDataList.size() - 1)) {
                    int xPos = ((seekBar.getRight() - seekBar.getLeft()) / seekBar.getMax()) * seekBar.getProgress();
                    txtMiddleSeekBar.setPadding(xPos, 0, 0, 0);
                    txtMiddleSeekBar.setText(String.valueOf(mPicDataList.get(nearestMatchIndex).getPicYear()));

                } else {
                    // set empty text for first and last position
                    txtMiddleSeekBar.setText("");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // find and set progress after stop tracking touch
                // thumb would be set to nearest progress from the list
                int stopProgress = seekBar.getProgress();
                int nearestMatchIndex = 0;
                for (int i = 1; i < mPicDataList.size(); i++) {
                    if (Math.abs(stopProgress - mPicDataList.get(nearestMatchIndex).getPicProgress())
                            > Math.abs(stopProgress - mPicDataList.get(i).getPicProgress())) {
                        nearestMatchIndex = i;
                    }
                }
                progress = mPicDataList.get(nearestMatchIndex).getPicProgress();
                seekBar.setProgress(progress);
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
        mPicDataList.add(new PictureDataTemp("busdorfkirche_aussen", 2002));
        mPicDataList.add(new PictureDataTemp("busdorfkirche_innen", 2005));
        mPicDataList.add(new PictureDataTemp("dom", 2007));
        mPicDataList.add(new PictureDataTemp("kreuzgang_busdorfkirche", 2011));
        mPicDataList.add(new PictureDataTemp("databasetest", 2015));
    }

    // create list for setting dots on seekbar
    private List<Integer> getPicDataProgressList(List<PictureDataTemp> list){
        List<Integer> mPicDataProgressList = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            mPicDataProgressList.add(list.get(i).getPicProgress());
        }
        return mPicDataProgressList;
    }

    private class PictureDataTemp{
        private int picID;
        private int picYear;
        private int picProgress;

        public PictureDataTemp(String strName, int iYear){
            picID = getResources().getIdentifier(strName, "drawable", getPackageName());
            picYear = iYear;
            picProgress = 0;
        }

        public int getPicID(){
            return this.picID;
        }

        public int getPicYear(){
            return this.picYear;
        }

        public int getPicProgress(){
            return this.picProgress;
        }

        public void setPicProgress(int picProgress){
            this.picProgress = picProgress;
        }
    }
}
