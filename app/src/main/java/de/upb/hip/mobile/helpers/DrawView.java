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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!drawOnImage) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        for (ImagePage.Rectangle rect : rectangles) {
            //TODO: Take into account the scaling
            canvas.drawRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2(), paint);
        }
    }
}
