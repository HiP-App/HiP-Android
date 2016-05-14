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

import java.io.Serializable;

/**
 * Model class for the route tag.
 */
public class RouteTag implements Serializable {

    private String mTag;
    private String mName;
    private Image mImage;

    /**
     * Constructor for the RouteTag model.
     *
     * @param tag           Internal name of the tag.
     * @param name          Displayed name of the tag in the app.
     * @param image         The image of the tag.
     */
    public RouteTag(String tag, String name, Image image) {
        this.mTag = tag;
        this.mName = name;
        this.mImage = image;
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

    public Image getImage() {
        return mImage;
    }

    /**
     * Getter for the name of the image of the tag.
     *
     * @return Name of the image of the tag.
     */
    public String getImageFilename() {
        return mImage.getFilename();
    }
}
