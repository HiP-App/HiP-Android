package com.example.timo.hip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class ShowcaseAdapter extends BaseAdapter {
    private Context mContext;

    public ShowcaseAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return Math.min(mThumbIds.length, mActivitys.length);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageButton imageButton;
        if (convertView == null) {
            imageButton = new ImageButton(mContext);
            imageButton.setLayoutParams(new GridView.LayoutParams(400,600));
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            imageButton.setPadding(10, 10, 10, 10);

        } else {
            imageButton = (ImageButton) convertView;
        }

        imageButton.setImageResource(mThumbIds[0]);
        imageButton.setId(position);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, mActivitys[v.getId()]);
                mContext.startActivity(intent);
            }
        });

        return imageButton;

    }

    private Integer[] mThumbIds =
            {
                R.drawable.showcase_main
            };

    private Class[] mActivitys =
            {
                MainActivity.class
            };
}
