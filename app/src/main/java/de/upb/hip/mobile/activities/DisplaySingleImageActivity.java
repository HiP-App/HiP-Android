package de.upb.hip.mobile.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Exhibit;

/**
 * This activity shows a single image together with a descriptive text
 */
public class DisplaySingleImageActivity extends ActionBarActivity {

    public static final String INTENT_EXHIBIT_ID = "exhibit-id";
    public static final String INTENT_IMAGE_NAME = "imageName";

    private DBAdapter mDatabase;
    private Exhibit mExhibit;

    private int mExhibitId;

    private ImageView mImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_image);

        mImageView = (ImageView) findViewById(R.id.displaySingleImageImageView);
        mTextView = (TextView) findViewById(R.id.displaySingleImageTextView);

        mDatabase = new DBAdapter(this);
        mExhibitId = getIntent().getIntExtra(INTENT_EXHIBIT_ID, 0);
        mExhibit = new Exhibit(mDatabase.getDocument(mExhibitId));

        //TODO: Replace this string constant
        Drawable d = DBAdapter.getImage(mExhibitId, "image.jpg");
        mImageView.setImageDrawable(d);

        mTextView.setText(mExhibit.pictureDescriptions.get(getIntent().getStringExtra(INTENT_IMAGE_NAME)));

        setUpActionBar();

    }

    /**
     * Set back button on actionbar
     */
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mExhibit.name);
        }
    }

    /**
     * Ensures tat the back button on te action bar works properly
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}