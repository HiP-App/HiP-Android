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