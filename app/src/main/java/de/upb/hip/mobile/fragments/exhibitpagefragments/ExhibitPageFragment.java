/*
 * Copyright (c) 2016 History in Paderborn App - Universität Paderborn
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

package de.upb.hip.mobile.fragments.exhibitpagefragments;

import android.net.Uri;
import android.support.v4.app.Fragment;

import de.upb.hip.mobile.helpers.BottomSheetConfig;


/**
 * Abstract superclass for Fragments that are displayed as pages in the ExhibitDetailsActivity.
 */
public abstract class ExhibitPageFragment extends Fragment {

    /** Available page types */
    public enum Type {
        APPETIZER,
        IMAGE,
        SLIDER,
        TEXT
    }

    /** Uri pointing to the audio file that should be played */
    private Uri audio = null;

    public Uri getAudio() {
        return audio;
    }

    public void setAudio(Uri audio) {
        this.audio = audio;
    }


    /** Returns the type of page */
    public abstract Type getType();

    /** Returns the BottomSheetConfig for the PageFragment */
    public abstract BottomSheetConfig getBottomSheetConfig();

}
