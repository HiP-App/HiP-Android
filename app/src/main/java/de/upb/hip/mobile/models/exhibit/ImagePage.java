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

package de.upb.hip.mobile.models.exhibit;

import java.io.Serializable;
import java.util.List;

import de.upb.hip.mobile.models.Audio;
import de.upb.hip.mobile.models.Image;

/**
 * A model class for a page containing a single mImage
 */
public class ImagePage extends Page implements Serializable {
    private final Image mImage;
    private final List<String> mTexts;
    private final List<Rectangle> mAreas;

    public ImagePage(Image mImage, List<String> mTexts, List<Rectangle> mAreas, Audio audio) {
        super(audio);
        this.mImage = mImage;
        this.mTexts = mTexts;
        this.mAreas = mAreas;
    }

    public Image getImage() {
        return mImage;
    }

    public List<String> getTexts() {
        return mTexts;
    }

    public List<Rectangle> getAreas() {
        return mAreas;
    }

    public static class Rectangle {
        private final int x1, y1; //top left corner
        private final int x2, y2; //bottom right corner

        public Rectangle(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public int getX1() {
            return x1;
        }

        public int getY1() {
            return y1;
        }

        public int getX2() {
            return x2;
        }

        public int getY2() {
            return y2;
        }
    }
}
