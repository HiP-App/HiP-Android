package com.example.timo.hip;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Outline;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {

    private DBAdapter database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fab.setOutlineProvider(viewOutlineProvider);

        openDatabase();

        int id = getIntent().getIntExtra("exhibit-id", 0);

        Cursor cursor = database.getRow(id);
        Exhibit exhibit = new Exhibit(cursor);
        cursor.close();

        ImageView imageView = (ImageView) findViewById(R.id.imageViewDetail);

        imageView.getWidth();
        int loader = R.drawable.loader;
        String image_url = "http://tboegeholz.de/ba/pictures/" + exhibit.id + ".jpg";
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.DisplayImage(image_url, loader, imageView);

        TextView txtName = (TextView) findViewById(R.id.txtName);
        txtName.setText(exhibit.name);

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtDescription.setText(exhibit.description);

//        ImageView image = (ImageView) findViewById(R.id.imageView);
//        image.setImageResource(R.drawable.dom);

        //getWindow().setEnterTransition(new Explode());

        //Toast.makeText(this, "Test Toast", Toast.LENGTH_LONG).show();

    }

    public void onClick_back(View view){
        Log.i("Click", "BACK!");
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();
    }

    private void openDatabase() {
        database = new DBAdapter(this);
        database.open();
    }

    private void closeDatabase() {
        database.close();
    }
}
