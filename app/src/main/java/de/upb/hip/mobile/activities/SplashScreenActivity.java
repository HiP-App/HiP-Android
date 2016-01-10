package de.upb.hip.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Timo on 10.01.2016.
 */
public class SplashScreenActivity extends Activity {

    private TextView txtAction;
    private TextView txtWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txtAction = (TextView) findViewById(R.id.textView_action);
        txtWaiting = (TextView) findViewById(R.id.textView_waiting);

        //Check If Internet Connection
        if(isOnline())
        {
            // Yes: Sync Database

                // Show Waiting Bar

                // When Finished: Start Main Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else
        {
            // No: Show Error Messeage
            txtAction.setText("Keine Internetverbindung.");
            txtWaiting.setText("Bitte stellen Sie sicher, dass Sie über eine aktive Internetverbindung verfügen.");

        }
        // In Case of Error: Return Error
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
