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
import android.widget.TextView;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.models.exhibit.Page;
import de.upb.hip.mobile.models.exhibit.TextPage;

/**
 * A {@link ExhibitPageFragment} subclass for the {@link TextPage}.
 */
public class TextExhibitPageFragment extends ExhibitPageFragment {

    private TextPage page = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exhibitpage_text, container, false);

        TextView textView = (TextView) v.findViewById(R.id.tvText);
        textView.setText(page.getText());

        return v;
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        return new BottomSheetConfig.Builder().displayBottomSheet(false).getBottomSheetConfig();
    }

    @Override
    public void setPage(Page page) {
        if (page instanceof TextPage)
            this.page = (TextPage) page;
        else
            throw new IllegalArgumentException("Page has to be an instance of TextPage!");
    }
}
