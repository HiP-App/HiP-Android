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

package de.upb.hip.mobile.fragments.bottomsheetfragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.upb.hip.mobile.activities.R;


/**
 * A simple {@link BottomSheetFragment} implementation which provides a fragment displaying
 * a title and a description.
 */
public class SimpleBottomSheetFragment extends BottomSheetFragment {

    /** Title displayed in the bottom sheet (should be ~30 characters long) */
    private String title = "default title";

    /** Description displayed in the bottom sheet */
    private String description = "default description";


    /** Default empty constructor */
    public SimpleBottomSheetFragment() {
        // Required empty public constructors
    }


    /**
     * Sets the title which is used in onCreateView to set the text of the corresponding TextView.
     *
     * @param title Title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description which is used in onCreateView to set the text of the corresponding
     * TextView.
     *
     * @param desc Description to set.
     */
    public void setDescription(String desc) {
        this.description = desc;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_simple, container, false);

        // set title and description
        TextView tv = (TextView) v.findViewById(R.id.tvBsTitle);
        tv.setText(title);
        tv = (TextView) v.findViewById(R.id.tvBsDescription);
        tv.setText(description);

        return v;
    }


    @Override
    public void onBottomSheetExpand() {
        super.onBottomSheetExpand();

        // TODO:  flash scrollbars of NestedScrollView if possible?
    }
}
