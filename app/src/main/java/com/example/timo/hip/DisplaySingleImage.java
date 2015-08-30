package com.example.timo.hip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class DisplaySingleImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_image);
        Button button = (Button) this.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
