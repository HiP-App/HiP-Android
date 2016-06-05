package de.upb.hip.mobile.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.models.exhibit.ImagePage;

/**
 * An encapsulation class for ImageView that allows drawing on it
 */
public class DrawView extends ImageView {

    private boolean drawOnImage = true;
    private List<ImagePage.Rectangle> rectangles = new LinkedList<>();

    //Needed for scaling the drawed rectangles to the correct size
    private int[] originalImageDimensions = new int[]{1, 1};


    public DrawView(Context context) {
        super(context);
    }

    DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isDrawOnImage() {
        return drawOnImage;
    }

    public void setDrawOnImage(boolean drawOnImage) {
        this.drawOnImage = drawOnImage;
    }

    public List<ImagePage.Rectangle> getRectangles() {
        return rectangles;
    }

    public void setOriginalImageDimensions(int[] originalImageDimensions) {
        this.originalImageDimensions = originalImageDimensions;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!drawOnImage) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        for (ImagePage.Rectangle rect : rectangles) {
            double widthScalingFactor = (double) getWidth() / (double) originalImageDimensions[0];
            double heightScalingFactor = (double) getHeight() / (double) originalImageDimensions[1];

            canvas.drawRect((int) (rect.getX1() * widthScalingFactor),
                    (int) (rect.getY1() * heightScalingFactor),
                    (int) (rect.getX2() * widthScalingFactor),
                    (int) (rect.getY2() * heightScalingFactor),
                    paint);
            ;
        }
    }
}
