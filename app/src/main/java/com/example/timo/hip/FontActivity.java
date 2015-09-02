package com.example.timo.hip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by az on 24.08.15.
 */
public class FontActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(FontActivity.this, TimeLineActivity.class);
        intent.putExtra("SHOW_FONT", true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {super.onResume();}

    @Override
    protected void onDestroy() {super.onDestroy();}

}

