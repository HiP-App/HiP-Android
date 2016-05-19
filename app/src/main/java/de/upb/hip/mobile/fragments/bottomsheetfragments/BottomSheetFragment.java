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

import android.support.v4.app.Fragment;

/**
 * Abstract class for fragments that are included in the bottom sheet of
 * {@link de.upb.hip.mobile.activities.ExhibitDetailsActivity}.
 */
public abstract class BottomSheetFragment extends Fragment {

    /**
     * Called by ExhibitDetailsActivity when the bottom sheet has been expanded.
     * Subclasses should override this method if they require individual behaviour.
     */
    public void onBottomSheetExpand() {
        // intentionally left blank
    }

    /**
     * Called by ExhibitDetailsActivity when the bottom sheet has been collapsed.
     * Subclasses should override this method if they require individual behaviour.
     */
    public void onBottomSheetCollapse() {
        // intentionally left blank
    }

}
