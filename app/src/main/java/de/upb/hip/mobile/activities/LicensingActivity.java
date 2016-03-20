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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicensingActivity extends ActionBarActivity {

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licensing);

        makeLinksClickable();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.licensing_title);
        }
    }

    private void makeLinksClickable() {
        ((TextView) findViewById(R.id.licensingGoogleMaterialBody)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingOSMDroidBody)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.licensingOSMBonusPackBody)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * Ensures tat the back button on the action bar works properly
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
