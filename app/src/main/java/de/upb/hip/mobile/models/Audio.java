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

package de.upb.hip.mobile.models;

//imports will be used, once the audio files will be read from the database

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * A model class representing an audio file
 */
public class Audio extends DBFile implements Serializable {

    String mDescription;
    String mTitle;
    private String mCaption;

    @JsonIgnore
    int mAudio;

    public Audio(int audio){
        super(0, "audio.mp3");
        mAudio = audio;
    }

    public Audio(int audio, String caption){
        super(0, "audio.mp3");
        mAudio = audio;
        mCaption = caption;
    }

//    public Audio(int docId, String filename, int audio){
//        super(docId, filename);
//        mAudio = audio;
//    }

//    public Audio(int docId, String filename) {
//        super(docId, filename);
//    }


//    public Audio(int docId, String filename, String caption) {
//        super(docId, filename);
//        this.mCaption = caption;
//    }

    public String getCaption() {
        return this.mCaption;
    }

    public void setCaption(String caption) {
        this.mCaption = caption;
    }

    //TODO: Add methods for getting this as a playable file from the database

    /**
     * Sets a dersialized drawable version of this image
     *
     * @param audio sets the int value for the audio file in R.raw.*
     */
    public void setAudio(int audio) {
        mAudio = audio;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getAudioDir(){
            //this is a temporary solution with "hardcoded" audio files
        return mAudio;
    }
}
