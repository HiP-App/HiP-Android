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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Activity Class for the splash screen, that is shown when the app it started, including the apps
 * logo.
 */
public class SplashScreenActivity extends Activity {

    private final int mStartup_delay = 2000;
    private TextView mTextAction;
    private TextView mTextWaiting;
    private ProgressBar mProgressBar;
    private Button mButtonRetry;
    private DBAdapter mDbAdapter;

    /**
     * Called when the activity is created.
     * If the database can be reached and is not empty, the MainActivity will be started with a
     * startup delay. Otherwise the a retry button and an error message is shown.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mTextAction = (TextView) findViewById(R.id.splashScreenActionText);
        mTextWaiting = (TextView) findViewById(R.id.splashScreenWaitingText);
        mProgressBar = (ProgressBar) findViewById(R.id.splashScreenProgressBar);
        mButtonRetry = (Button) findViewById(R.id.splashScreenRetryButton);

        mTextAction.setText(getString(R.string.splash_screen_loading));
        mTextWaiting.setText(getString(R.string.splash_screen_waiting));

        mDbAdapter = new DBAdapter(this);
        int i = mDbAdapter.getDocumentCount();
        if (i == 0) {
            onlineCheck();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startMainActivity();
                }
            }, mStartup_delay);

        }
    }

    /**
     * If the database is reachable, check if there are entries in the database and start the
     * MainActivity. Otherwise show no connection message.
     */
    private void onlineCheck() {
        if (isOnline()) {
            int i = mDbAdapter.getDocumentCount();
            while (i == 0) {
                i = mDbAdapter.getDocumentCount();
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startMainActivity();
                }
            }, mStartup_delay);

        } else {
            mTextAction.setText(getString(R.string.splash_screen_no_connection));
            mTextWaiting.setText(getString(R.string.splash_screen_no_connection_error_message));
            mProgressBar.setVisibility(View.GONE);
            mButtonRetry.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if device is connected to the internet.
     *
     * @return Returns true if device is connected to the internet.
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Starts the MainActivity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method bound to the button. Starts the check for connection again.
     *
     * @param v View
     */
    public void onClick_splashScreenRetryButton(View v) {
        mTextAction.setText(getString(R.string.splash_screen_check_connection));
        mTextWaiting.setText(getString(R.string.splash_screen_waiting));
        mProgressBar.setVisibility(View.VISIBLE);
        mButtonRetry.setVisibility(View.GONE);
        onlineCheck();
    }
}
