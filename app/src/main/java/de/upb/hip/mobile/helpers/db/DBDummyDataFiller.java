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

    private static final String lorem = "lorem.";

    private static final Audio audio1 = new Audio(0, "audio.mp3", "Während des bisherigen Rundgangs haben Sie erfahren, wie wichtig das Gebiet zwischen Lippe und Pader für die Politik Karls des Großen ab den 770er Jahren war. Erinnern wir uns nur an die große Reichsversammlung im Jahre 777! Zu diesem Anlass fanden sich Franken, Sachsen, aber auch arabische Gesandte aus Spanien hier in Paderborn zusammen.\n" +
            "Aber was fanden diese Personen hier vor? Wie hat man sich das damalige Paderborn, die sogenannte „urbs Karoli“, eigentlich vorzustellen? Lange Zeit fragten sich Historiker und Archäologen, ob die „urbs Karoli“ tatsächlich in Paderborn existierte und wenn ja, wo genau?  Die karolingischen Geschichtswerke sprechen für Paderborn nicht von palatium,  \n" +
            "dem  \n" +
            "lateinischen Wort für „Pfalz“. Und es gab auch keine archäologischen Anhaltspunkte für die Pfalz Karls des Großen in Paderborn. Noch Mitte des 20. Jahrhunderts schrieb der Akademieprofessor und Domkapitular Alois Fuchs, dass „für eine [karolingische] Pfalz in Paderborn nicht nur alle urkundlichen Bezeugungen fehlen, sondern auch alle Baureste, die für die charakteristischen Pfalzgebäude, den Reichssaal und die Pfalzkapelle, sprechen könnten.“  Sichtbar waren einzig verbaute Überreste der Domburg Bischof Meinwerks aus dem 11. Jahrhundert. Diese Überreste hatten bereits Mitte des 19. Jahrhunderts das Interesse von Lokalforschern geweckt. \n" +
            "Jetzt stehen Sie zwischen dem Dom und dem Museum in der Kaiserpfalz. Dieses große und repräsentative Gebäude mit den Rundbogenfenstern sieht so aus, wie man sich eine Kaiserpfalz vorstellt. Doch handelt es sich dabei um die Pfalz Karls des Großen? Nein! Es ist die archäologische Rekonstruktion der Pfalz Bischof Meinwerks aus dem frühen 11. Jahrhundert. \n Aber wo befand sich nun die karolingische Kaiserpfalz? Sehen Sie die etwa 31 mal 10 m große, rechteckige Fläche zwischen Ihnen und dem Museum? Sie ist durch Bruchsteinmauern abgegrenzt. Das sind die aus konservatorischen Gründen aufgemauerten Fundamente der sog. aula regia, der Königshalle Karls des Großen. Wenn Sie genau hinschauen, sehen Sie ein rotes Ziegelband. Dieses trennt das originale Bruchsteinmauerwerk von später, im Zuge der Rekonstruktion ergänzten Steinen.", R.raw.a);

    private static final Audio audio2 = new Audio(0, "audio.mp3", "Falls Sie Schwierigkeiten haben sollten, sich in diesem Mauergewirr zurechtzufinden, blicken Sie auf Ihr Display. Hier werden die Mauern der aula regia rot hervorgehoben.\n Vor hundert Jahren hätten Sie davon noch nichts sehen können. Denn dieses Gelände war damals mit Fachwerkhäusern bebaut, die im 2. Weltkrieg zerstört wurden. Einen Eindruck, wie sich das Gelände nördlich des Doms verändert hat, bietet der Fotoslider auf Ihrem Display. Die Kriegszerstörungen boten aber auch neue Möglichkeiten. So legte man ab den 1950er Jahren Teile der karolingischen Befestigungsmauer frei und konnte so ihren Verlauf rekonstruieren.", R.raw.a);
    private static final Audio audio3 = new Audio(0, "audio.mp3", "Auf Ihrem Display sehen Sie nun einen Plan der Paderborner Innenstadt. Hier ist der Verlauf der karolingischen Befestigungsmauer unter Berücksichtigung der Grabungsergebnisse von Marianne Moser eingezeichnet.  Sie sehen, dass die Mauer ein annähernd quadratisches Areal von 280 mal 250 m um den heutigen Dom einfasste. Diese Kalkbruchsteinmauer ersetzte die frühere Holz-Erde-Konstruktion nach der Zerstörung der urbs Karoli durch die Sachsen im Jahre 778. Eine solche gemauerte Befestigung war zu dieser Zeit unüblich. In Paderborn war sie aber aufgrund der Sachsenkriege zwingend notwendig.\n" +
            "Aber zurück zu unserem stark zerstörten Gelände der Nachkriegszeit. Es sollte 1963 neu erschlossen werden. Bei Baggerarbeiten stieß man auf ältere Gebäudemauern. Deshalb entschloss man sich, das gesamte Areal archäologisch zu untersuchen. Die systematische Freilegung begann 1964 und wurde 14 Jahre lang unter der Leitung des Archäologen Wilhelm Winkelmann fortgeführt. \n Einen Überblick über die verschiedenen mittelalterlichen Gebäudereste aus unterschiedlichen Jahrhunderten bietet die Fotografie, die Sie nun auf Ihrem Display sehen können. Sie wurde Mitte der 60er Jahre während der Ausgrabungen vom Domturm herab gemacht und zeigt die gesamte Grabungsfläche nördlich des Domes. Die Ausgräber legten nicht nur die ottonisch-salische Pfalz frei - hier gelb markiert -, sondern sie fanden auch die Überreste der karolingischen Pfalz - hier rot markiert.\n" +
            "Und genau inmitten dieses Areals stehen Sie nun. Eine Abbildung in Ihrer App hilft Ihnen, die im Folgenden beschriebenen baulichen Elemente der Karlspfalz zu identifizieren.\n", R.raw.a);
    private static final Audio audio4 = new Audio(0, "audio.mp3", "Kommen wir wieder auf die aula regia Karls des Großen zurück. Wie Sie anhand der Bruchsteinmauern sehen, handelte es sich um ein einfaches rechteckiges Steingebäude. Auf der Ihnen zugewandten Seite, der Südseite, befanden sich zwei Eingänge, die zu den Wirtschaftsräumen führten.   Trotz der geringen Größe wird es seiner Zeit die Sachsen vor Ort in Staunen versetzt haben, waren Steinbauten in dieser Region doch eine absolute Ausnahme.  Und noch etwas muss sie überrascht haben: Dieses Gebäude aus Stein war zweigeschossig. Spätestens nach der Zerstörung beim Sachsenaufstand 778, hatte die wiederaufgebaute aula regia ein repräsentatives Obergeschoss und ein kellerartiges Untergeschoss mit fünf Wirtschaftsräumen.  Sichtbar ist heute nur noch der Grundriss im Untergeschoss. Sie müssen sich vorstellen, dass das Gebäude vermutlich fünf bis sechs Meter in die Höhe ragte. Ein für diese Zeit sicherlich beeindruckender Bau! Sehen Sie das viereckige Fundament an der Südwest-Ecke, d.h. zu Ihrer Linken? ", R.raw.a);
    private static final Audio audio5 = new Audio(0, "audio.mp3", "Es sind vermutlich die Fundamentreste eines Balkons oder Altans.  Ein Balkon oder Altan, d.h. ein „vom Erdboden aus gestützter balkonartiger Anbau“,  diente dem Herrscher zum repräsentativen Auftritt: Hier konnte er direkt aus dem Festsaal, der aula regia, im Obergeschoss vor sein Gefolge treten und aus erhöhter Position sprechen. Dies war ein deutliches Zeichen seiner Königswürde. Dabei stellte sich auch schon früh die Frage, wo Karl der Große im Festsaal Platz nahm. Doch dazu gibt es leider weder Aussagen in Geschichtswerken noch einen konkreten archäologischen Befund. Vermutlich saß Karl auf einem mobilen, klappbaren Thron, der entweder auf der nördlichen Längsseite – gegenüber dem Eingang – oder auf einer der Schmalseiten im Obergeschoss gestanden haben könnte. \n" +
            "Schauen Sie nach rechts auf die gegenüberliegende, südöstliche Seite. Sehen Sie die dunkelgraue Pflastersteine in Höhe des heutigen Bodenniveaus? Sie markieren die zwei parallel verlaufenden Mauern, die den repräsentativen Zugang zum Obergeschoss bildeten. \n", R.raw.a);
    private static final Audio audio6 = new Audio(0, "audio.mp3", "Eine Vorstellung, wie diese aula regia bei der Reichsversammlung im Jahre 785 ausgesehen haben könnte, vermittelt Ihnen die Zeichnung der Archäologinnen Sveva Gai und Birgit Mecke auf Ihrem Display.\n" +
            "Aber zurück zur dunkelgrauen Pflasterung... Sehen Sie vielleicht noch andere Stellen, die diese Pflasterung aufweisen? Ja, genau. Neben der Bartholomäuskapelle ist der Mauerverlauf eines weiteren karolingischen Gebäudes so gekennzeichnet. Im Gegensatz zur aula regia können Sie diesen Ort begehen. Hier stand vor 799 eine Kirche. Sie war dem Salvator,  also Jesus Christus, geweiht, ungefähr so groß wie die aula regia und ebenfalls aus Kalkbruchstein errichtet. Der Bau einer steinernen Kirche ist auch eine politische Aussage. Mitten im heidnischen Sachsen baute man schon früh einen repräsentativen Sakralbau, der vermutlich zugleich als Pfalz- und Missionskirche diente. \n Wie Sie sicherlich schon bemerkt haben, zeichnet die Pflasterung keinen vollständigen Grundriss nach. Die Linien verschwinden unter dem heutigen Dom. Den gesamten Grundriss dieser Salvatorkirche können Sie nun auf Ihrem Display sehen. Es handelte sich vermutlich um eine einschiffige Saalkirche, deren rechteckiger Chor im Osten dreigeteilt war: ein breiterer Mittelchor und 2 kleinere Nebenchöre. Im Westen schloss die Kirche mit einem rechteckigen Westbau ab, dessen Gestaltung unklar bleibt.\n Auf Ihrem Display sehen Sie nun den neuesten Rekonstruktionsvorschlag der Paderborner Stadtarchäologin Sveva Gai. Sie vermutet, dass es sich um einen doppelgeschossigen Baukörper handelte.  Im Untergeschoss wurden 16 Gräber von Männern, Frauen und Kindern freigelegt. Die Bestattung im Westbau der Kirche war nur ein „besonder[s], privilegierte[r] Bestattungsplatz“.  Weitere hunderte von Gräbern fanden sich auch südlich der damaligen Salvatorkirche.", R.raw.a);
    private static final Audio audio7 = new Audio(0, "audio.mp3", "Neben diesen beiden Steinbauten gibt es in der frühen Pfalzanlage auch Hinweise für zahlreiche Holzbauten und die Anwesenheit verschiedener Handwerker, unter ihnen Maurer, Steinmetze, Schmiede, Glasmacher und Bleigießer.  Besonders während der 770er Jahre waren diese Handwerker unentbehrlich. Sie halfen beim Bau der Anlage und zogen später weiter zu neuen Auftraggebern – die Mobilität der Menschen zu dieser Zeit ist nicht zu unterschätzen!\n" +
            "Viele Handwerker wurden wieder gegen Ende des 8. Jahrhunderts gebraucht. Damals beschloss man nämlich die Erweiterung der aula regia. Im Nordwesten wurde ein steinerner Wohntrakt angebaut.  Die Salvatorkirche wuirde durch die „ecclesiam mira[e] magnitudinis“,  das heisst eine „Kirche von wunderbarer Größe“ ersetzt. Die nun der Gottesmutter und dem hl. Kilian geweihte Kirche maß tatsächlich 21 x 42,7m und gehörte somit zu den großen Kirchenbauten des Frankenreiches, vergleichbar etwa der Klosterkirche in Lorsch oder der Abteikirche in Saint-Denis. \n", R.raw.a);
    private static final Audio audio8 = new Audio(0, "audio.mp3", "Wie sah die karolingische Pfalz um 800 also aus? Schauen Sie dazu bitte auf Ihr Display. Sie sehen dort den Plan der Anlage zu dieser Zeit. Im unteren Bereich sehen können Sie den Grundriss der neugebauten Kirche „von wunderbarer Größe“ erkennen. Ihre Fundamentmauern sind unter dem heutigen Dom noch erhalten. [Für weiterführende Informationen zur Besichtigung der Ausgrabungen klicken Sie bitte hier] Bei der Kirche handelte es sich um eine dreischiffige Basilika ohne Querhaus, die an die Tradition spätantiker Kirchenbauten anknüpfte.  Solche basilikalen Großbauten waren den zentralen Pfalzen vorbehalten.  Hierin zeigt sich die Aufwertung der Paderborner Pfalz am Ende der Sachsenkriege. Sie sehen, wie sich die urbs Karoli innerhalb zweier Jahrzehnte zu einem zentralen Ort in Sachsen entwickelt hat. Im Vergleich zu Pfalzen im fränkischen Kernland, wie etwa Aachen oder Ingelheim, war sie jedoch von geringem Ausmaß. \n Einen Eindruck der Größenverhältnisse vermittelt Ihnen der Slider.", R.raw.a);
    private static final Audio audio9 = new Audio(0, "audio.mp3", "Nun haben Sie bereits viele Pläne und Rekonstruktionen der Gebäude gesehen. Aber weiß man denn, wie sie innen aussahen?\n" +
            "Eine Vielzahl archäologischer Funde erlaubt Rückschlüsse auf die Ausstattung der Gebäude. Allerdings lassen sich diese Funde nicht bestimmten Gebäuden zuordnen, da sie in großflächigenr Schuttablagerungen gefunden wurden. Diese Schuttablagerungen fielen rund zweihundert Jahre später an, als die karolingische Pfalz abgetragen wurde, um unter Bischof Meinwerk neuen Gebäuden Raum zu geben.  Im Museum in der Kaiserpfalz können Sie eine Auswahl dieser Funde sehen. Dazu gehören etwa geritzte Ziegelplatten, die vermutlich zur Trauf- oder Eckverzierung kleinerer Bauglieder dienten. Ferner Fragmente von Fensterglas und die dazugehörigen Bleistege. Sie werden in der Fachsprache auch Bleiruten genannt. Mit ihnen wurden die einzelnen Glasteile des Fensters ehemals zusammengehalten. Darüber hinaus sind unterschiedliche Bauskulpturen und auch Wandmalereifragmente zu besichtigen. All dies zeugt von prächtig ausgestatteten Gebäuden.  \n" +
            "Eine 3D-Rekonstruktion der karolingischen Pfalz stellt uns freundlicherweise das Museum in der Kaiserpfalz zur Verfügung. Das dreieinhalbminütige Video können Sie nun auf Ihrem Display ansehen.\n Es visualisiert einen denkbaren Zustand der Pfalz im Jahre 799.", R.raw.a);

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
        LinkedList<Page> domPages = new LinkedList<>();


        domPages.add(new AppetizerPage("missing Appetizer",
                new Image(1, text1, "dom.jpg", "Die Paderborner Pfalz zu Zeiten Karls des Großen"), null));


        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio1));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio2));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio3));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio4));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio5));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio6));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio7));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio8));
        domPages.add(new ImagePage(new Image(1, text1, "pfalzbefestigungnach778_plan_gai_mecke2004.jpg", "Pfalzbefestigung"), null, null, audio9));

        domPages.add(new TextPage(lorem, null));

        List<ImagePage.Rectangle> imagePageRectangles = new LinkedList<>();
        imagePageRectangles.add(new ImagePage.Rectangle(100, 100, 300, 300));
        imagePageRectangles.add(new ImagePage.Rectangle(310, 310, 500, 700));
        List<String> imagePageTexts = new LinkedList<>();
        imagePageTexts.add("Area 1");
        imagePageTexts.add("Area 2");
        ImagePage imagePage = new ImagePage(new Image(1, lorem, "abdinghof.jpg", "Abdinghof"), imagePageTexts, imagePageRectangles, audio1);

        domPages.add(imagePage);

        List<Image> sliderImages = new LinkedList<>();
        List<Long> sliderTimes = new LinkedList<>();
        sliderImages.add(new Image(1, "image 1 desc gwegs ", "phasei.jpg", "Img1"));
        sliderTimes.add(776L);
        sliderImages.add(new Image(1, "image 2 desc gwegs ", "phaseii.jpg", "Img2"));
        sliderTimes.add(799L);
        sliderImages.add(new Image(1, "image 3 desc gwegs ", "phaseiii.jpg", "Img3"));
        sliderTimes.add(836l);
        // domPages.add(new TimeSliderPage("Slidertitle", lorem, audio3, sliderTimes, sliderImages));

        Exhibit e1 = new Exhibit(1, "Paderborner Dom", "Der Hohe Dom Ss. Maria, Liborius und Kilian ist" +
                " die Kathedralkirche des Erzbistums Paderborn und liegt im Zentrum der " +
                "Paderborner Innenstadt, oberhalb der Paderquellen.", 51.718953, 8.75583,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(1, "", "dom.jpg", ""), domPages);
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

