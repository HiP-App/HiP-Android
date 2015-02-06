package com.example.timo.hip;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.transition.Explode;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {

    private DBAdapter database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
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
