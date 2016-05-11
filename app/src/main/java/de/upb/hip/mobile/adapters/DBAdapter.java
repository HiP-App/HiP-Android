/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.upb.hip.mobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
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

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.SliderImage;
import de.upb.hip.mobile.models.Waypoint;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.ExhibitSerializer;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * Class for the connection between app and database
 */
public class DBAdapter {


    /* field descriptions in database */
    public static final String KEY_TYPE = "type";
    public static final String KEY_CHANNELS = "channels";
    public static final String KEY_ID = "_id";

    public static final String KEY_EXHIBIT_NAME = "name";
    public static final String KEY_EXHIBIT_DESCRIPTION = "description";
    public static final String KEY_EXHIBIT_LAT = "lat";
    public static final String KEY_EXHIBIT_LNG = "lng";
    public static final String KEY_EXHIBIT_CATEGORIES = "categories";
    public static final String KEY_EXHIBIT_TAGS = "tags";
    public static final String KEY_EXHIBIT_IMAGE = "image";
    public static final String KEY_EXHIBIT_PAGES = "pages";

    public static final String KEY_SLIDER_IMAGES = "sliderImages";
    public static final String KEY_SLIDER_IMAGE_NAME = "imageName";
    public static final String KEY_SLIDER_IMAGE_YEAR = "year";

    public static final String KEY_ROUTE_TITLE = "title";
    public static final String KEY_ROUTE_DESCRIPTION = "description";
    public static final String KEY_ROUTE_WAYPOINTS = "waypoints";
    public static final String KEY_ROUTE_DURATION = "duration";
    public static final String KEY_ROUTE_DISTANCE = "distance";
    public static final String KEY_ROUTE_TAGS = "tags";
    public static final String KEY_ROUTE_IMAGE_NAME = "imageName";


    public static final String DB_NAME = "hip"; // local database name
    // URL to Server with running Couchbase Sync Gateway
    public static final String COUCHBASE_SERVER_URL = "https://couchbase-hip.cs.upb.de:4984/hip";
    public static final String TAG = "DBAdapter"; // for logging
    // username to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_USER = "android_user";
    // password to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_PASSWORD = "5eG410KF2fnPSnS0";
    private static Database mDatabase = null; // local database
    private static Context sContext; // static context for static getImage()-method
    private final Context mContext; // Context of application who uses us.
    private Manager mManager = null; // local database manager

    /**
     * Constructor
     */
    public DBAdapter(Context ctx) {
        mContext = ctx;
        sContext = ctx;
        if (mDatabase == null) {
            initDatabase(true);
            // uncomment this line to set up the gateway database with new dummy data
            insertDummyDataToDatabase();
        }
    }

    public static Attachment getAttachment(int documentId, String filename) {
        Document doc = mDatabase.getDocument(String.valueOf(documentId));
        Revision rev = doc.getCurrentRevision();
        Attachment attachment = rev.getAttachment(filename);
        return attachment;
    }

    /**
     * returns an image from the database
     */
    public static Drawable getImage(int id, String imageName) {
        Attachment att = getAttachment(id, imageName);
        Drawable d = null;
        if (att != null) {
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                InputStream is = att.getContent();
                Bitmap b = BitmapFactory.decodeStream(is);
                int width = b.getWidth();
                int height = b.getHeight();

                WindowManager wm = (WindowManager) sContext.getSystemService(
                        Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screen_width = size.x;
                int screen_height = size.y;

                // check if greater than device's width / height
                if (width >= screen_width && height >= screen_height) {
                    final float scale = sContext.getResources().getDisplayMetrics().density;
                    int new_width = (int) (width / scale + 0.5f);
                    int new_height = (int) (height / scale + 0.5f);
                    Bitmap b2 = Bitmap.createScaledBitmap(b, new_width, new_height, false);
                    d = new BitmapDrawable(sContext.getResources(), b2);
                } else {
                    d = new BitmapDrawable(sContext.getResources(), b);
                }

                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
        return d;
    }

    /**
     * returns an image from the database
     */
    public static Drawable getImage(int id, String imageName, int required_size) {
        Attachment att = getAttachment(id, imageName);
        Drawable d = null;
        if (att != null) {
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                InputStream is = att.getContent();
                Bitmap b = BitmapFactory.decodeStream(is);

                //Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = required_size;
                int width_tmp = b.getWidth(), height_tmp = b.getHeight();
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                Bitmap b2 = Bitmap.createScaledBitmap(b, width_tmp, height_tmp, false);
                d = new BitmapDrawable(sContext.getResources(), b2);
                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
        return d;
    }

    /**
     * Put some dummy data to the database
     * Call this function manual, if you need to reset the database.
     * IMPORTANT: This data will be replicated to the life database, so be sure what you are doing!
     */
    // ToDo: Remove this function for productive use!
    public void insertDummyDataToDatabase() {
        try {
            mDatabase.delete();
            mDatabase = null;
            initDatabase(true);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error deleting local database", e);
            return;
        }

        /* insert text */

        Exhibit e1 = new Exhibit(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist" +
                " die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der " +
                "Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583,
                new String[]{"Kirche"}, new String[]{"Dom"}, null, new LinkedList<Page>());
        insertExhibit(e1);

        Exhibit e2 = new Exhibit(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, " +
                "Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.",
                51.706768, 8.771104, new String[]{"Uni"}, new String[]{"Universität"}, null, new LinkedList<Page>());
        insertExhibit(e2);


        /**
         * The data adding code below needs to be refactored!
         */

        LinkedList<Waypoint> waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(51.715606, 8.746552, -1));
        waypoints.add(new Waypoint(51.718178, 8.747164, -1));
        waypoints.add(new Waypoint(51.722850, 8.750780, -1));
        waypoints.add(new Waypoint(51.722710, 8.758365, -1));
        waypoints.add(new Waypoint(51.718789, 8.762699, -1));
        waypoints.add(new Waypoint(51.715745, 8.757796, -1));
        waypoints.add(new Waypoint(51.715207, 8.752142, 7));
        waypoints.add(new Waypoint(51.715606, 8.746552, -1));

        List<RouteTag> ringrouteTags = new LinkedList<>();
        ringrouteTags.add(new RouteTag("bar", "Bar", "route_tag_bar"));
        ringrouteTags.add(new RouteTag("restaurant", "Restaurant", "route_tag_restaurant"));

        insertRoute(101, "Ringroute", "Dies ist ein einfacher Rundweg rund um den Ring.",
                waypoints, 60 * 30, 5.2, ringrouteTags, "route_ring.jpg");



        List<SliderImage> sliderImages = new LinkedList<>();
        sliderImages.add(new SliderImage(776, "Phase 1.jpg"));
        sliderImages.add(new SliderImage(799, "Phase 2.jpg"));
        sliderImages.add(new SliderImage(836, "Phase 3.jpg"));
        sliderImages.add(new SliderImage(900, "Phase 4.jpg"));
        sliderImages.add(new SliderImage(938, "Phase 5.jpg"));
        insertSlider(201, sliderImages);

        addImage(R.drawable.phasei, 201, "Phase 1.jpg");
        addImage(R.drawable.phaseii, 201, "Phase 2.jpg");
        addImage(R.drawable.phaseiii, 201, "Phase 3.jpg");
        addImage(R.drawable.phaseiv, 201, "Phase 4.jpg");
        addImage(R.drawable.phasev, 201, "Phase 5.jpg");
    }

    /**
     * adds an image from R.drawable to the document defined by document_id in local database
     */
    private void addImage(int image_number, int document_id, String imageName) {
        InputStream image = mContext.getResources().openRawResource(+image_number);
        // "+" is from: https://stackoverflow.com/questions/25572647/android-openrawresource-not-working-for-a-drawable
        addAttachment(document_id, imageName, "image/jpeg", image);
    }

    public void addAttachment(int document_id, String filename,
                              String mimeType, InputStream attachment) {
        Document doc = mDatabase.getDocument(String.valueOf(document_id));
        UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
        newRev.setAttachment(filename, mimeType, attachment);
        try {
            newRev.save();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error attaching resource " + filename + " to document " + document_id, e);
        }
    }

    /**
     * notify the UI Thread that the database has changed
     */
    private synchronized void notifyExhibitSetChanged() {
        if (!((Activity) mContext).getClass().getSimpleName().equals("MainActivity")) {
            return;
        }

        ((MainActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mContext).notifyExhibitSetChanged();
            }
        });
    }

    /**
     * gets the (local) database, ensures the Singleton pattern
     */
    public Database getDatabaseInstance() {
        if (this.mDatabase == null) {
            try {
                this.mDatabase = getManagerInstance().getDatabase(DB_NAME);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.mDatabase;
    }

    /**
     * gets the manager of the (local) database, ensures the Singleton pattern
     */
    public Manager getManagerInstance() throws IOException {
        if (this.mManager == null) {
            this.mManager = new Manager(new AndroidContext(this.mContext), Manager.DEFAULT_OPTIONS);
        }
        return this.mManager;
    }

    /**
     * initialize the database, the flag enablePush indicates
     * if local changes should be pushed to the gateway
     */
    private void initDatabase(Boolean enablePush) {
        try {
            setHttpClientFactory(); // sets the HTTP Factory to connect over SSL to the gateway

            /* set up a connection to Sync Gateway */
            URL url = new URL(COUCHBASE_SERVER_URL);

            Authenticator auth = AuthenticatorFactory.createBasicAuthenticator(
                    COUCHBASE_USER, COUCHBASE_PASSWORD); // authenticate on the gateway

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
        }
    }

    /**
     * Gets the key store with the (self signed) trusted certificates to the gateway
     */
    private KeyStore getKeyStore() {
        KeyStore keystore = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // get the public certificate
            InputStream in = this.mContext.getResources().openRawResource(R.raw.ssl);
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

    /**
     * sets the Couchbase Lite Manager HTTP Factory with the keystore returned by getKeystore()
     */
    private void setHttpClientFactory() {
        PersistentCookieStore cookieStore = null;

        cookieStore = getDatabaseInstance().getPersistentCookieStore(); // get Cookie Store

        CouchbaseLiteHttpClientFactory cblHttpClientFactory =
                new CouchbaseLiteHttpClientFactory(cookieStore); // get Factory

        try {
            cblHttpClientFactory.setSSLSocketFactory(
                    new SSLSocketFactory(getKeyStore())); // sets the keystore
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
        mManager.setDefaultHttpClientFactory(cblHttpClientFactory);
    }

    /**
     * insert a exhibit in the database
     */
    public void insertExhibit(Exhibit exhibit) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(exhibit.getId()));
        ExhibitSerializer.serializeExhibit(document, exhibit);

    }

    public void insertSlider(int id, List<SliderImage> sliderImages) {
        Document document = mDatabase.getDocument(String.valueOf(id));
        Map<String, Object> properties = new HashMap<>();

        if (sliderImages != null && !sliderImages.isEmpty()) {
            properties.put(KEY_TYPE, "slider");
            properties.put(KEY_SLIDER_IMAGES, sliderImages);
            //ensure access for all users in the Couchbase database
            properties.put(KEY_CHANNELS, "*");
        }

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }
    }

    /**
     * insert a route in the database
     */
    public void insertRoute(int id, String title, String description,
                            LinkedList<Waypoint> waypoints, int duration, double distance,
                            List<RouteTag> tags, String imageName) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(id));
        Map<String, Object> properties = new HashMap<>();

        properties.put(KEY_TYPE, "route");
        properties.put(KEY_ROUTE_TITLE, title);
        properties.put(KEY_ROUTE_DESCRIPTION, description);
        properties.put(KEY_ROUTE_WAYPOINTS, waypoints);
        properties.put(KEY_ROUTE_DURATION, duration);
        properties.put(KEY_ROUTE_DISTANCE, distance);
        properties.put(KEY_ROUTE_TAGS, tags);
        properties.put(KEY_ROUTE_IMAGE_NAME, imageName);
        properties.put(KEY_CHANNELS, "*");
        //KEY_CHANNELS "*" ensures the access for all users in the Couchbase database

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

        //Add images for route tags as attachment
        for (RouteTag tag : tags) {
            final int resId = mContext.getResources().getIdentifier(tag.getImageFilename(),
                    "drawable", mContext.getPackageName());
            if (resId != 0) {
                InputStream ress = mContext.getResources().openRawResource(+resId);
                addAttachment(id, tag.getImageFilename(), "image/jpeg", ress);
            } else {
                Log.e("routes", "Could not load image tag resource for route " + id);
            }
        }

        //Add route image as attachment
        //Use only the part before "." for the filename when accessing the Android resource
        final int resId = mContext.getResources().getIdentifier(imageName.split("\\.")[0],
                "drawable", mContext.getPackageName());
        if (resId != 0) {
            InputStream ress = mContext.getResources().openRawResource(+resId);
            addAttachment(id, imageName, "image/png", ress);
        } else {
            Log.e("routes", "Could not load image resource for route " + id);
        }
    }

    /**
     * gets all rows from a view from the database
     */
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

                Map<String, Object> properties = getDatabaseInstance().getDocument(
                        row.getDocumentId()).getProperties();
                result.add(properties);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * gets one row_main (specified by id) from database
     */
    public Document getDocument(int id) {
        return getDatabaseInstance().getDocument(String.valueOf(id));
    }

    /**
     * gets the total count of data in the local database
     */
    public int getDocumentCount() {
        return getDatabaseInstance().getDocumentCount();
    }


}