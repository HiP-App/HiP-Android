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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import de.upb.hip.mobile.activities.R;

public class CustomSeekBar extends SeekBar {

    private static final int HALF_SIZE = 15;

    private Paint mSelectedColor, mUnselectedColor;
    private Paint mTextPaint;
    private RectF mPosition;
    private List<Integer> mDotList = new ArrayList<>();

    /**
     * Constructor
     * @param context
     */
    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * update the instance variable
     * @param list
     */
    public void setDots(List<Integer> list) {
        mDotList = list;
    }

    /**
     * Initalizes the class
     * Set variables
     */
    private void init() {
        mSelectedColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedColor.setColor(getResources().getColor(R.color.colorPrimary));
        mSelectedColor.setStyle(Paint.Style.FILL);

        mUnselectedColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnselectedColor.setColor(getResources().getColor(R.color.textColorSecondaryInverse));

        mSelectedColor.setStyle(Paint.Style.FILL);
        mPosition = new RectF();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.textColorSecondary));
        mTextPaint.setTextSize(48);
    }

    /**
     * Draw on Canvas
     * @param canvas
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        float margin = (canvas.getWidth() - (paddingLeft + getPaddingRight()));
        float halfHeight = (canvas.getHeight() + paddingTop) * .5f;

        for (int i = 0; i < mDotList.size(); i++) {
            int pos = (int) (margin / 100 * mDotList.get(i));

            mPosition.set(paddingLeft + pos - HALF_SIZE, halfHeight - HALF_SIZE,
                    paddingLeft + pos + HALF_SIZE, halfHeight + HALF_SIZE);

            int progress = mDotList.get(i);
            canvas.drawOval(
                    mPosition,
                    (progress < getProgress()) ? mSelectedColor : mUnselectedColor
            );
//            canvas.drawText("653", paddingLeft + pos - HALF_SIZE * 2, halfHeight + HALF_SIZE * 4, mTextPaint);
        }

        super.onDraw(canvas);
    }
}
