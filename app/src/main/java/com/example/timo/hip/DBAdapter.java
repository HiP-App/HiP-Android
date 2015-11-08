package com.example.timo.hip;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Database;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Revision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.support.CouchbaseLiteHttpClientFactory;
import com.couchbase.lite.support.PersistentCookieStore;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ssl.SSLSocketFactory;


/* Class for the connection between app and database */
public class DBAdapter {


    /* field descriptions in database */
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";


    public static final String DB_NAME = "hip"; // local database name
    private Manager manager = null; // local database manager
    private static Database database = null; // local database
    private static final String COUCHBASE_SERVER_URL = "https://couchbase-hip.cs.upb.de:4984/hip"; // URL to Server with running Couchbase Sync Gateway
    private static final String COUCHBASE_USER = "android_user"; // username to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_PASSWORD = "5eG410KF2fnPSnS0"; // password to access the data bucket on Couchbase Sync Gateway


    public static final String TAG = "DBAdapter"; // for logging
    private final Context context; // Context of application who uses us.


    /* Constructor */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        if (database == null) {
            initDatabase(false);
            //insertDummyDataToDatabase(); // uncomment this line to set up the gateway database with new dummy data
        }
    }


    /* Put some dummy data to the database
     * Call this function manual, if you need to reset the database. IMPORTANT: This data will replicated to the life database, so be sure what you are doing!
     * ToDo: Remove this function for productive use! */
    public void insertDummyDataToDatabase() {
        try {
            database.delete();
            database = null;
            initDatabase(true);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error deleting local database", e);
            return;
        }

        /* insert text and images*/
        insertExhibit(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583, "Kirche", "Dom");
        addImage(R.drawable.dom, 1);
        insertExhibit(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.", 51.706768, 8.771104, "Uni", "Universität");
        addImage(R.drawable.uni, 2);
        insertExhibit(3, "Heinz Nixdorf Institut", "Das Heinz Nixdorf Institut (HNI) ist ein interdisziplinäres Forschungsinstitut der Universität Paderborn.", 51.7292257, 8.7434972, "Uni", "HNI");
        addImage(R.drawable.hnf, 3);
        insertExhibit(4, "Museum in der Kaiserpfalz", "Das Museum in der Kaiserpfalz befindet sich in Paderborn in unmittelbarer Nähe des Doms. Es stellt Funde aus karolingischer, ottonischer und sächsischer Zeit vor. Es befindet sich an der Stelle, an der man 1964 bei Bauarbeiten die Grundmauern der Pfalzanlage aus dem 8. Jahrhundert bzw. aus der Zeit Heinrichs II. gefunden hat. Sie sind Teil der heutigen Bausubstanz und lassen sich im Mauerwerk des Museums noch sehr gut nachvollziehen. Direkt neben dem heutigen Museum fand man 1964 auch die Kaiserpfalz Karl des Großen. Der Umriss dieser Anlage ist heute nur noch durch die rekonstruierten Grundmauern zu erkennen. Träger des Landesmuseums ist der Landschaftsverband Westfalen-Lippe. Das Gebäude gehört dem Metropolitankapitel und wird mietzinsfrei an den Träger des Museums vermietet", 51.719412, 8.755524, "Kirche, Museum", "");
        addImage(R.drawable.pfalz, 4);
        insertExhibit(5, "Abdinghofkirche", "Das Abdinghofkloster Sankt Peter und Paul ist eine ehemalige Abtei der Benediktiner in Paderborn, bestehend von seiner Gründung im Jahre 1015 bis zu seiner Säkularisation am 25. März 1803. In der Zeit seines Bestehens standen ihm insgesamt 51 Äbte vor. Kulturelle Bedeutung erlangte es durch seine Bibliothek, die angeschlossene Schule, ein Hospiz, seine Werkstatt für Buchmaler und Buchbinderei und wichtige Kirchenschätze. Zudem war das Kloster lange Zeit Grundbesitzer im Wesergebiet (so die Externsteine) und am Niederrhein bis in die Niederlande. Die Kirche ist heute eine evangelisch-lutherische Pfarrkirche.", 51.718725, 8.752889, "Kirche", "");
        addImage(R.drawable.abdinghof, 5);
        insertExhibit(6, "Busdorfkirche", "Die Busdorfkirche ist eine Kirche in Paderborn, die nach dem Vorbild der Grabeskirche in Jerusalem entstand. Das Stift Busdorf war ein 1036 gegründetes Kollegiatstift in Paderborn. Stift und Kirche lagen ursprünglich außerhalb der Stadt, wurden aber im 11./12. Jahrhundert im Zuge der Stadterweiterung in diese einbezogen.", 51.7186951, 8.7577606, "Kirche", "");
        addImage(R.drawable.busdorfkirche_aussen, 6);
        insertExhibit(7, "Liborikapelle", "Die spätbarocke, äußerlich unscheinbare Liborikapelle ist vor den Mauern der alten Stadt auf dem Liboriberg zu finden. Von weitem leuchtet der vergoldete Pfau als Wetterfahne auf dem Dachreiter. Ein Pfau als Zeichen für die Verehrung des hl. Liborius schmückt auch die Stirnseite über dem auf Säulen ruhenden Vordach. Inschriften zeigen Gebete und Lobsprüche für den Stadt- und Bistumsheiligen Liborius und geben Hinweis auf den Erbauer sowie auf das Erbauungsjahr 1730. Die Kapelle diente als Station auf der alljährlichen Libori-Prozession rund um die Stadt.", 51.715041, 8.754022, "Kirche", "");
        addImage(R.drawable.liboriuskapelle, 7);

        LinkedList<Waypoint> waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(new LatLng(51.715606, 8.746552), -1));
        waypoints.add(new Waypoint(new LatLng(51.718178, 8.747164), -1));
        waypoints.add(new Waypoint(new LatLng(51.722850, 8.750780), -1));
        waypoints.add(new Waypoint(new LatLng(51.722710, 8.758365), -1));
        waypoints.add(new Waypoint(new LatLng(51.718789, 8.762699), -1));
        waypoints.add(new Waypoint(new LatLng(51.715745, 8.757796), -1));
        waypoints.add(new Waypoint(new LatLng(51.715207, 8.752142), 7));
        waypoints.add(new Waypoint(new LatLng(51.715606, 8.746552), -1));
        insertRoute(101, "Ringroute", "Dies ist ein einfacher Rundweg rund um den Ring.", waypoints);

        waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(new LatLng(51.718590, 8.752206), 5));
        waypoints.add(new Waypoint(new LatLng(51.719128, 8.755457), 1));
        waypoints.add(new Waypoint(new LatLng(51.719527, 8.755736), 4));
        waypoints.add(new Waypoint(new LatLng(51.718969, 8.758472), 6));
        waypoints.add(new Waypoint(new LatLng(51.720371, 8.761723), -1));
        waypoints.add(new Waypoint(new LatLng(51.719454, 8.767484), -1));
        insertRoute(102, "Stadtroute", "Dies ist eine kurze Route in der Stadt.", waypoints);
    }


    /* adds an image from R.drawable to the document defined by document_id in local database */
    private void addImage(int image_number, int document_id) {
        InputStream image = context.getResources().openRawResource(+image_number); // "+" is from: https://stackoverflow.com/questions/25572647/android-openrawresource-not-working-for-a-drawable
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
        if(((Activity) context).getClass().getSimpleName() != "MainActivity") return;

        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) context).notifyExhibitSetChanged();
            }
        });
    }


    /* gets the (local) database, ensures the Singleton pattern */
    public Database getDatabaseInstance() {
        if (this.database == null) {
            try {
                this.database = getManagerInstance().getDatabase(DB_NAME);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    /* initialize the database, the flag enablePush indicates if local changes should be pushed to the gateway */
    private void initDatabase(Boolean enablePush) {
        try {
            setHttpClientFactory(); // sets the HTTP Factory to connect over SSL to the gateway

            /* set up a connection to Sync Gateway */
            URL url = new URL(COUCHBASE_SERVER_URL);

            Authenticator auth = AuthenticatorFactory.createBasicAuthenticator(COUCHBASE_USER, COUCHBASE_PASSWORD); // authenticate on the gateway

            Replication.ChangeListener changeListener = new Replication.ChangeListener() {
                @Override
                public void changed(Replication.ChangeEvent event) {
                    notifyExhibitSetChanged(); // updates the view everytime the database changes
                }
            };

            Replication pull = getDatabaseInstance().createPullReplication(url);

            pull.setContinuous(true); // sync all changes, ToDo: Sync later only neccessary entries
            pull.setAuthenticator(auth);
            pull.setCreateTarget(true); // creates the local database, if it doesn't exist
            pull.addChangeListener(changeListener);
            pull.start();

            if (enablePush) {
                /* all changes should be pushed to the database */
                Replication push = getDatabaseInstance().createPushReplication(url);
                push.setContinuous(true); // sync all changes
                push.setAuthenticator(auth);
                push.addChangeListener(changeListener);
                push.start();
            }

            /* initialize local views */
            View exhibitsView = getDatabaseInstance().getView("exhibits"); // view for exhibits
            exhibitsView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if (document.get("type").equals("exhibit")) {
                        emitter.emit(document.get("id"), null);
                    }
                }
            }, "1");


            View routeView = getDatabaseInstance().getView("routes"); // view for routes
            routeView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if (document.get("type").equals("route")) {
                        emitter.emit(document.get("id"), null);
                    }
                }
            }, "1");


        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL", e);
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error getting database manager", e);
            return;
        }
    }


    /* Gets the key store with the (self signed) trusted certificates to the gateway */
    private KeyStore getKeyStore() {
        KeyStore keystore = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in = this.context.getResources().openRawResource(R.raw.ssl); // get the public certificate
            Certificate ca;

            try {
                ca = cf.generateCertificate(in);
            } finally {
                in.close();
            }

            /* Setting default values for the keystore */
            String keyStoreType = KeyStore.getDefaultType();
            keystore = KeyStore.getInstance(keyStoreType);
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keystore;
    }


    /* sets the Couchbase Lite Manager HTTP Factory with the keystore returned by getKeystore() */
    private void setHttpClientFactory() {
        PersistentCookieStore cookieStore = null;

        cookieStore = getDatabaseInstance().getPersistentCookieStore(); // get Cookie Store

        CouchbaseLiteHttpClientFactory cblHttpClientFactory = new CouchbaseLiteHttpClientFactory(cookieStore); // get Factory

        try {
            cblHttpClientFactory.setSSLSocketFactory(new SSLSocketFactory(getKeyStore())); // sets the keystore
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        // set CouchbaseHttpClientFactory to Manager
        manager.setDefaultHttpClientFactory(cblHttpClientFactory);
    }


    /* insert a exhibit in the database */
    public void insertExhibit(int id, String name, String description, double lat, double lng, String categories, String tags) {
        Document document = database.getDocument(String.valueOf(id)); // this creates a new entry but with predefined id
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", "exhibit");
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


    /* insert a route in the database */
    public void insertRoute(int id, String title, String description, LinkedList<Waypoint> waypoints) {
        Document document = database.getDocument(String.valueOf(id)); // this creates a new entry but with predefined id
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", "route");
        properties.put("title", title);
        properties.put("description", description);
        properties.put("waypoints", waypoints);
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


    /* gets all rows from a view from the database */
    public List<Map<String, Object>> getView(String view_name) {

        List<Map<String, Object>> result = new ArrayList<>();
        try {
             /* get the view */
            View view = getDatabaseInstance().getView(view_name);
            view.updateIndex();

            Query query = view.createQuery();

            QueryEnumerator enumerator = query.run();

            while (enumerator.hasNext()) { // add all documents to result
                QueryRow row = enumerator.next();

                Map<String, Object> properties = getDatabaseInstance().getDocument(row.getDocumentId()).getProperties();

                result.add(properties);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return result;
    }


    /* gets one row (specified by id) from database */
    public Document getDocument(int id) {
        return getDatabaseInstance().getDocument(String.valueOf(id));
    }


}