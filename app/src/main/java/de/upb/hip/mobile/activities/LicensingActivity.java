/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
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

package de.upb.hip.mobile.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * This activity shows information about the used 3rd party resources and their licenses
 */
public class LicensingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licensing);

        makeLinksClickable();

        //set up navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setUpNavigationDrawer(this, mDrawerLayout);
    }

    /**
     * Makes the URL links in the body of licensing information clickable so that they'll open
     * in Chrome when the user touches them
     */
    private void makeLinksClickable() {
        ((TextView) findViewById(R.id.licensingGoogleMaterialBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingOSMDroidBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingOSMBonusPackBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingCouchbaseBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingMapiconsBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingPhotoviewBody))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
