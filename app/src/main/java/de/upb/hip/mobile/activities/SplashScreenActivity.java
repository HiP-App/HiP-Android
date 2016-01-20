package de.upb.hip.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Created by Timo on 10.01.2016.
 */
public class SplashScreenActivity extends Activity {

    private TextView txtAction;
    private TextView txtWaiting;
    private ProgressBar progBar;
    private Button btnRetry;
    DBAdapter dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txtAction = (TextView) findViewById(R.id.textView_action);
        txtWaiting = (TextView) findViewById(R.id.textView_waiting);
        progBar = (ProgressBar) findViewById(R.id.progressBar1);
        btnRetry = (Button) findViewById(R.id.button_retry);

        //Check If Internet Connection

        dba = new DBAdapter(this);
        if(dba.getDocumentCount() > 0)
        {
            startMainActivity();
        } else {
            onlineCheck();
        }

    }

    private void onlineCheck() {
        if(isOnline())
        {
            // dba.COUCHBASE_SERVER_URL;


            // DOMAIN CHECK

                //DOMAIN NICHT DA

                // Database not reachable
            startMainActivity();
        } else
        {

            txtAction.setText(getString(R.string.splash_screen_no_connection));
            txtWaiting.setText(getString(R.string.splash_screen_no_connection_error_message));
            txtWaiting.setGravity(Gravity.CENTER);
            progBar.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onclick_retry(View v)
    {
        txtAction.setText(getString(R.string.splash_screen_check_connection));
        txtWaiting.setText(getString(R.string.splash_screen_waiting));
        progBar.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);
        onlineCheck();
    }
}
