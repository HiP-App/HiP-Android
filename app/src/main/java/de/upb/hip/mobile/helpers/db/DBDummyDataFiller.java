package de.upb.hip.mobile.helpers.db;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Image;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.SliderImage;
import de.upb.hip.mobile.models.Waypoint;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * This class adds dummy data to the DB until we can obtain real data from team CMS
 */
public class DBDummyDataFiller {

    public static final String TAG = "db-filler";

    private final Database mDatabase;
    private final DBAdapter mDbAdapter;
    private final Context mContext;


    public DBDummyDataFiller(Database db, DBAdapter dbAdapter, Context ctx) {
        this.mDatabase = db;
        this.mDbAdapter = dbAdapter;
        this.mContext = ctx;
    }

    public void insertData() {
        Exhibit e1 = new Exhibit(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist" +
                " die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der " +
                "Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(1, "", "dom.jpg", ""), new LinkedList<Page>());
        insertExhibit(e1);

        Exhibit e2 = new Exhibit(2, "Universität Paderborn", "Die Universität Paderborn in Paderborn, " +
                "Deutschland, ist eine 1972 gegründete Universität in Nordrhein-Westfalen.",
                51.706768, 8.771104, new String[]{"Uni"}, new String[]{"Universität"}, new Image(2, "", "uni.jpg", ""), new LinkedList<Page>());
        insertExhibit(e2);

        LinkedList<Waypoint> waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(51.715606, 8.746552, -1));
        waypoints.add(new Waypoint(51.718178, 8.747164, 1));
        waypoints.add(new Waypoint(51.722850, 8.750780, -1));
        waypoints.add(new Waypoint(51.722710, 8.758365, -1));
        waypoints.add(new Waypoint(51.718789, 8.762699, -1));
        waypoints.add(new Waypoint(51.715745, 8.757796, -1));
        waypoints.add(new Waypoint(51.715207, 8.752142, 2));
        waypoints.add(new Waypoint(51.715606, 8.746552, -1));

        List<RouteTag> ringrouteTags = new LinkedList<>();
        ringrouteTags.add(new RouteTag("bar", "Bar", new Image(101, "", "route_tag_bar", "")));
        ringrouteTags.add(new RouteTag("restaurant", "Restaurant", new Image(101, "", "route_tag_restaurant", "")));

        Route ringroute = new Route(101, "Ringroute", "Dies ist ein einfacher Rundweg rund um den Ring.",
                waypoints, 60 * 30, 5.2, ringrouteTags, new Image(101, "", "route_ring.jpg", ""));

        insertRoute(ringroute);


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
     * insert a exhibit in the database
     */
    public void insertExhibit(Exhibit exhibit) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(exhibit.getId()));
        ExhibitSerializer.serializeExhibit(document, exhibit, mContext, this);

    }

    /**
     * TODO: Remove this
     *
     * @param id
     * @param sliderImages
     */
    public void insertSlider(int id, List<SliderImage> sliderImages) {
        Document document = mDatabase.getDocument(String.valueOf(id));
        Map<String, Object> properties = new HashMap<>();

        if (sliderImages != null && !sliderImages.isEmpty()) {
            properties.put(DBAdapter.KEY_TYPE, "slider");
            properties.put(DBAdapter.KEY_SLIDER_IMAGES, sliderImages);
            //ensure access for all users in the Couchbase database
            properties.put(DBAdapter.KEY_CHANNELS, "*");
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
    public void insertRoute(Route route) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(route.getId()));
        RouteSerializer.serializeRoute(document, route, mContext, this);
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
}

