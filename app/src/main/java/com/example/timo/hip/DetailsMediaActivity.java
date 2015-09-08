package com.example.timo.hip;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.couchbase.lite.Document;

public class DetailsMediaActivity extends Activity {

    private DBAdapter database;

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_IMAGE = "detail:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_TITLE = "detail:title";

    private ImageView mImageView;
    private TextView mTextView;
    private VideoView mVideoView;
    private MediaPlayer mediaPlayer;
    final DetailsMediaActivity mDetailsActivity = this;

    private int exhibitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_details);

        mImageView = (ImageView) findViewById(R.id.imageViewDetail);
        mTextView = (TextView) findViewById(R.id.txtName);
        //mVideoView = (VideoView) findViewById(R.id.videoView);

        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(mImageView, VIEW_NAME_IMAGE);
        ViewCompat.setTransitionName(mTextView, VIEW_NAME_TITLE);

        addTransitionListener();

        openDatabase();

        exhibitId = getIntent().getIntExtra("exhibit-id", 0);

        Drawable d = DBAdapter.getImage(exhibitId);
        mImageView.setImageDrawable(d);

        Document document = database.getRow(exhibitId);
        Exhibit exhibit = new Exhibit(document);

        //String uri = "android.resouce://" + getPackageName() + "/" + R.raw.video;
        //mVideoView.setVideoURI(Uri.parse(uri));
        //mVideoView.requestFocus();
        //mVideoView.start();

        //mediaPlayer.create(DetailsActivity.this, R.raw.video);
        //mediaPlayer.start();

        mTextView.setText(exhibit.name);

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtDescription.setText(exhibit.description);

        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fab.setOutlineProvider(viewOutlineProvider);

    }

    public void onClick_video(View view){
        Intent intent = new Intent(this, MediaActivity.class);

        intent.putExtra("exhibit-id", this.exhibitId);
        intent.putExtra("exhibit-media-type", MediaTypes.VIDEO);
        intent.putExtra("exhibit-media", R.raw.video);
        startActivity(intent);
    }

    public void onClick_audio(View view){
        Intent intent = new Intent(this, MediaActivity.class);

        intent.putExtra("exhibit-id", this.exhibitId);
        intent.putExtra("exhibit-media-type", MediaTypes.AUDIO);
        intent.putExtra("exhibit-media", R.raw.audio);
        startActivity(intent);
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
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
                    String image_url = "http://tboegeholz.de/ba/pictures/" + exhibitId + ".jpg";
                    ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                    imgLoader.DisplayImage(image_url, mImageView);

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
        Log.i("Click", "BACK!");
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //closeDatabase();
    }

    private void openDatabase() {
        database = new DBAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_mediaview:
                openImageView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openImageView() {
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }
}
