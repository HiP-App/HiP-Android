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

package de.upb.hip.mobile.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import de.upb.hip.mobile.activities.R;

/**
 * Helper Class for manipulation of images.
 */
public class ImageManipulation {

    /**
     * Converts a square image into a round image.
     *
     * @param bitmap the rectangular image
     * @param radius the radius of the output image
     * @return bitmap of round image
     */
    public static Bitmap getCroppedImage(Bitmap bitmap, int radius) {
        Bitmap scaledBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        } else {
            scaledBitmap = bitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(scaledBitmap.getWidth() / 2 + 0.7f, scaledBitmap.getHeight() / 2 + 0.7f,
                scaledBitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Converts a square image into a round image with marker for maps.
     *
     * @param bitmap  the rectangular image
     * @param context Android Context
     * @return image of marker with imput image inside
     */
    public static Bitmap getMarker(Bitmap bitmap, Context context) {
        bitmap = ImageManipulation.getCroppedImage(bitmap, 55);

        Bitmap markerBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.marker_blue);
        int markerBitmapWidth = markerBitmap.getWidth();
        int markerBitmapHeight = markerBitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        
        float marginLeft = (float) (markerBitmapWidth * 0.5 - bitmapWidth * 0.5);
        float marginTop = (float) 13;

        Bitmap overlayBitmap = Bitmap.createBitmap(
                markerBitmapWidth, markerBitmapHeight, markerBitmap.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(markerBitmap, new Matrix(), null);
        canvas.drawBitmap(bitmap, marginLeft, marginTop, null);

        return overlayBitmap;
    }
}
