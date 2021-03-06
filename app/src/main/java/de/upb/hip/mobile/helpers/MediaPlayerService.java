/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.upb.hip.mobile.helpers;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.models.Audio;

//import android.content.res.AssetFileDescriptor;   //only use if necessary in the end
//                                                      (remains to be seen)

/**
 * Created by Lobner on 18.04.2016.
 */
public class MediaPlayerService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener{
			
	/**
	 *	This class controls playing audio files. As the audio needs to be playable regardless of the screen activity 
	 *	and also while the screen is off, this needs to be a service. The service should be started from one activity
	 *	and then be given to the other activities in which it is used, otherwise a good control is impossible.
	 */

    boolean mAudioFileIsSet = false;

    Audio a1 = new Audio(R.raw.intochaos); // this is only used as a dummy in the initialization

    private MediaPlayer mMediaPlayer;
    private IBinder mBinder = new MediaPlayerBinder();

    public void onCreate(){
        try{    //the service is bound, therefore this function is used rather than onStartCommand
            mMediaPlayer = MediaPlayer.create(this, a1.getAudioDir());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
//            add an exception handling
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        try{
//            mMediaPlayer = MediaPlayer.create(this, songList[current].getAudioDir());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
//            add an exception handling
        }

        return START_STICKY; //keeps the service running until told otherwise
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        //the media player will be prepared when starting an activity,
        // but that may not be the right time to start the audio
        //therefore this is empty
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onError(MediaPlayer mp, int what, int extra){
//            add an error handling

        return false;
    }

    //following are the functions for the app to use to control the media player
    public void startSound(){
        if(!mAudioFileIsSet){
            mMediaPlayer = MediaPlayer.create(this, a1.getAudioDir());
        }
        mMediaPlayer.start();
    }

    public void stopSound(){
        mMediaPlayer.stop();
    }

    public void pauseSound(){
        mMediaPlayer.pause();
    }

    /** sets a specific audio file*/
    public void setAudioFile(Audio audio){
        try {
            setToNewAudioFile(audio.getAudioDir());
        }catch(Exception e){
//            add an exception handling
        }
    }

    /** sets a specific audio file*/
    public void setAudioFile(int audio){
        try {
            setToNewAudioFile(audio);
        }catch(Exception e){
//            add an exception handling
        }
    }

    private void setToNewAudioFile(int audio){
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer = MediaPlayer.create(this, audio);
        mAudioFileIsSet = true;
    }


    public boolean getAudioFileIsSet(){
        //since the mediaplayer is used as a service, in the beginning it can't yet be called
        //as it is not yet created. however, as soon as the play button is used, the media player
        //surely is existend. the first time, it is pushed, there is not yet set an audiofile
        //because it couldn't be set before, so this needs to be checked and an audio file can then
        //be set if necessary
        return mAudioFileIsSet;
    }

    public long getTimeTotal(){
        return mMediaPlayer.getDuration();
    }

    public long getTimeCurrent(){
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int time){
        mMediaPlayer.seekTo(time);
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }
}

