package com.example.timo.hip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RouteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(RouteActivity.this, MainActivity.class);
        intent.putExtra("SHOW_ROUTE", true);
        startActivity(intent);
        finish();
    }
}
