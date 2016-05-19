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

package de.upb.hip.mobile.helpers;


import de.upb.hip.mobile.fragments.bottomsheetfragments.BottomSheetFragment;

/**
 * Contains information for the {@link de.upb.hip.mobile.activities.ExhibitDetailsActivity}
 * on how to display the bottom sheet.
 */
public class BottomSheetConfig {

    /** Describes the action the FAB should perform on click */
    public enum FabAction {
        NONE,
        EXPAND,
        COLLAPSE,
        NEXT
    }

    /** Indicates whether the bottom sheet should be displayed (true) or not (false) */
    private boolean displayBottomSheet = true;

    /** Fragment that is displayed in the bottom sheet */
    private BottomSheetFragment bottomSheetFragment;

    /** The maximum height of the bottom sheet (in dp) */
    private int maxHeight = 220;

    /** The height of the bottom sheet when it is collapsed (in dp) */
    private int peekHeight = 80;

    /* The action associated with the FAB */
    private FabAction fabAction = FabAction.EXPAND;


    // getters

    public boolean isDisplayBottomSheet() {
        return displayBottomSheet;
    }

    public BottomSheetFragment getBottomSheetFragment() {
        return bottomSheetFragment;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getPeekHeight() {
        return peekHeight;
    }

    public FabAction getFabAction() {
        return fabAction;
    }


    /** Builder class which eases the creation of BottomSheetConfigs. */
    public static class Builder {

        BottomSheetConfig config = new BottomSheetConfig();

        public Builder displayBottomSheet(boolean display) {
            config.displayBottomSheet = display;
            return this;
        }

        public Builder bottomSheetFragment(BottomSheetFragment fragment) {
            config.bottomSheetFragment = fragment;
            return this;
        }

        public Builder maxHeight(int height) {
            config.maxHeight = height;
            return this;
        }

        public Builder peekHeight(int height) {
            config.peekHeight = height;
            return this;
        }

        public Builder fabAction(FabAction action) {
            config.fabAction = action;
            return this;
        }

        public BottomSheetConfig getBottomSheetConfig() {
            return config;
        }
    }

}
