package com.example.timo.hip;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ImageSelectionActivity extends Activity {
    private ImageView mImageViewSelection;
    private ImageView mImageViewSelection2;

    private RadioGroup radGroup;

    // TODO bad style, change late
    //         xTOP, yTop, xBotton, yBotton
    int[] left = {170,770,290,1300};
    int[] right = {780,770,920,1300};
    int[] center = {445,620,620,1165};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        mImageViewSelection = (ImageView) findViewById(R.id.imageViewSelection);
        mImageViewSelection.setImageResource(R.drawable.paradiesportal);
        mImageViewSelection2 = (ImageView) findViewById(R.id.imageViewSelection2);
        radGroup = (RadioGroup) findViewById(R.id.rgroupImgSelection);

        radGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                float alpha = (float)0.3;
                switch (id) {
                    case R.id.rdbStKilian:
                        // StKilian
                        mImageViewSelection.setAlpha(alpha);
                        mImageViewSelection2.setImageResource(R.drawable.st_kilian);
                        mImageViewSelection2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rdbMaria:
                        // Maria
                        mImageViewSelection.setAlpha(alpha);
                        mImageViewSelection2.setImageResource(R.drawable.maria);
                        mImageViewSelection2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rdbStLiborius:
                        // StLiborius
                        mImageViewSelection.setAlpha(alpha);
                        mImageViewSelection2.setImageResource(R.drawable.st_liborius);
                        mImageViewSelection2.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mImageViewSelection.setAlpha((float)1.0);
                        mImageViewSelection2.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int eventAction = event.getAction();
        switch(eventAction) {
            case MotionEvent.ACTION_UP:
                float TouchX = event.getX();
                float TouchY = event.getY();
                placeImage(TouchX, TouchY);
                break;
        }
        return true;
    }

    private void placeImage(float X, float Y) {
        int touchX = (int) X;
        int touchY = (int) Y;

        float alpha = (float)0.3;

        if ( (touchX > left[0] && touchX < left[2]) && (touchY > left[1] && touchY < left[3]) ){
            // StKilian
            mImageViewSelection.setAlpha(alpha);
            mImageViewSelection2.setImageResource(R.drawable.st_kilian);
            mImageViewSelection2.setVisibility(View.VISIBLE);
            radGroup.check(R.id.rdbStKilian);
        }
        else if ( (touchX > right[0] && touchX < right[2]) && (touchY > right[1] && touchY < right[3]) ) {
            // StLiborius
            mImageViewSelection.setAlpha(alpha);
            mImageViewSelection2.setImageResource(R.drawable.st_liborius);
            mImageViewSelection2.setVisibility(View.VISIBLE);
            radGroup.check(R.id.rdbStLiborius);

        }
        else if ((touchX > center[0] && touchX < center[2]) && (touchY > center[1] && touchY < center[3])){
            // Maria
            mImageViewSelection.setAlpha(alpha);
            mImageViewSelection2.setImageResource(R.drawable.maria);
            mImageViewSelection2.setVisibility(View.VISIBLE);
            radGroup.check(R.id.rdbMaria);

        }
        else{
            mImageViewSelection.setAlpha((float)1.0);
            mImageViewSelection2.setVisibility(View.INVISIBLE);
            radGroup.check(R.id.rdbWholePic);

        }

    }
}
