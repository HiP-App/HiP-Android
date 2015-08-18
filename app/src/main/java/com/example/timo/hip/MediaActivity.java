package com.example.timo.hip;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.couchbase.lite.Document;

/**
 * Created by Hagen Stahl on 13.08.2015.
 */
public class MediaActivity extends Activity implements SurfaceHolder.Callback{

    private DBAdapter database;

    private MediaPlayer mediaPlayer;
    public TextView mMediaTitle, mDuration;
    private ImageView mImageView;
    private VideoView mVideoView;
    private SurfaceView mVideoView2;
    private SurfaceHolder mVideoViewHolder;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    private int exhibitId;
    private int mediaType;
    private int media;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openDatabase();

        //set the layout of the Activity
        setContentView(R.layout.activity_media);
        exhibitId = getIntent().getIntExtra("exhibit-id", 0);
        mediaType = getIntent().getIntExtra("exhibit-media-type", 0);
        media = getIntent().getIntExtra("exhibit-media", 0);

        //initialize views
        initializeViews();
    }

    public void initializeViews(){
        if(mediaType != MediaTypes.NONE) {

            Document document = database.getRow(exhibitId);
            Exhibit exhibit = new Exhibit(document);


            mMediaTitle = (TextView) findViewById(R.id.mediaTitle);
            mMediaTitle.setText(exhibit.name);

            if (mediaType == MediaTypes.AUDIO) {
                findViewById(R.id.videoImageView).setVisibility(View.GONE);
                mImageView = (ImageView) findViewById(R.id.audioImageView);


                Drawable d = DBAdapter.getImage(exhibitId);
                mImageView.setImageDrawable(d);
                mImageView.setVisibility(View.VISIBLE);

                mediaPlayer = MediaPlayer.create(this, this.media);


            }else if(mediaType == MediaTypes.VIDEO){
                findViewById(R.id.audioImageView).setVisibility(View.GONE);

                getWindow().setFormat(PixelFormat.UNKNOWN);
                mVideoView2 = (SurfaceView) findViewById(R.id.videoImageView2);
                mVideoView2.setVisibility(View.VISIBLE);

                mVideoViewHolder = mVideoView2.getHolder();
                mVideoViewHolder.addCallback(this);
                mVideoViewHolder.setFixedSize(176, 144);
                mVideoViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mediaPlayer = new MediaPlayer();

                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                String targetUri = "android.resource://" + getPackageName() + "/"+R.raw.video;
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(targetUri));
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            finalTime = mediaPlayer.getDuration();
            mDuration = (TextView) findViewById(R.id.duration);
            seekbar = (SeekBar) findViewById(R.id.seekBar);

            seekbar.setMax((int) finalTime);
            seekbar.setClickable(false);
        }
    }

    // play mp3 song
    public void play(View view) {
        mediaPlayer.setDisplay(mVideoViewHolder);
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            try {
                //get current position
                timeElapsed = mediaPlayer.getCurrentPosition();
                //set seekbar progress
                seekbar.setProgress((int) timeElapsed);
                //set time remaing
                double timeRemaining = finalTime - timeElapsed;
                mDuration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

                //repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            } catch (IllegalStateException e) {
            }
        }
    };

    // pause mp3 song
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // go forward at forwardTime seconds
    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song endes
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    // go backwards at backwardTime seconds
    public void rewind(View view) {
        //check if we can go back at backwardTime seconds after song starts
        if ((timeElapsed - backwardTime) > 0) {
            timeElapsed = timeElapsed - backwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    private void openDatabase() {
        database = new DBAdapter(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }
}
