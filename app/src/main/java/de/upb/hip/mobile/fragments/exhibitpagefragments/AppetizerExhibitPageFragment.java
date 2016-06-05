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


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.models.Image;
import de.upb.hip.mobile.models.exhibit.AppetizerPage;
import de.upb.hip.mobile.models.exhibit.Page;


/**
 * A {@link ExhibitPageFragment} subclass for the {@link AppetizerPage}.
 */
public class AppetizerExhibitPageFragment extends ExhibitPageFragment {

    /** Title for the appetizer bottom sheet */
    private String appetizerTitle = "Default Appetizer Title";

    /** Stores the model instance for this page */
    private AppetizerPage page = null;

    /** Height of the Bottom Sheet in dp */
    private final int BOTTOM_SHEET_HEIGHT = 200;


    public AppetizerExhibitPageFragment() {
        // Required empty public constructor
    }


    public void setAppetizerTitle(String title) {
        this.appetizerTitle = title;
    }

    @Override
    public void setPage(Page page) {
        this.page = (AppetizerPage) page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exhibitpage_appetizer, container, false);

        // set image
        ImageView imgView = (ImageView) v.findViewById(R.id.imgAppetizer);
        if (imgView != null && page != null) {
            Image img = page.getImage();
            Drawable drawable = img.getDawableImage(getContext());
            imgView.setImageDrawable(drawable);
        }

        return v;
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        SimpleBottomSheetFragment bsFragment = new SimpleBottomSheetFragment();
        bsFragment.setTitle(appetizerTitle);
        if (page != null)
            bsFragment.setDescription(page.getText());

        return new BottomSheetConfig.Builder()
                .maxHeight(BOTTOM_SHEET_HEIGHT)
                .peekHeight(BOTTOM_SHEET_HEIGHT)
                .fabAction(BottomSheetConfig.FabAction.NEXT)
                .bottomSheetFragment(bsFragment)
                .getBottomSheetConfig();
    }

}
