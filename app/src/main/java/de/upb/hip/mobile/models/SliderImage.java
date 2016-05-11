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
 * Model for a single image in the imageslider
 */
public class SliderImage implements Serializable {

    private String mImageName;
    private int mYear;

    /**
     * Constructor
     * @param year
     * @param imageName
     */
    public SliderImage (int year, String imageName)
    {
        this.mYear = year;
        this.mImageName = imageName;
    }

    /**
     * Getter and Setter for private Variables
     */
    public String getImageName() {
        return this.mImageName;
    }

    public void setImageName(String imageName) {
        this.mImageName = imageName;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

}
