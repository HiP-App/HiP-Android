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
import android.widget.ImageView;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.models.exhibit.ImagePage;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A fragment for displaying an image with selectable areas
 */
public class ImagePageExhibitFragment extends ExhibitPageFragment {

    private ImagePage page;

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exhibitpage_image, container, false);

        imageView = (ImageView) v.findViewById(R.id.fragment_exhibitpage_image_imageview);
        imageView.setImageDrawable(this.page.getImage().getDawableImage(getContext()));

        return v;
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        SimpleBottomSheetFragment bottomSheetFragment = new SimpleBottomSheetFragment();
        bottomSheetFragment.setTitle(page.getImage().getTitle());
        bottomSheetFragment.setDescription(page.getImage().getDescription());
        return new BottomSheetConfig.Builder().displayBottomSheet(true)
                .bottomSheetFragment(bottomSheetFragment).getBottomSheetConfig();
    }

    @Override
    public void setPage(Page page) {
        this.page = (ImagePage) page;
    }
}
