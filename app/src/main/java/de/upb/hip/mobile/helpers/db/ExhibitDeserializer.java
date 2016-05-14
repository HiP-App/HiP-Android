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

import com.couchbase.lite.Document;
import com.google.gson.Gson;

import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.exhibit.Exhibit;

/**
 * A helper class for deserializing all objects related to exhibits
 * This class is specific to the CouchBase Database!
 */
public class ExhibitDeserializer {

    public static Exhibit deserializeExhibit(Document document) {
        return deserializeExhibit(document.getProperties());
    }


    public static Exhibit deserializeExhibit(Map<String, Object> properties) {
        Gson gson = new Gson();
        return gson.fromJson((String) properties.get(DBAdapter.KEY_DATA), Exhibit.class);
        /*int id = Integer.valueOf((String) properties.get(DBAdapter.KEY_ID));
        String name = (String) properties.get(DBAdapter.KEY_EXHIBIT_NAME);
        String description = (String) properties.get(DBAdapter.KEY_EXHIBIT_DESCRIPTION);
        double lat = (double) properties.get(DBAdapter.KEY_EXHIBIT_LAT);
        double lng = (double) properties.get(DBAdapter.KEY_EXHIBIT_LNG);
        String[] categories = null;
        if (properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES) instanceof String) {
            categories = ((String) properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES)).split(",");
        } else {
            categories = ((List<String>) properties.get(DBAdapter.KEY_EXHIBIT_CATEGORIES)).toArray(new String[0]);
        }
        String[] tags = null;
        if (properties.get(DBAdapter.KEY_EXHIBIT_TAGS) instanceof String) {
            tags = DBAdapter.KEY_EXHIBIT_TAGS.split(",");
        } else {
            tags = ((List<String>) properties.get(DBAdapter.KEY_EXHIBIT_TAGS)).toArray(new String[0]);
        }


        //TODO: Deserialize Image
        Image image = (Image) properties.get(DBAdapter.KEY_EXHIBIT_IMAGE);

        //TODO: Deserialize page list
        //List<Page> pages = (LinkedList<Page>) properties.get(DBAdapter.KEY_EXHIBIT_PAGES);
        //TODO: Remove hack
        return new Exhibit(id, name, description, lat, lng, categories, tags, image, new LinkedList<Page>());*/
    }
}
