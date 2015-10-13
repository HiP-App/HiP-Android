package com.example.timo.hip;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.replicator.ReplicationState;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Class for the connection between app and database */
public class DBAdapter {


    /* field descriptions in database */
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";


    public static final String DB_NAME = "exhibits"; // local database name, which is synchronized with the server
    public static final String EXAMPLE_DB_NAME = "example_database"; // the local database with example data, that is used if the network is unreachable
    private Manager manager = null; // local database manager
    private static Database database = null; // local database
    private static final String COUCHBASE_SERVER_URL = "http://Couchbase-Server:4984/exhibits"; // URL to Server with running Couchbase Sync Gateway
    private static final String COUCHBASE_USER = "android_user"; // username to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_PASSWORD = "couchbase"; // password to access the data bucket on Couchbase Sync Gateway


    public static final String TAG = "DBAdapter"; // for logging
    private final Context context; // Context of application who uses us.


    /* Constructor */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        if (database==null) {
            initDatabase();
        }
    }


    /* Put some dummy data to the local database
     * ToDo: Remove this function for productive use! */
    public void initExampleDatabase() {
        try {
            switchDatabase(EXAMPLE_DB_NAME);
            database.delete(); // delete the database
            switchDatabase(EXAMPLE_DB_NAME); // set up a new databes
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error deleting local database", e);
            return;
        }

        /* insert text and images*/
        insertRow(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583, "Kirche", "Dom");
        addImage(R.drawable.dom, 1);
        insertRow(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.", 51.706768, 8.771104, "Uni", "Universität");
        addImage(R.drawable.uni, 2);
        insertRow(3, "Heinz Nixdorf Institut", "Das Heinz Nixdorf Institut (HNI) ist ein interdisziplinäres Forschungsinstitut der Universität Paderborn.", 51.7292257, 8.7434972, "Uni", "HNI");
        addImage(R.drawable.hnf, 3);
        insertRow(4, "Museum in der Kaiserpfalz", "Das Museum in der Kaiserpfalz befindet sich in Paderborn in unmittelbarer Nähe des Doms. Es stellt Funde aus karolingischer, ottonischer und sächsischer Zeit vor. Es befindet sich an der Stelle, an der man 1964 bei Bauarbeiten die Grundmauern der Pfalzanlage aus dem 8. Jahrhundert bzw. aus der Zeit Heinrichs II. gefunden hat. Sie sind Teil der heutigen Bausubstanz und lassen sich im Mauerwerk des Museums noch sehr gut nachvollziehen. Direkt neben dem heutigen Museum fand man 1964 auch die Kaiserpfalz Karl des Großen. Der Umriss dieser Anlage ist heute nur noch durch die rekonstruierten Grundmauern zu erkennen. Träger des Landesmuseums ist der Landschaftsverband Westfalen-Lippe. Das Gebäude gehört dem Metropolitankapitel und wird mietzinsfrei an den Träger des Museums vermietet", 51.719412, 8.755524, "Kirche, Museum", "");
        addImage(R.drawable.pfalz, 4);
        insertRow(5, "Abdinghofkirche", "Das Abdinghofkloster Sankt Peter und Paul ist eine ehemalige Abtei der Benediktiner in Paderborn, bestehend von seiner Gründung im Jahre 1015 bis zu seiner Säkularisation am 25. März 1803. In der Zeit seines Bestehens standen ihm insgesamt 51 Äbte vor. Kulturelle Bedeutung erlangte es durch seine Bibliothek, die angeschlossene Schule, ein Hospiz, seine Werkstatt für Buchmaler und Buchbinderei und wichtige Kirchenschätze. Zudem war das Kloster lange Zeit Grundbesitzer im Wesergebiet (so die Externsteine) und am Niederrhein bis in die Niederlande. Die Kirche ist heute eine evangelisch-lutherische Pfarrkirche.", 51.718725, 8.752889, "Kirche", "");
        addImage(R.drawable.abdinghof, 5);
        insertRow(6, "Busdorfkirche", "Die Busdorfkirche ist eine Kirche in Paderborn, die nach dem Vorbild der Grabeskirche in Jerusalem entstand. Das Stift Busdorf war ein 1036 gegründetes Kollegiatstift in Paderborn. Stift und Kirche lagen ursprünglich außerhalb der Stadt, wurden aber im 11./12. Jahrhundert im Zuge der Stadterweiterung in diese einbezogen.", 51.7186951, 8.7577606, "Kirche", "");
        addImage(R.drawable.busdorfkirche_aussen, 6);
        insertRow(7, "Liborikapelle", "Die spätbarocke, äußerlich unscheinbare Liborikapelle ist vor den Mauern der alten Stadt auf dem Liboriberg zu finden. Von weitem leuchtet der vergoldete Pfau als Wetterfahne auf dem Dachreiter. Ein Pfau als Zeichen für die Verehrung des hl. Liborius schmückt auch die Stirnseite über dem auf Säulen ruhenden Vordach. Inschriften zeigen Gebete und Lobsprüche für den Stadt- und Bistumsheiligen Liborius und geben Hinweis auf den Erbauer sowie auf das Erbauungsjahr 1730. Die Kapelle diente als Station auf der alljährlichen Libori-Prozession rund um die Stadt.", 51.715041, 8.754022, "Kirche", "");
        addImage(R.drawable.liboriuskapelle, 7);
        switchDatabase(DB_NAME); // switch back to productive database
    }


    /* adds an image from R.drawable to the document defined by document_id in local database */
    private void addImage(int image_number, int document_id) {
        InputStream image = context.getResources().openRawResource(+ image_number); // "+" is from: https://stackoverflow.com/questions/25572647/android-openrawresource-not-working-for-a-drawable
        Document doc = database.getDocument(String.valueOf(document_id));
        UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
        newRev.setAttachment("image.jpg", "image/jpeg", image);
        try {
            newRev.save();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error attaching image", e);
        }
    }


    /* notify the UI Thread that the database has changed */
    private synchronized void notifyExhibitSetChanged() {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) context).notifyExhibitSetChanged();
            }
        });
    }


    /* gets the (local) database, ensures the Singleton pattern */
    public Database getDatabaseInstance() throws CouchbaseLiteException, IOException {
        if (this.database == null) {
            this.database = getManagerInstance().getDatabase(DB_NAME);
        }
        return this.database;
    }


    /* gets the manager of the (local) database, ensures the Singleton pattern */
    public Manager getManagerInstance() throws IOException {
        if (this.manager == null) {
            this.manager = new Manager(new AndroidContext(this.context), Manager.DEFAULT_OPTIONS);
        }
        return this.manager;
    }


    /* switch to the (local) database, that is given by the String db_name
    * ToDo: Remove for productive use */
    private void switchDatabase(String db_name) {
        try {
            this.database = getManagerInstance().getDatabase(db_name);
        } catch (CouchbaseLiteException ex) {
            Log.e(TAG, "Error switching to example database", ex);
        } catch (IOException ex) {
            Log.e(TAG, "Error switching to example database", ex);
        }
    }


    /* initialize the database */
    private void initDatabase() {
        try {
            initExampleDatabase(); // set up example database

            /* set up a connection to Sync Gateway */
            URL url= new URL(COUCHBASE_SERVER_URL);

            Authenticator auth = AuthenticatorFactory.createBasicAuthenticator(COUCHBASE_USER, COUCHBASE_PASSWORD); // authenticate on the gateway

            Replication push = getDatabaseInstance().createPushReplication(url);
            Replication pull = getDatabaseInstance().createPullReplication(url);

            pull.setContinuous(true); // sync all changes, ToDo: Sync later only neccessary entries
            push.setContinuous(true); // sync all changes

            push.setAuthenticator(auth);
            pull.setAuthenticator(auth);

            pull.setCreateTarget(true); // creates the local database, if it doesn't exist

            Replication.ChangeListener changeListener = new Replication.ChangeListener() {
                @Override
                public void changed(Replication.ChangeEvent event) {
                    switchDatabase(DB_NAME);
                    if (database.getDocumentCount() == 0) {
                        switchDatabase(EXAMPLE_DB_NAME);
                    }
                    notifyExhibitSetChanged(); // updates the view everytime the database changes
                }
            };

            pull.addChangeListener(changeListener);
            push.addChangeListener(changeListener);

            pull.start();
            push.start();

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error getting database", e);
            return;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL", e);
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error getting database manager", e);
            return;
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
        properties.put("channels", "*"); // ensures the access for all users in the Couchbase database

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