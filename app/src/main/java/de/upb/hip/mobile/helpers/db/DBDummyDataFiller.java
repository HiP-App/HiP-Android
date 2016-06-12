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

package de.upb.hip.mobile.helpers.db;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Audio;
import de.upb.hip.mobile.models.Image;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteTag;
import de.upb.hip.mobile.models.Waypoint;
import de.upb.hip.mobile.models.exhibit.AppetizerPage;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.ImagePage;
import de.upb.hip.mobile.models.exhibit.Page;
import de.upb.hip.mobile.models.exhibit.TextPage;

/**
 * This class adds dummy data to the DB until we can obtain real data from team CMS
 */
public class DBDummyDataFiller {


    public static final String TAG = "db-filler";


    private static final Audio audio1 = new Audio(R.raw.sprechertext_1, "Während des bisherigen Rundgangs haben Sie erfahren, wie wichtig das Gebiet zwischen Lippe und Pader für die Politik Karls des Großen ab den 770er Jahren war. Erinnern wir uns nur an die große Reichsversammlung im Jahre 777! Zu diesem Anlass fanden sich Franken, Sachsen, aber auch arabische Gesandte aus Spanien hier in Paderborn zusammen.\n" +
            "Aber was fanden diese Personen hier vor? Wie hat man sich das damalige Paderborn, die sogenannte „urbs Karoli“, eigentlich vorzustellen? Lange Zeit fragten sich Historiker und Archäologen, ob die „urbs Karoli“ tatsächlich in Paderborn existierte und wenn ja, wo genau?  Die karolingischen Geschichtswerke sprechen für Paderborn nicht von palatium,  \n" +
            "dem  \n" +
            "lateinischen Wort für „Pfalz“. Und es gab auch keine archäologischen Anhaltspunkte für die Pfalz Karls des Großen in Paderborn. Noch Mitte des 20. Jahrhunderts schrieb der Akademieprofessor und Domkapitular Alois Fuchs, dass „für eine [karolingische] Pfalz in Paderborn nicht nur alle urkundlichen Bezeugungen fehlen, sondern auch alle Baureste, die für die charakteristischen Pfalzgebäude, den Reichssaal und die Pfalzkapelle, sprechen könnten.“  Sichtbar waren einzig verbaute Überreste der Domburg Bischof Meinwerks aus dem 11. Jahrhundert. Diese Überreste hatten bereits Mitte des 19. Jahrhunderts das Interesse von Lokalforschern geweckt. \n" +
            "Jetzt stehen Sie zwischen dem Dom und dem Museum in der Kaiserpfalz. Dieses große und repräsentative Gebäude mit den Rundbogenfenstern sieht so aus, wie man sich eine Kaiserpfalz vorstellt. Doch handelt es sich dabei um die Pfalz Karls des Großen? Nein! Es ist die archäologische Rekonstruktion der Pfalz Bischof Meinwerks aus dem frühen 11. Jahrhundert. \n Aber wo befand sich nun die karolingische Kaiserpfalz? Sehen Sie die etwa 31 mal 10 m große, rechteckige Fläche zwischen Ihnen und dem Museum? Sie ist durch Bruchsteinmauern abgegrenzt. Das sind die aus konservatorischen Gründen aufgemauerten Fundamente der sog. aula regia, der Königshalle Karls des Großen. Wenn Sie genau hinschauen, sehen Sie ein rotes Ziegelband. Dieses trennt das originale Bruchsteinmauerwerk von später, im Zuge der Rekonstruktion ergänzten Steinen.");


    private static final String text1 = "Text1";

    private final Database mDatabase;
    private final DBAdapter mDbAdapter;
    private final Context mContext;


    public DBDummyDataFiller(Database db, DBAdapter dbAdapter, Context ctx) {
        this.mDatabase = db;
        this.mDbAdapter = dbAdapter;
        this.mContext = ctx;
    }

    public void insertData() {
        // Create some example pages for the Dom
        LinkedList<Page> kaiserpfalzPages = new LinkedList<>();


        kaiserpfalzPages.add(new AppetizerPage("missing Appetizer",
                new Image(1, text1, "kaiserpfalz_teaser.jpg", "Die Kaiserpfalz"), null));


        kaiserpfalzPages.add(new ImagePage(new Image(1, text1, "kaiserpfalz_teaser.jpg", "Kaiserpfalz"), null, null, audio1));



        Exhibit kaiserpfalz = new Exhibit(1, "Die Kaiserpfalz", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist" +
                " die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der " +
                "Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(1, "", "kaiserpfalz_teaser.jpg", ""), kaiserpfalzPages);
        insertExhibit(kaiserpfalz);



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
     * insert a route in the database
     */
    public void insertRoute(Route route) {
        //create a new entry but with predefined id
        Document document = mDatabase.getDocument(String.valueOf(route.getId()));
        RouteSerializer.serializeRoute(document, route, mContext, this);
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

