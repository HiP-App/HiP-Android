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

import de.upb.hip.mobile.models.exhibit.AppetizerPage;
import de.upb.hip.mobile.models.exhibit.Page;
import de.upb.hip.mobile.models.exhibit.TextPage;

/**
 * Used for the instantiation of ExhibitPageFragments.
 */
public class ExhibitPageFragmentFactory {

    /**
     * Creates an ExhibitPageFragment for the specified Page.
     *
     * @param page Page to create an ExhibitPageFragment for.
     * @return the created ExhibitPageFragment.
     */
    public static ExhibitPageFragment getFragmentForExhibitPage(Page page, String exhibitName) {

        // TODO: update this when new pages are available
        if (page instanceof AppetizerPage) {
            AppetizerPage appetizerPage = (AppetizerPage) page;
            AppetizerExhibitPageFragment fragment = new AppetizerExhibitPageFragment();
            fragment.setPage(appetizerPage);
            if (exhibitName != null)
                fragment.setAppetizerTitle(exhibitName);
            return fragment;
        } else if (page instanceof TextPage) {
            TextPage textPage = (TextPage) page;
            TextExhibitPageFragment fragment = new TextExhibitPageFragment();
            fragment.setPage(textPage);
            return fragment;
        } else
            return new DummyExhibitPageFragment();
    }

}