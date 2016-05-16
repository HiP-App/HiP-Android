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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppetizerExhibitPageFragment extends ExhibitPageFragment {

    /** Title for the appetizer bottom sheet */
    private String appetizerTitle = "default appetizer title";

    /** Appetizer text displayed in the bottom sheet */
    private String appetizerText = "default appetizer text";


    public AppetizerExhibitPageFragment() {
        // Required empty public constructor
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
        bsFragment.setTitle(appetizerTitle);
        bsFragment.setDescription(appetizerText);

        return new BottomSheetConfig.Builder()
                .maxHeight(200)
                .peekHeight(200)
                .fabAction(BottomSheetConfig.FabAction.NEXT)
                .bottomSheetFragment(bsFragment)
                .getBottomSheetConfig();
    }

    @Override
    public Type getType() {
        return Type.APPETIZER;
    }
}
