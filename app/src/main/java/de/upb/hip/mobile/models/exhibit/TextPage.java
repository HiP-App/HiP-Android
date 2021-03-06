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

import de.upb.hip.mobile.models.Audio;

/**
 * A model class representing a mText page
 */
public class TextPage extends Page implements Serializable {

    private final String mText;

    public TextPage(String mText, Audio audio) {
        super(audio);
        this.mText = mText;
        this.mAudio = mAudio;
    }

    public String getText() {
        return mText;
    }

}
