package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.models.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;

public class DetailsActivity extends ActionBarActivity {

    private DBAdapter database;

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_IMAGE = "detail:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_TITLE = "detail:title";

    private ImageView mImageView;
    private TextView mTextView;

    private int exhibitId;
    private boolean isSlider;
    private String imageName;

    //ActionBar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        openDatabase();

        mImageView = (ImageView) findViewById(R.id.imageViewDetail);
        imageName = "image.jpg";
        mImageView.setImageDrawable(database.getImage(1, imageName));
        mTextView = (TextView) findViewById(R.id.txtName);

        if (Build.VERSION.SDK_INT >= 21) {
            /**
             * Set the name of the view's which will be transition to, using the static values above.
             * This could be done in the layout XML, but exposing it via static variables allows easy
             * querying from other Activities
             */
            ViewCompat.setTransitionName(mImageView, VIEW_NAME_IMAGE);
            ViewCompat.setTransitionName(mTextView, VIEW_NAME_TITLE);


            addTransitionListener();
        }

        exhibitId = getIntent().getIntExtra("exhibit-id", 0);

        Drawable d = database.getImage(exhibitId, "image.jpg");
        mImageView.setImageDrawable(d);

        Document document = database.getDocument(exhibitId);
        Exhibit exhibit = new Exhibit(document);
        mTextView.setText(exhibit.name);
        if(exhibit.sliderID != -1){
            isSlider = true;
        }else{
            isSlider = false;
        }

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtDescription.setText(exhibit.description);

//        // Set ActionBar
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            toolbar.setTitle(exhibit.name);
//            setSupportActionBar(toolbar);
//        }

        // Set back button on actionbar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(exhibit.name);
        }

        //ImageButton fab = (ImageButton) findViewById(R.id.fab);
//        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                // Or read size directly from the view's width/height
//                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
//                outline.setOval(0, 0, size, size);
//            }
//        };
//        fab.setOutlineProvider(viewOutlineProvider);

    }

    /**
     * Try and add a {@link android.transition.Transition.TransitionListener} to the entering shared element
     * {@link android.transition.Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    Drawable d = database.getImage(exhibitId, "image.jpg");
                    mImageView.setImageDrawable(d);

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

    public void onClick_back(View view){
        this.finish();
    }

    public void onClick_imageViewDetail(View view) {
        if(isSlider) {
            Intent intent = new Intent(this, DisplayImageSliderActivity.class);
            intent.putExtra("exhibit-id", exhibitId);
            intent.putExtra("imageName", imageName);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, DisplaySingleImageActivity.class);
            intent.putExtra("exhibit-id", exhibitId);
            intent.putExtra("imageName", imageName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openDatabase() {
       database = new DBAdapter(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    /*  Check later if this is needed
    public void onStop() {
        mImageView.destroyDrawingCache();
        super.onStop();
    }
    */

}
