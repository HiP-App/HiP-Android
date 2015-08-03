package com.example.timo.hip;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Revision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Class for the connection between app and database */
public class DBAdapter {

    /* field descriptions in database */
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";

    public static final String DB_NAME = "exhibit"; // database name
    public static final String TAG = "DBAdapter"; // for logging

    // Context of application who uses us.
    private final Context context;

    private Manager manager = null;
    private static Database database = null;


    /* Constructor */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        if (database==null) {
            initDatabase();
        }
    }


    /* initialize the database */
    public void initDatabase() {
        /* Get the database and the manager */
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DB_NAME);
            database.delete(); // ToDo: Just for testing purposes, remove if synchronization with real DB is working!
            database = manager.getDatabase(DB_NAME); // ToDo: Just for testing purposes, remove if synchronization with real DB is working!
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error getting database", e);
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error getting database", e);
            return;
        }

        /* test if database is empty, if it is, insert dummy data. ToDo: Remove this workaround, when the backend database synchronization works */
        Boolean db_is_empty;
        db_is_empty = database.getDocumentCount() == 0;
        if (db_is_empty) {

            /* insert text */
            insertRow(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583, "Kirche", "Dom");
            insertRow(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.", 51.706768, 8.771104, "Uni", "Universität");
            insertRow(3, "Heinz Nixdorf Institut", "Das Heinz Nixdorf Institut (HNI) ist ein interdisziplinäres Forschungsinstitut der Universität Paderborn.", 51.7292257, 8.7434972, "Uni", "HNI");
            insertRow(4, "Irgendwo in der Nähe des HNF", "Dies ist ein Testeintrag um sicherzustellen, dass wirklich die korrekten Datenbankeinträge verwendet werden.", 51.7292000, 8.7434000, "Test", "Test");

            /* insert images */
            for (int i=1; i<=database.getDocumentCount(); i++) {
                InputStream image = context.getResources().openRawResource(+ R.drawable.databasetest); // "+" is from: https://stackoverflow.com/questions/25572647/android-openrawresource-not-working-for-a-drawable
                Document doc = database.getDocument(String.valueOf(i));
                UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
                newRev.setAttachment("image.jpg", "image/jpeg", image);
                try {
                    newRev.save();
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error attaching image", e);
                }
            }
        }
    }


    /* insert a row in the database */
    public void insertRow(int id, String name, String description, double lat, double lng, String categories, String tags) {
        Document document = database.getDocument(String.valueOf(id)); // this creates a new entry but with predefined id
        Map<String, Object> properties = new HashMap<>();

        properties.put("name", name);
        properties.put("description", description);
        properties.put("categories", categories);
        properties.put("tags", tags);
        properties.put("lat", lat);
        properties.put("lng", lng);

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

    }


    /* returns an image from the database */
    public static Drawable getImage(int id) {
        Document doc = database.getDocument(String.valueOf(id));
        Revision rev = doc.getCurrentRevision();
        Attachment att = rev.getAttachment("image.jpg");
        Drawable d = null;
        if (att != null) {
            try {
                InputStream is = att.getContent();
                d = Drawable.createFromStream(is, "image.jpg");
                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
        return d;
    }


    /* gets all rows from the database */
    public List<Map<String, Object>> getAllRows() {
        Query query = database.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        QueryEnumerator enumerator = null;
        try {
            enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error getting all rows", e);
        }
        List<Map<String,Object>> result = new ArrayList<>();
        while (enumerator.hasNext()) {
            QueryRow row = enumerator.next();
            Map<String, Object> properties = database.getDocument(row.getDocumentId()).getProperties();
            result.add(properties);
        }
        return result;
    }


    /* gets one row (specified by id) from database */
    public Document getRow(int id) {
        return database.getDocument(String.valueOf(id));
    }

}
