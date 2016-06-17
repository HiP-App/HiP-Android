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

package de.upb.hip.mobile.models.exhibit;

import java.io.Serializable;
import java.util.List;

import de.upb.hip.mobile.models.Audio;
import de.upb.hip.mobile.models.Image;

/**
 * A model class for the TimeSliderPage page
 */
public class TimeSliderPage extends Page implements Serializable {
    private final String mTitle;
    private final String mText;
    private final List<Long> mDates;
    private final List<Image> mImages;
    private final boolean hideYearNumbers;

    public TimeSliderPage(String mTitle, String mText, Audio mAudio, List<Long> mDates, List<Image> mImages, boolean mHideYearNumbers) {
        super(mAudio);
        this.mTitle = mTitle;
        this.mText = mText;
        this.mAudio = mAudio;
        this.mDates = mDates;
        this.mImages = mImages;
        this.hideYearNumbers = mHideYearNumbers;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getText() {
        return mText;
    }

    public List<Long> getDates() {
        return mDates;
    }

    public List<Image> getImages() {
        return mImages;
    }

    public boolean isHideYearNumbers() {
        return hideYearNumbers;
    }
    
}
