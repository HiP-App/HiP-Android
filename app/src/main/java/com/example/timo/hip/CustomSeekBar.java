package com.example.timo.hip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.SeekBar;
import java.util.ArrayList;
import java.util.List;

public class CustomSeekBar extends SeekBar {
    private Paint selected, unselected;
    private RectF position;
    private final int halfSize = 15;
    private List<Integer> mProgressList = new ArrayList<>();

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

    public void setProgress(List<Integer> list){
        //update the instance variable
        mProgressList = list;
    }

    private void init(){
        selected = new Paint(Paint.ANTI_ALIAS_FLAG);
        selected.setColor(getResources().getColor(R.color.white));
        selected.setStyle(Paint.Style.FILL);

        unselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselected.setColor(getResources().getColor(R.color.gray));

        selected.setStyle(Paint.Style.FILL);
        position = new RectF();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int paddingLeft  = getPaddingLeft();
        int paddingTop   = getPaddingTop();
        float margin     = (canvas.getWidth() - (paddingLeft + getPaddingRight()));
        float halfHeight = (canvas.getHeight() + paddingTop) *.5f;

        for (int i = 0; i < mProgressList.size(); i++) {
            int pos = (int) (margin / 100 * mProgressList.get(i));

            position.set(paddingLeft + pos - halfSize, halfHeight - halfSize,
                         paddingLeft + pos + halfSize, halfHeight + halfSize);

            int progress = mProgressList.get(i);
            canvas.drawOval(position, (progress < getProgress()) ? selected : unselected);
        }
        super.onDraw(canvas);
    }
}
