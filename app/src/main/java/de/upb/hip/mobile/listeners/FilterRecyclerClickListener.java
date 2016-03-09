package de.upb.hip.mobile.listeners;

import de.upb.hip.mobile.activities.*;

import android.support.v7.widget.RecyclerView;
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
            TextView txtName = (TextView) childView.findViewById(R.id.detailsName);
            mMainActivity.updateCategories(txtName.getText().toString());

            CheckBox checkBox = (CheckBox) childView.findViewById(R.id.checkBox);
            if(checkBox.isChecked()) checkBox.setChecked(false);
            else checkBox.setChecked(true);
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
