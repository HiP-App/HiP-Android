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
import de.upb.hip.mobile.models.exhibit.TimeSliderPage;

/**
 * This class adds dummy data to the DB until we can obtain real data from team CMS
 */
public class DBDummyDataFiller {


    public static final String TAG = "db-filler";


    private static final Audio audio1 = new Audio(R.raw.sprechertext_1, "Während des bisherigen Rundgangs haben Sie erfahren, wie wichtig das Gebiet zwischen Lippe und Pader für die Politik Karls des Großen ab den 770er Jahren war. Erinnern wir uns nur an die große Reichsversammlung im Jahre 777! Zu diesem Anlass fanden sich Franken, Sachsen, aber auch arabische Gesandte aus Spanien hier in Paderborn zusammen.\n" +
            "Aber was fanden diese Personen hier vor? Wie hat man sich das damalige Paderborn, die sogenannte „urbs Karoli“, eigentlich vorzustellen? Lange Zeit fragten sich Historiker und Archäologen, ob die „urbs Karoli“ tatsächlich in Paderborn existierte und wenn ja, wo genau? <fn> Einen Überblick über die hierzu geäußerten Vermutungen bietet Birgit Mecke: Der Stand der Forschungen vor den Grabungskampagnen Winkelmanns, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 1-8. </fn> Die karolingischen Geschichtswerke sprechen für Paderborn nicht von palatium,  \n" +
            "dem  \n" +
            "lateinischen Wort für „Pfalz“. Und es gab auch keine archäologischen Anhaltspunkte für die Pfalz Karls des Großen in Paderborn. Noch Mitte des 20. Jahrhunderts schrieb der Akademieprofessor und Domkapitular Alois Fuchs, dass „für eine [karolingische] Pfalz in Paderborn nicht nur alle urkundlichen Bezeugungen fehlen, sondern auch alle Baureste, die für die charakteristischen Pfalzgebäude, den Reichssaal und die Pfalzkapelle, sprechen könnten <fn>Alois A. Fuchs: Zur Frage der Bautätigkeit des Bischofs Badurad am Paderborner Dom, in: Westfälische Zeitschrift 97 (1947), S. 3-34, hier S. 5. </fn>.“  Sichtbar waren einzig verbaute Überreste der Domburg Bischof Meinwerks aus dem 11. Jahrhundert. Diese Überreste hatten bereits Mitte des 19. Jahrhunderts das Interesse von Lokalforschern geweckt <fn>Vgl. J. B. Johann Bernhard Greve: Der kaiserliche und bischöfliche Palast in Paderborn, in: Blätter zur näheren Kunde Westfalens 6/4 (1868), S. 33-38.</fn>. \n" +
            "Jetzt stehen Sie zwischen dem Dom und dem Museum in der Kaiserpfalz. Dieses große und repräsentative Gebäude mit den Rundbogenfenstern sieht so aus, wie man sich eine Kaiserpfalz vorstellt. Doch handelt es sich dabei um die Pfalz Karls des Großen? Nein! Es ist die archäologische Rekonstruktion der Pfalz Bischof Meinwerks aus dem frühen 11. Jahrhundert. \n Aber wo befand sich nun die karolingische Kaiserpfalz? Sehen Sie die etwa 31 mal 10 m große, rechteckige Fläche zwischen Ihnen und dem Museum? Sie ist durch Bruchsteinmauern abgegrenzt. Das sind die aus konservatorischen Gründen aufgemauerten Fundamente der sog. aula regia, der Königshalle Karls des Großen. Wenn Sie genau hinschauen, sehen Sie ein rotes Ziegelband. Dieses trennt das originale Bruchsteinmauerwerk von später, im Zuge der Rekonstruktion ergänzten Steinen.");

    private static final Audio audio2_1 = new Audio(R.raw.sprechertext_2_1, "Falls Sie Schwierigkeiten haben sollten, sich in diesem Mauergewirr zurechtzufinden, blicken Sie auf Ihr Display. Hier werden die Mauern der aula regia rot hervorgehoben.");
    private static final Audio audio2_2 = new Audio(R.raw.sprechertext_2_2, "Vor hundert Jahren hätten Sie davon noch nichts sehen können. Denn dieses Gelände war damals mit Fachwerkhäusern bebaut, die im 2. Weltkrieg zerstört wurden. Einen Eindruck, wie sich das Gelände nördlich des Doms verändert hat, bietet der Fotoslider auf Ihrem Display. Die Kriegszerstörungen boten aber auch neue Möglichkeiten. So legte man ab den 1950er Jahren Teile der karolingischen Befestigungsmauer frei und konnte so ihren Verlauf rekonstruieren.");

    private static final Audio audio3_1 = new Audio(R.raw.sprechertext_3_1, "Auf Ihrem Display sehen Sie nun einen Plan der Paderborner Innenstadt. Hier ist der Verlauf der karolingischen Befestigungsmauer unter Berücksichtigung der Grabungsergebnisse von Marianne Moser eingezeichnet <fn>Vgl. Sveva Gai: Die Errichtung einer Befestigungsanlage, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 95-102, unter Berücksichtigung von Marianne Moser: Neue Beobachtungen zu Struktur und Entwicklung der Domburgbefestigung. Eine kritische Betrachtung bisheriger Interpretationen aufgrund der Zusammenschau zahlreicher Hinweise. Unveröffentlichtes Manuskript.</fn>.  Sie sehen, dass die Mauer ein annähernd quadratisches Areal von 280 mal 250 m um den heutigen Dom einfasste. Diese Kalkbruchsteinmauer ersetzte die frühere Holz-Erde-Konstruktion nach der Zerstörung der urbs Karoli durch die Sachsen im Jahre 778. Eine solche gemauerte Befestigung war zu dieser Zeit unüblich. In Paderborn war sie aber aufgrund der Sachsenkriege zwingend notwendig.\n" +
            "Aber zurück zu unserem stark zerstörten Gelände der Nachkriegszeit. Es sollte 1963 neu erschlossen werden. Bei Baggerarbeiten stieß man auf ältere Gebäudemauern. Deshalb entschloss man sich, das gesamte Areal archäologisch zu untersuchen. Die systematische Freilegung begann 1964 und wurde 14 Jahre lang unter der Leitung des Archäologen Wilhelm Winkelmann fortgeführt <fn> Einen Überblick über den Fortgang und die Dokumentation der Ausgrabungen vermittelt Birgit Mecke: Die Ausgrabungen der Jahre 1963 bis 1978, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 9-44. </fn>.");

    private static final Audio audio3_2 = new Audio(R.raw.sprechertext_3_2, "Einen Überblick über die verschiedenen mittelalterlichen Gebäudereste aus unterschiedlichen Jahrhunderten bietet die Fotografie, die Sie nun auf Ihrem Display sehen können. Sie wurde Mitte der 60er Jahre während der Ausgrabungen vom Domturm herab gemacht und zeigt die gesamte Grabungsfläche nördlich des Domes. Die Ausgräber legten nicht nur die ottonisch-salische Pfalz frei - hier gelb markiert -, sondern sie fanden auch die Überreste der karolingischen Pfalz - hier rot markiert.\n" +
            "Und genau inmitten dieses Areals stehen Sie nun. Eine Abbildung in Ihrer App hilft Ihnen, die im Folgenden beschriebenen baulichen Elemente der Karlspfalz zu identifizieren.");

    private static final Audio audio4_1 = new Audio(R.raw.sprechertext_4_1, "Kommen wir wieder auf die aula regia Karls des Großen zurück. Wie Sie anhand der Bruchsteinmauern sehen, handelte es sich um ein einfaches rechteckiges Steingebäude. Auf der Ihnen zugewandten Seite, der Südseite, befanden sich zwei Eingänge, die zu den Wirtschaftsräumen führten <fn> Vgl. Sveva Gai: Der Bau der Aula (776), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 103-114, hier S. 103. </fn>. Trotz der geringen Größe wird es seiner Zeit die Sachsen vor Ort in Staunen versetzt haben, waren Steinbauten in dieser Region doch eine absolute Ausnahme <fn> Vgl. Sveva Gai: Die Pfalzenarchitektur der Karolingerzeit, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 185-198, hier S. 195. </fn>.  Und noch etwas muss sie überrascht haben: Dieses Gebäude aus Stein war zweigeschossig. Spätestens nach der Zerstörung beim Sachsenaufstand 778 hatte die wiederaufgebaute aula regia ein repräsentatives Obergeschoss und ein kellerartiges Untergeschoss mit fünf Wirtschaftsräumen <fn> Vgl. Sveva Gai: Der Bau der Aula (776), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 103-114, hier S. 107. </fn>.  Sichtbar ist heute nur noch der Grundriss im Untergeschoss. Sie müssen sich vorstellen, dass das Gebäude vermutlich fünf bis sechs Meter in die Höhe ragte. Ein für diese Zeit sicherlich beeindruckender Bau! Sehen Sie das viereckige Fundament an der Südwest-Ecke, d.h. zu Ihrer Linken?");

    private static final Audio audio4_2 = new Audio(R.raw.sprechertext_4_2, "Es sind vermutlich die Fundamentreste eines Balkons oder Altans <fn> Vgl. Sveva Gai: Der Bau der Aula (776), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 103-114, hier S. 113. </fn>.  Ein Balkon oder Altan, d.h. ein „vom Erdboden aus gestützter balkonartiger Anbau“ <fn> Art. Altan, in: DUDEN. Fremdwörterbuch, 6., auf der Grundlage der amtlichen Neuregelung der deutschen Rechtschreibung überarbeitete und erweiterte Auflage, Mannheim u.a.,1997, S. 53. </fn>,  diente dem Herrscher zum repräsentativen Auftritt: Hier konnte er direkt aus dem Festsaal, der aula regia, im Obergeschoss vor sein Gefolge treten und aus erhöhter Position sprechen. Dies war ein deutliches Zeichen seiner Königswürde. Dabei stellte sich auch schon früh die Frage, wo Karl der Große im Festsaal Platz nahm. Doch dazu gibt es leider weder Aussagen in Geschichtswerken noch einen konkreten archäologischen Befund. Vermutlich saß Karl auf einem mobilen, klappbaren Thron, der entweder auf der nördlichen Längsseite – gegenüber dem Eingang – oder auf einer der Schmalseiten im Obergeschoss gestanden haben könnte <fn> Vgl. Sveva Gai: Der Bau der Aula (776), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 103-114, hier S. 107-113. </fn>. \n" +
            "Schauen Sie nach rechts auf die gegenüberliegende, südöstliche Seite. Sehen Sie die dunkelgraue Pflastersteine in Höhe des heutigen Bodenniveaus? Sie markieren die zwei parallel verlaufenden Mauern, die den repräsentativen Zugang zum Obergeschoss bildeten <fn> Vgl. Sveva Gai: Der Wiederaufbau der Pfalz mit der Errichtung des östlichen Zugangs zur Aula. Phase Ib, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 120-121. </fn>.");

    private static final Audio audio5_1 = new Audio(R.raw.sprechertext_5_1, "Eine Vorstellung, wie diese aula regia bei der Reichsversammlung im Jahre 785 ausgesehen haben könnte, vermittelt Ihnen die Zeichnung der Archäologinnen Sveva Gai und Birgit Mecke auf Ihrem Display.\n" +
            "Aber zurück zur dunkelgrauen Pflasterung... Sehen Sie vielleicht noch andere Stellen, die diese Pflasterung aufweisen? Ja, genau. Neben der Bartholomäuskapelle ist der Mauerverlauf eines weiteren karolingischen Gebäudes so gekennzeichnet. Im Gegensatz zur aula regia können Sie diesen Ort begehen. Hier stand vor 799 eine Kirche. Sie war dem Salvator, <fn> Annales Sangallenses Baluzii ad a. 777, ed. von Georg Heinrich Pertz (MGH SS 1), Hannover 1826, S. 63: hoc anno fuit domnus rex Karlus in Saxonia ad Patrisbrunna, et ibi aedificavit ecclesiam in honore Salvatoris. </fn>  also Jesus Christus, geweiht, ungefähr so groß wie die aula regia und ebenfalls aus Kalkbruchstein errichtet. Der Bau einer steinernen Kirche ist auch eine politische Aussage. Mitten im heidnischen Sachsen baute man schon früh einen repräsentativen Sakralbau, der vermutlich zugleich als Pfalz- und Missionskirche diente <fn> Vgl. Sveva Gai: Die Salvatorkirche mit dem sog. „Atrium“ (777), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 115-117, hier S. 116. </fn>.");

    private static final Audio audio5_2 = new Audio(R.raw.sprechertext_5_2, "Wie Sie sicherlich schon bemerkt haben, zeichnet die Pflasterung keinen vollständigen Grundriss nach. Die Linien verschwinden unter dem heutigen Dom. Den gesamten Grundriss dieser Salvatorkirche können Sie nun auf Ihrem Display sehen. Es handelte sich vermutlich um eine einschiffige Saalkirche, deren rechteckiger Chor im Osten dreigeteilt war. Im Westen schloss die Kirche mit einem rechteckigen Westbau ab, dessen Gestalt unklar bleibt.");

    private static final Audio audio5_3 = new Audio(R.raw.sprechertext_5_3, "Auf Ihrem Display sehen Sie nun den neuesten Rekonstruktionsvorschlag der Paderborner Stadtarchäologin Sveva Gai. Sie vermutet, dass es sich um einen doppelgeschossigen Baukörper handelte <fn> Vgl. Sveva Gai: Die Salvatorkirche mit dem sog. „Atrium“ (777), in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 115-117, hier S. 116. </fn>. Im Untergeschoss wurden 16 Gräber von Männern, Frauen und Kindern freigelegt. Die Bestattung im Westbau der Kirche war ein „besonder[s], privilegierte[r] Bestattungsplatz“ <fn> Vgl. Sveva Gai: Bauliche Veränderungen im Kirchenbereich nach der Zerstörung von 778. Phase Ib, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 122-124, hier S. 122. </fn>.  Weitere hunderte von Gräbern fanden sich auch südlich der damaligen Salvatorkirche.");

    private static final Audio audio6 = new Audio(R.raw.sprechertext_6, "Neben diesen beiden Steinbauten gibt es in der frühen Pfalzanlage auch Hinweise für zahlreiche Holzbauten und die Anwesenheit verschiedener Handwerker, unter ihnen Maurer, Steinmetze, Schmiede, Glasmacher und Bleigießer <fn> Vgl. Sveva Gai: Werkstätten und Pfostenbebauungen, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 117-119, hier S. 117. </fn>.  Besonders während der 770er Jahre waren diese Handwerker unentbehrlich. Sie halfen beim Bau der Anlage und zogen später weiter zu neuen Auftraggebern – die Mobilität der Menschen zu dieser Zeit ist nicht zu unterschätzen!\n" +
            "Viele Handwerker wurden wieder gegen Ende des 8. Jahrhunderts gebraucht. Damals beschloss man nämlich die Erweiterung der aula regia. Im Nordwesten wurde ein steinerner Wohntrakt angebaut <fn> Vgl. Sveva Gai: Der nördliche Wohntrakt, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 129-130. </fn>.  Die Salvatorkirche wurde durch die „ecclesiam mira[e] magnitudinis“ <fn> Annales Laureshamenses ad a. 799, ed. von Georg Heinrich Pertz (MGH SS 1), Hannover 1826, S. 38: (...) et postea cum pace et honore magno eum remisit ad propriam sedem (…) et [rex] ibi ad Padresbrunnun aedificavit ecclesiam mira magnitudinis, et fecit eam dedicare (...). </fn>,  das heisst eine „Kirche von wunderbarer Größe“ ersetzt. Die nun der Gottesmutter und dem hl. Kilian geweihte Kirche maß tatsächlich 21 x 42,7m und gehörte somit zu den großen Kirchenbauten des Frankenreiches, vergleichbar etwa der Klosterkirche in Lorsch oder der Abteikirche in Saint-Denis. <fn> Vgl. Uwe Lobbedey: Der Paderborner Dom. Vorgeschichte, Bau und Fortleben einer westfälischen Bischofskirche, München / Berlin 1990, S. 14f. </fn>");

    private static final Audio audio7_1 = new Audio(R.raw.sprechertext_7_1, "Wie sah die karolingische Pfalz um 800 also aus? Schauen Sie dazu bitte auf Ihr Display. Sie sehen dort den Plan der Anlage zu dieser Zeit. Im unteren Bereich können Sie den Grundriss der neugebauten Kirche „von wunderbarer Größe“ erkennen. Ihre Fundamentmauern sind unter dem heutigen Dom noch erhalten. [Für weiterführende Informationen zur Besichtigung der Ausgrabungen klicken Sie bitte hier] Bei der Kirche handelte es sich um eine dreischiffige Basilika ohne Querhaus, die an die Tradition spätantiker Kirchenbauten anknüpfte <fn> Vgl. Sveva Gai: Der Bau der ecclesia mirae magnitudinis, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 130-133, hier S. 131. </fn>.  Solche basilikalen Großbauten waren den zentralen Pfalzen vorbehalten <fn> Vgl. Gerhard G. Streich: Burg und Kirche während des deutschen Mittelalters. Untersuchungen zur Sakraltopographie von Pfalzen, Burgen und Herrensitzen (Vorträge und Forschungen, Sonderband 29 I, II), 2 Bde., Sigmaringen 1984, hier Bd. 1, S. 36. </fn>.  Hierin zeigt sich die Aufwertung der Paderborner Pfalz am Ende der Sachsenkriege. Sie sehen, wie sich die urbs Karoli innerhalb zweier Jahrzehnte zu einem zentralen Ort in Sachsen entwickelt hat. Im Vergleich zu Pfalzen im fränkischen Kernland, wie etwa Aachen oder Ingelheim, war sie jedoch von geringem Ausmaß.");

    private static final Audio audio7_2 = new Audio(R.raw.sprechertext_7_2, "Einen Eindruck der Größenverhältnisse vermittelt Ihnen der Slider.");

    private static final Audio audio8 = new Audio(R.raw.sprechertext_8, "Nun haben Sie bereits viele Pläne und Rekonstruktionen der Gebäude gesehen. Aber weiß man denn, wie sie innen aussahen?\n" +
            "Eine Vielzahl archäologischer Funde erlaubt Rückschlüsse auf die Ausstattung der Gebäude. Allerdings lassen sich diese Funde nicht bestimmten Gebäuden zuordnen, da sie in großflächigen Schuttablagerungen gefunden wurden. Diese Schuttablagerungen fielen rund zweihundert Jahre später an, als die karolingische Pfalz abgetragen wurde, um unter Bischof Meinwerk neuen Gebäuden Raum zu geben <fn> Vgl. Matthias Preißler: Die karolingischen Malereifragmente aus Paderborn. Zu den Putzfunden aus der Pfalzanlage Karls des Großen. Archäologie der Wandmalerei (Denkmalpflege und Forschung in Westfalen 40,1), Mainz 2003, S. 130. </fn>.  Im Museum in der Kaiserpfalz können Sie eine Auswahl dieser Funde sehen. Dazu gehören etwa geritzte Ziegelplatten, die vermutlich zur Trauf- oder Eckverzierung kleinerer Bauglieder dienten. Ferner Fragmente von Fensterglas und die dazugehörigen Bleistege. Sie werden in der Fachsprache auch Bleiruten genannt. Mit ihnen wurden die einzelnen Glasteile des Fensters ehemals zusammengehalten. Darüber hinaus sind unterschiedliche Bauskulpturen und auch Wandmalereifragmente zu besichtigen. All dies zeugt von prächtig ausgestatteten Gebäuden <fn> Vgl. Birgit Mecke: Zur Ausstattung der karolingischen Pfalzanlage: Architektonische Gestaltung und Einrichtung, in: Sveva Gai / Birgit Mecke: Est locus insignis…: Die Pfalz Karls des Großen in Paderborn und ihre bauliche Entwicklung bis zum Jahr 1002. Die Neuauswertung der Ausgrabungen Wilhelm Winkelmanns in den Jahren 1964-1978 (Denkmalpflege und Forschung in Westfalen 40,2), Mainz 2004, Bd. 1, S. 173-184. </fn>.  \n" +
            "Eine 3D-Rekonstruktion der karolingischen Pfalz stellt uns freundlicherweise das Museum in der Kaiserpfalz zur Verfügung. Das dreieinhalbminütige Video können Sie nun auf Ihrem Display ansehen.\n Es visualisiert einen denkbaren Zustand der Pfalz im Jahre 799.");


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


        kaiserpfalzPages.add(new AppetizerPage("Ein befestigter Stützpunkt in Sachsen – Aufbau und Entwicklung.",
                new Image(1, text1, "kaiserpfalz_teaser.jpg", "Die Pfalz Karls des Großen"), null));


        kaiserpfalzPages.add(new ImagePage(new Image(1, "Die Paderborner Kaiserpflaz mit dem Betrachterstandpunkt.", "kaiserpfalz_teaser.jpg", "Kaiserpfalz"), null, null, audio1));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Betrachterstandpunkts mit rot eingefärbten Mauern der aula regia.", "kaiserpfalz_image_2.jpg", "Betrachterstandpunkt"), null, null, audio2_1));

        List<Image> slider1 = new LinkedList<>();
        List<Long> sliderTimes1 = new LinkedList<>();
        slider1.add(new Image(1, "Blick von Norden auf die Paderquellen vor 1945", "kaiserpfalz_slider_1_1.jpg", "Img1"));
        sliderTimes1.add(0L);
        slider1.add(new Image(1, "Blick von Norden über die Treppe vor 1945 ", "kaiserpfalz_slider_1_2.jpg", "Img1"));
        sliderTimes1.add(1L);
        slider1.add(new Image(1, "Blick von Südwesten vor 1935", "kaiserpfalz_slider_1_3.jpg", "Img1"));
        sliderTimes1.add(2L);
        slider1.add(new Image(1, "Blick von Norden vor 1945", "kaiserpfalz_slider_1_4.jpg", "Img1"));
        sliderTimes1.add(3L);
        slider1.add(new Image(1, "Blick von Norden nach 1945", "kaiserpfalz_slider_1_5.jpg", "Img1"));
        sliderTimes1.add(4L);
        kaiserpfalzPages.add(new TimeSliderPage("Gelände nördlich des Doms", "Fotografien des Geländes nördlich des Domes, zu Beginn des 20. Jh., in den 1930er Jahren, in den 1950er Jahren und während der Grabung, Gai / Mecke 2004, S. 8, Abb. 5 und 6, S. 9, Abb. 7, S. 14, Abb. 11, S. 15, Abb. 12.", audio2_2, sliderTimes1, slider1, true));


        kaiserpfalzPages.add(new ImagePage(new Image(1, "Die vermutliche Ausdehnung der Befestigungsanlage in karolingischer Zeit.", "kaiserpfalz_image_3_1.jpg", "Befestigungsanlage"), null, null, audio3_1));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Blick vom Dom auf die Grabungsfläche der Paderborner Pfalz, AK Paderborn 1999, Bd. 3, S. 178, Abb. 3.", "kaiserpfalz_image_3_2.jpg", "Grabungsfläche"), null, null, audio3_2));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "", "kaiserpfalz_image_4.jpg", "Plan der Phasen I und Ib"), null, null, audio4_1));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "", "kaiserpfalz_image_4.jpg", "Plan der Phasen I und Ib"), null, null, audio4_2));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Rekonstruktion der Aula , Phase Ib, Gai / Mecke 2004, S. 120, Abb. 68.", "kaiserpfalz_image_5_1.jpg", "Rekonstruktion der Aula"), null, null, audio5_1));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Grundrisses der Salvatorkirche, Gai / Mecke 2004, Bd. 1, S. 108.", "kaiserpfalz_image_5_2.jpg", "Grundrisses der Salvatorkirche"), null, null, audio5_2));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Rekonstruktion der Salvatorkirche, Gai / Mecke 2004, Bd. 1, S. 122.", "kaiserpfalz_image_5_3.pg", "Rekonstruktion der Salvatorkirche"), null, null, audio5_3));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Die Paderborner Kaiserpflaz mit dem Betrachterstandpunkt.", "kaiserpfalz_teaser.jpg", "Kaiserpfalz"), null, null, audio6));
        kaiserpfalzPages.add(new ImagePage(new Image(1, "Plan der karolingischen Pfalzanlage um 800, Gai / Mecke 2004, Bd. I, S. 132, Abb. 77.", "kaiserpfalz_image_7_1.jpg", "karolingische Pfalzanlage"), null, null, audio7_1));


        List<Image> slider2 = new LinkedList<>();
        List<Long> sliderTimes2 = new LinkedList<>();
        slider2.add(new Image(1, "Pfalzanlage Phase II 799", "kaiserpfalz_slider_7_1.jpg", "Img1"));
        sliderTimes2.add(0L);
        slider2.add(new Image(1, "Aachen Kaiserpfalz Grundriß Keller", "kaiserpfalz_slider_7_2.jpg", "Img1"));
        sliderTimes2.add(1L);
        slider2.add(new Image(1, "Ingelheim Kaiserpfalz Schematischer Gesamtplan", "kaiserpfalz_slider_7_3.jpg", "Img1"));
        sliderTimes2.add(2L);
        kaiserpfalzPages.add(new TimeSliderPage("Grundrisse", "maßstabsgetreue Grundrisse von Aachen, Ingelheim und Paderborn, AK Aachen 2014, Bd. ****, Abb. **** und ****, Gai / Mecke 2004, Bd. I, S. 132, Abb. 77.", audio7_2, sliderTimes2, slider2, true));


        kaiserpfalzPages.add(new ImagePage(new Image(1, "Die Paderborner Kaiserpflaz mit dem Betrachterstandpunkt.", "kaiserpfalz_teaser.jpg", "Kaiserpfalz"), null, null, audio8));



        Exhibit kaiserpfalz = new Exhibit(1, "Die Pfalz Karls des Großen", "", 51.7189826, 8.754652599999986,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(1, "", "kaiserpfalz_teaser.jpg", ""), kaiserpfalzPages);


        LinkedList<Page> mariensaeulePages = new LinkedList<>();
        mariensaeulePages.add(new AppetizerPage("Startpunkt der Rundgänge: Hl. Liborius, Karl der Große, Meinwerk von Paderborn.",
                new Image(2, text1, "mariensaeule_teaser.jpg", "Die Mariensäule"), null));
        Exhibit mariensaeule = new Exhibit(2, "Die Mariensäule", "", 51.716724, 8.752244000000019,
                new String[]{"Kirch"}, new String[]{"Do"}, new Image(2, "", "mariensaeule_teaser.jpg", ""), mariensaeulePages);
        insertExhibit(mariensaeule);


        LinkedList<Page> paderquellen1Pages = new LinkedList<>();
        paderquellen1Pages.add(new AppetizerPage("Die Siedlung an den 200 Quellen. Woher kommt eigentlich der Name Paderborn?",
                new Image(3, text1, "quellen1_teaser.jpg", "Paderbrunnon, Patresbrun, Paderbrunno"), null));
        Exhibit paderquellen1 = new Exhibit(3, "Paderbrunnon, Patresbrun, Paderbrunno", "", 51.71861412677083, 8.75122457742691,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(3, "", "quellen1_teaser.jpg", ""), paderquellen1Pages);
        insertExhibit(paderquellen1);

        LinkedList<Page> paderquellen2Pages = new LinkedList<>();
        paderquellen2Pages.add(new AppetizerPage("Paderborn – so schön wie das Land, in dem Milch und Honig fließen?",
                new Image(4, text1, "quellen2_teaser.jpg", "Leben am Wasser"), null));
        Exhibit paderquellen2 = new Exhibit(4, "Leben am Wasser", "", 51.718811867802174, 8.751070350408554,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(4, "", "quellen2_teaser.jpg", ""), paderquellen2Pages);
        insertExhibit(paderquellen2);

        LinkedList<Page> paderquellen3Pages = new LinkedList<>();
        paderquellen3Pages.add(new AppetizerPage("Donar, Wotan und Saxnot – die Abkehr von den alten Göttern.",
                new Image(5, text1, "quellen3_teaser.jpg", "Taufen an der Pader?"), null));
        Exhibit paderquellen3 = new Exhibit(5, "Taufen an der Pader?", "", 51.71955795852887, 8.751071691513062,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(5, "", "quellen3_teaser.jpg", ""), paderquellen3Pages);
        insertExhibit(paderquellen3);

        LinkedList<Page> brueckePages = new LinkedList<>();
        brueckePages.add(new AppetizerPage("Karl der Große und die Sachsen – dreißig Jahre Krieg!",
                new Image(6, text1, "bruecke_teaser.jpg", "Sachsenkriege"), null));
        Exhibit brueckeBrauhaus = new Exhibit(6, "Sachsenkriege", "", 51.719582883396335, 8.751005977392197,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(6, "", "bruecke_teaser.jpg", ""), brueckePages);
        insertExhibit(brueckeBrauhaus);

        LinkedList<Page> gartenPages = new LinkedList<>();
        gartenPages.add(new AppetizerPage("Karls neue Strategie: Tod oder Taufe?",
                new Image(7, text1, "garten_teaser.jpg", "Christianisierung der Sachsen"), null));
        Exhibit geisselscherGarten = new Exhibit(7, "Christianisierung der Sachsen", "", 51.72050841708062, 8.75171273946762,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(7, "", "garten_teaser.jpg", ""), gartenPages);
        insertExhibit(geisselscherGarten);

        LinkedList<Page> bibliothekPages = new LinkedList<>();
        bibliothekPages.add(new AppetizerPage("Sachsen wird Teil des Frankenreiches.",
                new Image(8, text1, "bibliothek_teaser.jpg", "Karls Sieg über die Sachsen"), null));
        Exhibit stadtbibliothek = new Exhibit(8, "Karls Sieg über die Sachsen", "", 51.718953, 8.75583,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(8, "", "bibliothek_teaser.jpg", ""), bibliothekPages);
        insertExhibit(stadtbibliothek);

        LinkedList<Page> domPages = new LinkedList<>();
        domPages.add(new AppetizerPage("Der Blick auf Karl den Großen: Christ, Frankenkönig, Imperator, Heiliger.",
                new Image(9, text1, "dom_teaser.jpg", "Karl der Große im Wandel der Zeit"), null));
        Exhibit dom = new Exhibit(9, "Karl der Große im Wandel der Zeit", "", 51.7199006, 8.754952000000003,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(9, "", "dom_teaser.jpg", ""), domPages);
        insertExhibit(dom);

        LinkedList<Page> theoPages = new LinkedList<>();
        theoPages.add(new AppetizerPage("Was hat Karl der Große mit Schule, Schrift und Bildung zu tun?",
                new Image(10, text1, "theo_teaser.jpg", "Karl der Große macht Schule!"), null));
        Exhibit theodoranium = new Exhibit(10, "Karl der Große macht Schule!", "", 51.71601, 8.754249999999956,
                new String[]{"Kirche"}, new String[]{"Dom"}, new Image(10, "", "theo_teaser.jpg", ""), theoPages);
        insertExhibit(theodoranium);


        insertExhibit(kaiserpfalz);

        LinkedList<Waypoint> waypoints = new LinkedList<>();
        waypoints.add(new Waypoint(51.7189826, 8.754652599999986, 1));
        waypoints.add(new Waypoint(51.71601, 8.754249999999956, 10));
        waypoints.add(new Waypoint(51.716724, 8.752244000000019, 2));
        waypoints.add(new Waypoint(51.71861412677083, 8.75122457742691, 3));
        waypoints.add(new Waypoint(51.718811867802174, 8.751070350408554, 4));
        waypoints.add(new Waypoint(51.71955795852887, 8.751071691513062, 5));
        waypoints.add(new Waypoint(51.719582883396335, 8.751005977392197, 6));
        waypoints.add(new Waypoint(51.72050841708062, 8.75171273946762, 7));
        waypoints.add(new Waypoint(51.7199006, 8.754952000000003, 9));
        waypoints.add(new Waypoint(51.718953, 8.75583, 8));
        waypoints.add(new Waypoint(51.7189826, 8.754652599999986, -1));

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

