package com.example.timo.hip;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ImageBoundariesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_boundaries);
        final Button button = (Button) this.findViewById(R.id.BoundaryButton);
        final ImageView test = (ImageView) this.findViewById(R.id.ImageViewBoundaries);
        final View stkilian = this.findViewById(R.id.StKilian);
        final TextView nametext= (TextView)this.findViewById(R.id.NameText);
        final TextView descriptiontext= (TextView)this.findViewById(R.id.DescriptionText);
        stkilian.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nametext.setPadding(148,0,0,0);
                        nametext.setText(R.string.StKilian);
                        descriptiontext.setText(R.string.StKilianText);
                    }
                }
        );
        final View maria = (View) this.findViewById(R.id.Maria);
        maria.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nametext.setPadding(320,0,0,0);
                        nametext.setText(R.string.Madonna);
                        descriptiontext.setText(R.string.MadonnaText);
                    }
                }
        );
        final View stliborius = this.findViewById(R.id.StLiborius);
        stliborius.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nametext.setPadding(748,0,0,0);
                        nametext.setText(R.string.StLiborius);
                        descriptiontext.setText(R.string.StLiboriusText);
                    }
                }
        );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.getText().equals(getString(R.string.show_boundaries))) {
                    stkilian.setBackgroundResource(R.drawable.oval);
                    maria.setBackgroundResource(R.drawable.oval);
                    stliborius.setBackgroundResource(R.drawable.oval);
                    button.setText(getString(R.string.turnoff_boundaries));
                } else {
                    // turn off boundaries
                    stkilian.setBackgroundResource(R.drawable.oval_gone);
                    maria.setBackgroundResource(R.drawable.oval_gone);
                    stliborius.setBackgroundResource(R.drawable.oval_gone);
                    button.setText(getString(R.string.show_boundaries));
                }

            }
        });
    }
}
