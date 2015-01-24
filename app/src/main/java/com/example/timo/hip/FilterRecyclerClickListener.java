package com.example.timo.hip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class FilterRecyclerClickListener implements RecyclerView.OnItemTouchListener {
    MainActivity mMainActivity;

    public FilterRecyclerClickListener(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if(childView != null && e.getAction() == e.ACTION_DOWN) {
            TextView txtName = (TextView) childView.findViewById(R.id.txtName);
            mMainActivity.updateCategories(txtName.getText().toString());

            CheckBox checkBox = (CheckBox) childView.findViewById(R.id.checkBox);
            if(checkBox.isChecked()) checkBox.setChecked(false);
            else checkBox.setChecked(true);
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
