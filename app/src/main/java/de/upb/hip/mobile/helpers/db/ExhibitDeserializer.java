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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A helper class for deserializing all objects related to exhibits
 * This class is specific to the CouchBase Database!
 */
public class ExhibitDeserializer {

    public static Exhibit deserializeExhibit(Document document) {
        return deserializeExhibit(document.getProperties());
    }


    public static Exhibit deserializeExhibit(Map<String, Object> properties) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Page.class, new PageDeserializer());
        Gson gson = builder.create();
        return gson.fromJson((String) properties.get(DBAdapter.KEY_DATA), Exhibit.class);
    }

    private static class PageDeserializer implements JsonDeserializer<Page> {
        private Gson gson = new Gson();

        @Override
        public Page deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //Need evil hacks since Gson can't deserialize abstract Pages on its own
            JsonObject jsonObj = json.getAsJsonObject();
            String className = jsonObj.get(ExhibitSerializer.CLASS_META_KEY).getAsString();
            try {
                Class<?> clz = Class.forName(className);
                return context.deserialize(json, clz);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }
    }
}
