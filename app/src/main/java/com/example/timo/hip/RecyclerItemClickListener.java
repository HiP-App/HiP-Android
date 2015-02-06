package com.example.timo.hip;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    MainActivity mMainActivity;
    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
        mGestureDetector = new GestureDetector(mMainActivity, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if(childView != null && mGestureDetector.onTouchEvent(e)) {
                // Action
            //                        ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.SCALE_Y, 5);
//
//                        anim.setRepeatCount(1);
//                        anim.setRepeatMode(ValueAnimator.REVERSE);
//                        anim.setDuration(1000);
//                        anim.start();


            //

            final View txtName = childView.findViewById(R.id.txtName);
            Intent intent = new Intent(this.mMainActivity, DetailsActivity.class);

            ImageView imageView = (ImageView) childView.findViewById(R.id.imageViewMain);
            //getWindow().setExitTransition(new Explode());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this.mMainActivity, txtName, "txtName");
            //ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,0, view.getWidth(), view.getHeight());

            intent.putExtra("exhibit-id", childView.getId());
            this.mMainActivity.startActivity(intent, options.toBundle());

            //Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            this.mMainActivity.startActivity(intent);
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
