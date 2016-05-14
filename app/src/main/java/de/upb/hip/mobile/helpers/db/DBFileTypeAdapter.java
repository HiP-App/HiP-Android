package de.upb.hip.mobile.helpers.db;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.models.DBFile;

/**
 * A GSON type adapter used to get a list of the DBFile objects that are getting serialized
 */
public class DBFileTypeAdapter implements JsonSerializer<DBFile> {

    private List<DBFile> files = new LinkedList<>();

    @Override
    public JsonElement serialize(DBFile src, Type typeOfSrc, JsonSerializationContext context) {
        files.add(src);
        return new Gson().toJsonTree(src);
    }

    public List<DBFile> getFiles() {
        return files;
    }
}