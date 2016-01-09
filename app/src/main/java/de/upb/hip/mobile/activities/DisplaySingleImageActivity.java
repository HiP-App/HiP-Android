package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;


public class DisplaySingleImageActivity extends ActionBarActivity {

    private DBAdapter database;

    private int exhibitId;

    private ImageView mImageView;
    private TextView mTextView;

    //ActionBar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_image);
        Button button = (Button) this.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImageView = (ImageView) findViewById(R.id.ImageView01);
        mTextView = (TextView) findViewById(R.id.TextView01);

        database = new DBAdapter(this);

        exhibitId = getIntent().getIntExtra("exhibit-id", 0);

        Drawable d = DBAdapter.getImage(exhibitId);
        mImageView.setImageDrawable(d);


//        Document document = database.getDocument(exhibitId);
//        Exhibit exhibit = new Exhibit(document);

        // Set back button on actionbar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(exhibit.name);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}