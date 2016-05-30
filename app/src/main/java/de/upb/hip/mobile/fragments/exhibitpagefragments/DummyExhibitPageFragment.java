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


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A simple {@link ExhibitPageFragment} subclass for testing purposes.
 */
public class DummyExhibitPageFragment extends ExhibitPageFragment {

    // for testing purposes
    static int count = 0;


    public DummyExhibitPageFragment() {
        // Required empty public constructor

        count++;
    }

    @Override
    public void setPage(Page page) {
        // intentionally left blank for this dummy implementation
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exhibitpage_dummy, container, false);
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        SimpleBottomSheetFragment bsFragment = new SimpleBottomSheetFragment();
//        bsFragment.setTitle("SimpleBottomSheetFragment #" + count);
        bsFragment.setTitle("Außenansicht");
        bsFragment.setDescription(
                "Beschreibung zur Außenansicht (ist eigentlich der Abdinghof...). \n\n" +
                "(c) XYZ   \n\nyou cannot use getString(id) here because the fragment is not " +
                        "yet attached to an Activity!...");

        return new BottomSheetConfig.Builder()
                .bottomSheetFragment(bsFragment)
                .getBottomSheetConfig();
    }

}
