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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

//import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.models.*;

import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.Attachment;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.Revision;
import com.couchbase.lite.support.CouchbaseLiteHttpClientFactory;
import com.couchbase.lite.support.PersistentCookieStore;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;

import org.apache.http.conn.ssl.SSLSocketFactory;

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



/**
 *  Class for the connection between app and database
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
    public static final String KEY_EXHIBIT_PICTURE_DESCRIPTIONS = "pictureDescriptions";
    public static final String KEY_EXHIBIT_SLIDER_ID = "sliderId";

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
    private Manager mManager = null; // local database manager
    private static Database mDatabase = null; // local database
    // URL to Server with running Couchbase Sync Gateway
    public static final String COUCHBASE_SERVER_URL = "https://couchbase-hip.cs.upb.de:4984/hip";
    // username to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_USER = "android_user";
    // password to access the data bucket on Couchbase Sync Gateway
    private static final String COUCHBASE_PASSWORD = "5eG410KF2fnPSnS0";


    public static final String TAG = "DBAdapter"; // for logging
    private final Context mContext; // Context of application who uses us.
    private static Context staticContext; // static context for static getImage()-method

    /* Constructor */
    public DBAdapter(Context ctx) {
        mContext = ctx;
        staticContext = ctx;
        if (mDatabase == null) {
            initDatabase(false);
            // uncomment this line to set up the gateway database with new dummy data
//            insertDummyDataToDatabase();
        }
    }

    public static Attachment getAttachment(int documentId, String filename) {
        Document doc = mDatabase.getDocument(String.valueOf(documentId));
        Revision rev = doc.getCurrentRevision();
        Attachment attachment = rev.getAttachment(filename);
        return attachment;
    }

    /**
     *  returns an image from the database
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

                WindowManager wm = (WindowManager) staticContext.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screen_width = size.x;
                int screen_height = size.y;

                // check if greater than device's width / height
                if (width >= screen_width && height >= screen_height) {
                    final float scale = staticContext.getResources().getDisplayMetrics().density;
                    int new_width = (int) (width / scale + 0.5f);
                    int new_height = (int) (height / scale + 0.5f);
                    Bitmap b2 = Bitmap.createScaledBitmap(b, new_width, new_height, false);
                    d = new BitmapDrawable(staticContext.getResources(), b2);
                } else {
                    d = new BitmapDrawable(staticContext.getResources(), b);
                }

                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
        return d;
    }

    /* returns an image from the database */
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
                d = new BitmapDrawable(staticContext.getResources(), b2);
                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
        return d;
    }

    /** Put some dummy data to the database
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

        /* insert text and images*/
        HashMap<String, String> pictureDescriptions = new HashMap<>();
        String pictureName = "image.jpg";

        pictureDescriptions.put(pictureName, "Dom zu Paderborn (Südseite)");
        insertExhibit(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist" +
                        " die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der " +
                        "Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583,
                "Kirche", "Dom", pictureDescriptions, -1);
        addImage(R.drawable.dom, 1, pictureName);

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Gebäude O");
        insertExhibit(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, " +
                        "Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.",
                51.706768, 8.771104, "Uni", "Universität", pictureDescriptions, -1);
        addImage(R.drawable.uni, 2, pictureName);

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Das HNI-Gebäude");
        insertExhibit(3, "Heinz Nixdorf Institut", "Das Heinz Nixdorf Institut (HNI) ist ein " +
                        "interdisziplinäres Forschungsinstitut der Universität Paderborn.",
                51.7292257, 8.7434972, "Uni", "HNI", pictureDescriptions, -1);
        addImage(R.drawable.hnf, 3, pictureName);

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Museum in der Kaiserpfalz (aus Richtung Dom)");
        insertExhibit(4, "Museum in der Kaiserpfalz", "Das Museum in der Kaiserpfalz befindet" +
                " sich in Paderborn in unmittelbarer Nähe des Doms. Es stellt Funde aus" +
                " karolingischer, ottonischer und sächsischer Zeit vor. Es befindet sich an" +
                " der Stelle, an der man 1964 bei Bauarbeiten die Grundmauern der Pfalzanlage" +
                " aus dem 8. Jahrhundert bzw. aus der Zeit Heinrichs II. gefunden hat. Sie sind" +
                " Teil der heutigen Bausubstanz und lassen sich im Mauerwerk des Museums noch"
                + " sehr gut nachvollziehen. Direkt neben dem heutigen Museum fand man 1964" +
                " auch die Kaiserpfalz Karl des Großen. Der Umriss dieser Anlage ist heute nur" +
                " noch durch die rekonstruierten Grundmauern zu erkennen. Träger des" +
                " Landesmuseums ist der Landschaftsverband Westfalen-Lippe. Das Gebäude gehört" +
                " dem Metropolitankapitel und wird mietzinsfrei an den Träger des Museums " +
                "vermietet", 51.719412, 8.755524, "Kirche, Museum", "", pictureDescriptions, 201);
        addImage(R.drawable.pfalz, 4, pictureName);

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Abdinghofkirche (außen)");
        insertExhibit(5, "Abdinghofkirche", "Das Abdinghofkloster Sankt Peter und Paul ist" +
                        " eine ehemalige Abtei der Benediktiner in Paderborn, bestehend von seiner" +
                        " Gründung im Jahre 1015 bis zu seiner Säkularisation am 25. März 1803. In der" +
                        " Zeit seines Bestehens standen ihm insgesamt 51 Äbte vor. Kulturelle Bedeutung" +
                        " erlangte es durch seine Bibliothek, die angeschlossene Schule, ein Hospiz," +
                        " seine Werkstatt für Buchmaler und Buchbinderei und wichtige Kirchenschätze." +
                        " Zudem war das Kloster lange Zeit Grundbesitzer im Wesergebiet (so die" +
                        " Externsteine) und am Niederrhein bis in die Niederlande. Die Kirche ist" +
                        " heute eine evangelisch-lutherische Pfarrkirche.", 51.718725, 8.752889,
                "Kirche", "", pictureDescriptions, -1);
        addImage(R.drawable.abdinghof, 5, "image.jpg");

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Busdorfkirche (außen)");
        insertExhibit(6, "Busdorfkirche", "Die Busdorfkirche ist eine Kirche in Paderborn, die" +
                        " nach dem Vorbild der Grabeskirche in Jerusalem entstand. Das Stift Busdorf war" +
                        " ein 1036 gegründetes Kollegiatstift in Paderborn. Stift und Kirche lagen" +
                        " ursprünglich außerhalb der Stadt, wurden aber im 11./12. Jahrhundert im Zuge" +
                        " der Stadterweiterung in diese einbezogen.", 51.7186951, 8.7577606,
                "Kirche", "", pictureDescriptions, -1);
        addImage(R.drawable.busdorfkirche_aussen, 6, pictureName);

        pictureDescriptions.clear();
        pictureDescriptions.put("image.jpg", "Liborikapelle (außen)");
        insertExhibit(7, "Liborikapelle",
                "Die spätbarocke, äußerlich unscheinbare Liborikapelle ist vor den Mauern" +
                        " der alten Stadt auf dem Liboriberg zu finden. Von weitem leuchtet der" +
                        " vergoldete Pfau als Wetterfahne auf dem Dachreiter. Ein Pfau als" +
                        " Zeichen für die Verehrung des hl. Liborius schmückt auch die Stirnseite" +
                        " über dem auf Säulen ruhenden Vordach. Inschriften zeigen Gebete und" +
                        " Lobsprüche für den Stadt- und Bistumsheiligen Liborius und geben" +
                        " Hinweis auf den Erbauer sowie auf das Erbauungsjahr 1730. Die Kapelle" +
                        " diente als Station auf der alljährlichen Libori-Prozession rund um" +
                        "die Stadt.", 51.715041, 8.754022, "Kirche", "", pictureDescriptions, -1);
        addImage(R.drawable.liboriuskapelle, 7, "image.jpg");

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Paderquellen");
        insertExhibit(8, "Paderquellen", "", 51.718529, 8.750662, "", "", pictureDescriptions, -1);
        addImage(R.drawable.paderquellen, 8, "image.jpg");

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Denkmal für Karl den Großen");
        insertExhibit(9, "Denkmal für Karl den Großen", "", 51.713877, 8.753032, "", "",
                pictureDescriptions, -1);
        addImage(R.drawable.denkmalkdg, 9, "image.jpg");

        pictureDescriptions.clear();
        pictureDescriptions.put(pictureName, "Karlsschule");
        insertExhibit(10, "Karlsschule", "", 51.713587, 8.750617, "", "", pictureDescriptions, -1);
        addImage(R.drawable.karlsschule, 10, "image.jpg");

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

        waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(51.718590, 8.752206, 5));
        waypoints.add(new Waypoint(51.719128, 8.755457, 1));
        waypoints.add(new Waypoint(51.719527, 8.755736, 4));
        waypoints.add(new Waypoint(51.718969, 8.758472, 6));
        waypoints.add(new Waypoint(51.720371, 8.761723, -1));
        waypoints.add(new Waypoint(51.719454, 8.767484, -1));

        List<RouteTag> stadtrouteTags = new LinkedList<>();
        stadtrouteTags.add(new RouteTag("restaurant", "Restaurant", "route_tag_restaurant"));

        insertRoute(102, "Stadtroute", "Dies ist eine kurze Route in der Stadt.",
                waypoints, 60 * 120, 3.5, stadtrouteTags, "route_stadt.jpg");

        //Waypoints marked with *** are also markes displayed on the map
        waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(51.715506, 8.746364, -1)); // Bahnhofstr/Westerntor
        waypoints.add(new Waypoint(51.718192, 8.747126, -1)); // Friedrichstr/Marienstraße
        waypoints.add(new Waypoint(51.717876, 8.750280, -1)); // Marienstraße/Weberberg
        //***Paderquellen: Paderufer gegenüber des Galerie-Hotels
        waypoints.add(new Waypoint(51.718529, 8.750662, 8));
        waypoints.add(new Waypoint(51.718806, 8.751074, -1)); // Jenny-Aloni-Weg
        waypoints.add(new Waypoint(51.718610, 8.752168, -1)); // Abdinghof / Paderquellen
        waypoints.add(new Waypoint(51.718936, 8.753150, -1)); // Abdinghof
        waypoints.add(new Waypoint(51.718866, 8.754577, -1)); // Abdinghof / Ikenberg
        waypoints.add(new Waypoint(51.719128, 8.755457, -1)); // Dom
        waypoints.add(new Waypoint(51.719527, 8.755736, 4)); // ***Kaiserpfalz
        waypoints.add(new Waypoint(51.719128, 8.755457, 1)); // ***Dom
        waypoints.add(new Waypoint(51.718866, 8.754577, -1)); // Abdinghof / Ikenbergu
        waypoints.add(new Waypoint(51.717992, 8.755167, -1)); // Markt
        waypoints.add(new Waypoint(51.717543, 8.754539, -1)); // Schildern
        waypoints.add(new Waypoint(51.717321, 8.753423, -1)); // Rathausplatz/Kamp
        waypoints.add(new Waypoint(51.717281, 8.752490, -1)); // Marienplatz
        waypoints.add(new Waypoint(51.716862, 8.751353, -1)); // Rosenstr / Westernstr
        waypoints.add(new Waypoint(51.715187, 8.752109, -1)); // Bahnübergang Rosentor***
        waypoints.add(new Waypoint(51.713781, 8.752490, -1)); // Kilian/Karlstr
        //***Karlsstraße/Turmplatz (Denkmal für Karl den Großen)
        waypoints.add(new Waypoint(51.713881, 8.753021, 9));
        waypoints.add(new Waypoint(51.713781, 8.752490, -1)); // Kilian/Karlstr
        waypoints.add(new Waypoint(51.713442, 8.751331, -1)); // Vor Karlsschule
        waypoints.add(new Waypoint(51.713442, 8.751331, 10)); // ***Karlsschule
        waypoints.add(new Waypoint(51.713442, 8.751331, -1)); // Vor Karlsschule
        waypoints.add(new Waypoint(51.713060, 8.750457, -1)); // Widukindstr/Geroldstr
        //***Rundgang durch die Straßen - z.B: Widukindstraße
        waypoints.add(new Waypoint(51.711812, 8.749370, -1));

        List<RouteTag> karlsrouteTags = new LinkedList<>();
        karlsrouteTags.add(new RouteTag("restaurant", "Restaurant", "route_tag_restaurant"));

        insertRoute(103, "Karlsroute", "Rundgang zu Karl dem Großen", waypoints, 60 * 120, 3.5,
                karlsrouteTags, "route_karl.jpg");

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
     *  adds an image from R.drawable to the document defined by document_id in local database
     */
    private void addImage(int image_number, int document_id, String imageName) {
        InputStream image = mContext.getResources().openRawResource(+image_number);
        // "+" is from: https://stackoverflow.com/questions/25572647/android-openrawresource-not-working-for-a-drawable
        addAttachment(document_id, imageName, "image/jpeg", image);
    }

    public void addAttachment(int document_id, String filename,
                              String mimeType, InputStream attachment){
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
     *  notify the UI Thread that the database has changed
     */
    private synchronized void notifyExhibitSetChanged() {
        if(!((Activity) mContext).getClass().getSimpleName().equals("MainActivity")){
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
     *  gets the (local) database, ensures the Singleton pattern
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
     *  gets the manager of the (local) database, ensures the Singleton pattern
     */
    public Manager getManagerInstance() throws IOException {
        if (this.mManager == null) {
            this.mManager = new Manager(new AndroidContext(this.mContext), Manager.DEFAULT_OPTIONS);
        }
        return this.mManager;
    }

    /**
     *  initialize the database, the flag enablePush indicates
     *  if local changes should be pushed to the gateway
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
     *  Gets the key store with the (self signed) trusted certificates to the gateway
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
     *  sets the Couchbase Lite Manager HTTP Factory with the keystore returned by getKeystore()
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

    /* insert a exhibit in the database */
    public void insertExhibit(int id, String name, String description, double lat, double lng,
                              String categories, String tags,
                              HashMap<String, String> pictureDescriptions, int sliderId) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(id));
        Map<String, Object> properties = new HashMap<>();

        properties.put(KEY_TYPE, "exhibit");
        properties.put(KEY_EXHIBIT_NAME, name);
        properties.put(KEY_EXHIBIT_DESCRIPTION, description);
        properties.put(KEY_EXHIBIT_CATEGORIES, categories);
        properties.put(KEY_EXHIBIT_TAGS, tags);
        properties.put(KEY_EXHIBIT_LAT, lat);
        properties.put(KEY_EXHIBIT_LNG, lng);
        //ensure access for all users in the Couchbase database
        properties.put(KEY_CHANNELS, "*");
        properties.put(KEY_EXHIBIT_PICTURE_DESCRIPTIONS, pictureDescriptions);
        properties.put(KEY_EXHIBIT_SLIDER_ID, sliderId);


        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

    }

    public void insertSlider(int id, List<SliderImage> sliderImages){
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
     *  insert a route in the database
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
        for(RouteTag tag: tags){
            final int resId =  mContext.getResources().getIdentifier(tag.getImageFilename(),
                    "drawable", mContext.getPackageName());
            if(resId != 0){
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
        if(resId != 0){
            InputStream ress = mContext.getResources().openRawResource(+resId);
            addAttachment(id, imageName, "image/png", ress);
        } else {
            Log.e("routes", "Could not load image resource for route " + id);
        }
    }

    /**
     *  gets all rows from a view from the database
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
     *  gets one row_main (specified by id) from database
     */
    public Document getDocument(int id) {
        return getDatabaseInstance().getDocument(String.valueOf(id));
    }

    /**
     *  gets the total count of data in the local database
     */
    public int getDocumentCount() {
        return getDatabaseInstance().getDocumentCount();
    }


}