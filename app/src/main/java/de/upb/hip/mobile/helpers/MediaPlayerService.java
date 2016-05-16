package de.upb.hip.mobile.helpers;
import de.upb.hip.mobile.activities.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Lobner on 18.04.2016.
 */
public class MediaPlayerService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener{
    public static final String ACTION_PLAY = "de.upb.hip.mobile.PLAY";  //the intentions can probably be erased
    public static final String ACTION_STOP = "de.upb.hip.mobile.STOP";


    private int[] songList = {R.raw.audio_file_1embraceofsaturn, R.raw.audio_file_2gmanspeech,
            R.raw.audio_file_3gmanwise};
    int current = 1;
    private MediaPlayer mMediaPlayer;
    private IBinder mBinder = new MediaPlayerBinder();

    public void onCreate(){
        try{    //the service is bound, therefore this function is used rather than onStartCommand
            mMediaPlayer = MediaPlayer.create(this, songList[current]);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        try{
            mMediaPlayer = MediaPlayer.create(this, songList[current]);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
            System.out.println(e.getStackTrace());
        }

        return START_STICKY; //keeps the service running until told otherwise
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        //the media player will be prepared when starting an activity,
        // but that may not be the right time to start the audi
        //therefore this is empty
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onError(MediaPlayer mp, int what, int extra){
        System.out.println("what: " + what + "; extra: " + extra);

        return false;
    }

    //following are the functions for the app to use to control the media player
    public void startSound(){
        mMediaPlayer.start();
    }

    public void stopSound(){
        mMediaPlayer.stop();
    }

    public void pauseSound(){
        mMediaPlayer.pause();
    }

    public void changeAudioFile(){
        //as soon as the audio files are there, this needs to be changed
        //1. selection of audio file needs to be possible
        //2. load tracks from database
        try {
            mMediaPlayer.reset();
//            AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.audio_file_3gmanwise);
            current++;
            current %= songList.length;
            mMediaPlayer = MediaPlayer.create(this, songList[current]);
        }/*catch(IOException e){
            System.out.println(e.getStackTrace());
        }*/catch(Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }
}

