package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.models.Exhibit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;


public class DisplaySingleImageActivity extends ActionBarActivity {

    private DBAdapter database;
    private Exhibit exhibit;

    private int exhibitId;

    private ImageView mImageView;
    private TextView mTextView;

    //ActionBar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_image);

        mImageView = (ImageView) findViewById(R.id.ImageView01);
        mTextView = (TextView) findViewById(R.id.TextView01);

        database = new DBAdapter(this);
        exhibitId = getIntent().getIntExtra("exhibit-id", 0);
        exhibit = new Exhibit(database.getDocument(exhibitId));

        Drawable d = DBAdapter.getImage(exhibitId, "image.jpg");
        mImageView.setImageDrawable(d);

        mTextView.setText(exhibit.pictureDescriptions.get(getIntent().getStringExtra("imageName")));

        // Set back button on actionbar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(exhibit.name);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}