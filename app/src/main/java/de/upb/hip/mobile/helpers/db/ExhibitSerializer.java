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
import com.couchbase.lite.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.Audio;
import de.upb.hip.mobile.models.DBFile;
import de.upb.hip.mobile.models.Image;
import de.upb.hip.mobile.models.exhibit.Exhibit;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A helper class for serializing all objects related to exhibits
 * This class is specific to the CouchBase Database!
 */
public class ExhibitSerializer {
    public static final String TAG = "exhibit-serializer";

    public static final String CLASS_META_KEY = "CLASS_META_KEY";

    public static void serializeExhibit(Document document, Exhibit exhibit, Context mContext, DBDummyDataFiller filler) {
        Map<String, Object> properties = new HashMap<>();


        DBFileTypeAdapter dbFileTypeAdapter = new DBFileTypeAdapter();
        GsonBuilder builder = new GsonBuilder();
        //Register type adapter so we can get a list of all files in the database
        builder.registerTypeAdapter(Image.class, dbFileTypeAdapter);
        builder.registerTypeAdapter(Audio.class, dbFileTypeAdapter);

        //Pages need special handling since they are abstract
        builder.registerTypeAdapter(Page.class, new PageTypeAdapter());

        Gson gson = builder.create();
        String data = gson.toJson(exhibit);
        properties.put(DBAdapter.KEY_DATA, data);

        properties.put(DBAdapter.KEY_TYPE, DBAdapter.TYPE_EXHIBIT);
        properties.put(DBAdapter.KEY_CHANNELS, "*");

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

        for (DBFile file : dbFileTypeAdapter.getFiles()) {
            Log.i(TAG, "Saving file " + file.getFilename() + " to document " + document.getId());
            final int resId = mContext.getResources().getIdentifier(file.getFilename().split("\\.")[0],
                    "drawable", mContext.getPackageName());
            if (resId != 0) {
                InputStream ress = mContext.getResources().openRawResource(+resId);
                //TODO: Determine MIME type
                filler.addAttachment(exhibit.getId(), file.getFilename(), "image/jpeg", ress);
            } else {
                Log.e("routes", "Could not load image resource " + file.getFilename() + " for exhibit " + exhibit.getId());
            }
        }
    }

    private static class PageTypeAdapter implements JsonSerializer<Page> {
        @Override
        public JsonElement serialize(Page src, Type typeOfSrc, JsonSerializationContext context) {
            JsonElement jsonEle = context.serialize(src);
            jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, src.getClass().getCanonicalName());
            return jsonEle;
        }
    }
}
