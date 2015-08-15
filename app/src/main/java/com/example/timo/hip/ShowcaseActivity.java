package com.example.timo.hip;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;


public class ShowcaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ShowcaseAdapter(this));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
