package com.example.timo.hip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;


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

    /* numbers for database fields */
    public static final int COL_ROWID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_DESCRIPTION = 2;
    public static final int COL_LAT = 3;
    public static final int COL_LNG = 4;
    public static final int COL_CATEGORIES = 5;
    public static final int COL_TAGS = 6;

    public static final String DB_NAME = "exhibit"; // database name
    public static final String TAG = "DBAdapter"; // for logging

    // Context of application who uses us.
    private final Context context;

    private Manager manager = null;
    private Database database = null;


    /* Constructor */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        initDatabase();
    }


    /* initialize the database */
    public void initDatabase() {
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DB_NAME);
            database.delete();
            database = manager.getDatabase(DB_NAME);
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
            return;
        }

        /* test if database is empty, if it is, insert dummy data. ToDo: Remove this workaround, when the backend database synchronization works */
        Boolean db_is_empty = true;
        db_is_empty = database.getDocumentCount() == 0;
        if (db_is_empty) {
            insertRow(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583, "Kirche", "Dom");
            insertRow(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.", 51.706768, 8.771104, "Uni", "Universität");
            insertRow(3, "Heinz Nixdorf Institut", "Das Heinz Nixdorf Institut (HNI) ist ein interdisziplinäres Forschungsinstitut der Universität Paderborn.", 51.7292257, 8.7434972, "Uni", "HNI");
            insertRow(40, "Irgendwo in der Nähe des HNF", "Dies ist ein Testeintrag um sicherzustellen, dass wirklich die korrekten Datenbankeinträge verwendet werden.", 51.7292000, 8.7434000, "Test", "Test");
        }
    }


    /* insert a row in the database */
    public void insertRow(int id, String name, String description, double lat, double lng, String categories, String tags) {
        Document document = database.getDocument(String.valueOf(id)); // this creates a new entry but with predefined id
        Map<String, Object> properties = new HashMap<String, Object>();

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
        List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
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
