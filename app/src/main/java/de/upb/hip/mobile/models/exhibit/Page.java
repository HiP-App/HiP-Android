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
 * An interface for all model classes for an Exhibit Page
 */
public abstract class Page implements Serializable {

    protected Audio mAudio = null;

    public Page(Audio audio) {
        this.mAudio = audio;
    }

    public Audio getAudio() {
        return this.mAudio;
    }

    public void setAudio(Audio audio) {
        this.mAudio = audio;
    }
}
