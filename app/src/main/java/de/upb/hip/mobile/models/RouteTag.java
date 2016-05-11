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

package de.upb.hip.mobile.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Model class for the route tag.
 */
public class RouteTag implements Serializable {

    private String mTag;
    private String mName;
    private String mImageFilename;

    //Do not try to serialize the image
    @JsonIgnore
    transient private Drawable mImage;

    /**
     * Constructor for the RouteTag model.
     *
     * @param tag           Internal name of the tag.
     * @param name          Displayed name of the tag in the app.
     * @param imageFilename Name of the image of the tag.
     */
    public RouteTag(String tag, String name, String imageFilename) {
        this.mTag = tag;
        this.mName = name;
        this.mImageFilename = imageFilename;
    }

    /**
     * Getter for the internal tag name.
     *
     * @return Internal tag name.
     */
    public String getTag() {
        return mTag;
    }

    /**
     * Getter for the displayed tag name.
     *
     * @return Displayed tag name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Getter for the tag image. Gets the image from the database on the first call of this method.
     *
     * @param routeId ID of the route.
     * @param context The android application context
     * @return Image Drawable
     */
    public Drawable getImage(int routeId, Context context) {
        if (mImage != null) {
            return mImage;
        }

        Attachment attachment = DBAdapter.getAttachment(routeId, mImageFilename);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(attachment.getContent());
            mImage = new BitmapDrawable(context.getResources(), bitmap);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }

        return mImage;
    }

    /**
     * Getter for the name of the image belonging to the tag.
     * IMPORTANT: Can only be called if getmImage(routeId, imageFilename) was called at least once.
     * Else returns null.
     *
     * @return image Drawable
     */
    public Drawable getImage() {
        return mImage;
    }

    /**
     * Getter for the name of the image of the tag.
     *
     * @return Name of the image of the tag.
     */
    public String getImageFilename() {
        return mImageFilename;
    }
}
