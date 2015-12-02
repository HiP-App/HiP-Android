package de.upb.hip.mobile.activities;

import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FilterPopupWindow extends PopupWindow{

    public FilterPopupWindow(View popupView, int wrapContent, int wrapContent1, String test) {
        super(popupView, wrapContent, wrapContent1);
        ((TextView) popupView.findViewById(R.id.textView)).setText(test);
    }
}
