package com.example.timo.hip;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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

            Intent intent = new Intent(this.mMainActivity, DetailsActivity.class);

            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this.mMainActivity,

                    // Now we provide a list of Pair items which contain the view we can transitioning
                    // from, and the name of the view it is transitioning to, in the launched activity
                    new Pair<View, String>(childView.findViewById(R.id.imageViewMain),
                            DetailsActivity.VIEW_NAME_IMAGE),
                    new Pair<View, String>(childView.findViewById(R.id.txtName),
                            DetailsActivity.VIEW_NAME_TITLE));


            intent.putExtra("exhibit-id", childView.getId());
            ActivityCompat.startActivity(this.mMainActivity, intent, activityOptions.toBundle());

        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
