package com.example.timo.hip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowcaseAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public ShowcaseAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
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

        convertView = mInflater.inflate(R.layout.showcase_row, null);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageViewShowcase);
        imageView.setImageResource(mThumbIds[position]);
        //imageView.setLayoutParams(new GridView.LayoutParams(400,600));
//        imageButton.setLayoutParams(new GridView.LayoutParams(400,600));
//        imageButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
//        imageButton.setPadding(10, 10, 10, 10);
//        imageButton.setImageResource(mThumbIds[0]);
        convertView.setId(position);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, mActivitys[v.getId()]);
                mContext.startActivity(intent);
            }
        });
        TextView textView = (TextView) convertView.findViewById(R.id.textViewShowcase);
        textView.setText(mDescriptions[position]);

        return convertView;

    }

    private Integer[] mThumbIds =
            {
                R.drawable.showcase_main,
                R.drawable.showcase_main,
                R.drawable.showcase_main,
                R.drawable.showcase_imageview,
                R.drawable.showcase_main,
                R.drawable.showcase_main
            };

    private Class[] mActivitys =
            {
                    MainActivity.class,
                    DisplayImageView.class,
                    ArExampleActivity.class,
                    ImageBoundariesActivity.class,
                    GMapOldActivity.class
            };

    private String[] mDescriptions =
            {
                    "Main Activity",
                    "Image View",
                    "AR Example",
                    "ImageBoundaries",
                    "Old Map"
            };
}
