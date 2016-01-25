package de.upb.hip.mobile.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.SeekBar;
import java.util.ArrayList;
import java.util.List;

import de.upb.hip.mobile.activities.R;

public class CustomSeekBar extends SeekBar {

    private Paint selectedColor, unselectedColor;
    private RectF position;
    private final int halfSize = 15;
    private List<Integer> mDotList = new ArrayList<>();

    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setDots(List<Integer> list){
        //update the instance variable
        mDotList = list;
    }

    private void init(){
        selectedColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedColor.setColor(getResources().getColor(R.color.colorPrimary));
        selectedColor.setStyle(Paint.Style.FILL);

        unselectedColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselectedColor.setColor(getResources().getColor(R.color.textColorSecondaryInverse));

        selectedColor.setStyle(Paint.Style.FILL);
        position = new RectF();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int paddingLeft  = getPaddingLeft();
        int paddingTop   = getPaddingTop();
        float margin     = (canvas.getWidth() - (paddingLeft + getPaddingRight()));
        float halfHeight = (canvas.getHeight() + paddingTop) *.5f;

        for (int i = 0; i < mDotList.size(); i++) {
            int pos = (int) (margin / 100 * mDotList.get(i));

            position.set(paddingLeft + pos - halfSize, halfHeight - halfSize,
                    paddingLeft + pos + halfSize, halfHeight + halfSize);

            int progress = mDotList.get(i);
            canvas.drawOval(position, (progress < getProgress()) ? selectedColor : unselectedColor);
        }
        super.onDraw(canvas);
    }
}
