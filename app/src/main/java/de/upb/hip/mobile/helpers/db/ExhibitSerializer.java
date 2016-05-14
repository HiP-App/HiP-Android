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

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.exhibit.Exhibit;

/**
 * A helper class for serializing all objects related to exhibits
 * This class is specific to the CouchBase Database!
 */
public class ExhibitSerializer {


    public static void serializeExhibit(Document document, Exhibit exhibit) {
        Map<String, Object> properties = new HashMap<>();

        properties.put(DBAdapter.KEY_TYPE, "exhibit");
        properties.put(DBAdapter.KEY_EXHIBIT_NAME, exhibit.getName());
        properties.put(DBAdapter.KEY_EXHIBIT_DESCRIPTION, exhibit.getDescription());
        properties.put(DBAdapter.KEY_EXHIBIT_CATEGORIES, exhibit.getCategories());
        properties.put(DBAdapter.KEY_EXHIBIT_TAGS, exhibit.getTags());
        properties.put(DBAdapter.KEY_EXHIBIT_LAT, exhibit.getLatlng().latitude);
        properties.put(DBAdapter.KEY_EXHIBIT_LNG, exhibit.getLatlng().longitude);
        properties.put(DBAdapter.KEY_EXHIBIT_IMAGE, exhibit.getImage());
        properties.put(DBAdapter.KEY_EXHIBIT_PAGES, exhibit.getPages());


        //ensure access for all users in the Couchbase database
        properties.put(DBAdapter.KEY_CHANNELS, "*");

        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e("exhibit-serializer", e.toString());
        }
    }
}
