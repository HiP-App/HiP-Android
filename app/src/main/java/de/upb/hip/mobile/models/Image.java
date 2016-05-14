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
 * A model class for an image
 */
public class Image extends DBFile implements Serializable {
    private final String mDescription;
    private final String mTitle; //Called "name" in DB diagram

    //Do not try to serialize the image
    @JsonIgnore
    transient private Drawable mImage;

    public Image(int docId, String mDescription, String mFilename, String mTitle) {
        super(docId, mFilename);
        this.mDescription = mDescription;
        this.mTitle = mTitle;
    }

    public Drawable getDawableImage(Context ctx) {
        if (mImage == null) {
            Attachment attachment = DBAdapter.getAttachment(getDocumentId(), getFilename());

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(attachment.getContent());
                mImage = new BitmapDrawable(ctx.getResources(), bitmap);
            } catch (CouchbaseLiteException e) {
                Log.e("routes", e.toString());
            }
        }
        return mImage;
    }

    /**
     * Sets a dersialized drawable version of this image
     *
     * @param image
     */
    public void setImage(Drawable image) {
        mImage = image;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }


}
