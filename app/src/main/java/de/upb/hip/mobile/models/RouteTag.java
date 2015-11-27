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

import de.upb.hip.mobile.adapters.DBAdapter;

/**
 * Represents a tag for a route
 */
public class RouteTag {

    private String tag;
    private String name;
    @JsonIgnore
    private Drawable image;
    private String imageFilename;

    public RouteTag(String tag, String name, String imageFilename) {
        this.tag = tag;
        this.name = name;
        this.imageFilename = imageFilename;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    /*public void setImage(Drawable image){
        this.image = image;
    }*/

    public Drawable getImage(int documentId, Context ctx) {
        if(image != null){
            return image;
        }
        Attachment att = DBAdapter.getAttachment(documentId, imageFilename);
        try {
            Bitmap b = BitmapFactory.decodeStream(att.getContent());
            image = new BitmapDrawable(ctx.getResources(), b);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }


        return image;
    }

    public String getImageFilename(){
        return imageFilename;
    }

}
