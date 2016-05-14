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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.upb.hip.mobile.adapters.DBAdapter;
import de.upb.hip.mobile.models.DBFile;
import de.upb.hip.mobile.models.Image;
import de.upb.hip.mobile.models.Route;

/**
 * A helper class for serializing all objects related to a route
 * This class is specific to the CouchBase Database!
 */
public class RouteSerializer {

    public static final String TAG = "route-serializer";

    public static void serializeRoute(Document document, Route route, Context mContext, DBDummyDataFiller filler) {
        Map<String, Object> properties = new HashMap<>();

        DBFileTypeAdapter dbFileTypeAdapter = new DBFileTypeAdapter();
        GsonBuilder builder = new GsonBuilder();
        //Register type adapter so we can get a list of all files in the database
        builder.registerTypeAdapter(Image.class, dbFileTypeAdapter);

        Gson gson = builder.create();
        String data = gson.toJson(route);
        properties.put(DBAdapter.KEY_DATA, data);

        properties.put(DBAdapter.KEY_TYPE, DBAdapter.TYPE_ROUTE);
        properties.put(DBAdapter.KEY_CHANNELS, "*");

        try {
            // Save the properties to the document
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting properties", e);
        }

        for (DBFile file : dbFileTypeAdapter.getFiles()) {
            final int resId = mContext.getResources().getIdentifier(file.getFilename().split("\\.")[0],
                    "drawable", mContext.getPackageName());
            if (resId != 0) {
                InputStream ress = mContext.getResources().openRawResource(+resId);
                //TODO: Determine MIME type
                filler.addAttachment(route.getId(), file.getFilename(), "image/jpeg", ress);
            } else {
                Log.e("routes", "Could not load image resource for route " + route.getId());
            }
        }
    }
}
