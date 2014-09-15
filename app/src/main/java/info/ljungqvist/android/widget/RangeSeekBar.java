package info.ljungqvist.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

/*
 * Copyright 2013-2014 Petter Ljungqvist (petter@ljungqvist.info)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RangeSeekBar extends SeekBar {

    public interface OnRangeSeekBarChangeListener extends OnSeekBarChangeListener {
        void onProgress2Changed(RangeSeekBar seekBar, int progress, boolean fromUser);
    }


    private Drawable thumb2 = null;

    private OnRangeSeekBarChangeListener onRangeSeekBarChangeListener;

    public RangeSeekBar(Context context) {
        super(context);
    }
    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
        this.onRangeSeekBarChangeListener = onRangeSeekBarChangeListener;
        setOnSeekBarChangeListener(onRangeSeekBarChangeListener);
    }

    @Override
    protected void  onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //updateThumbPos(w, h);
    }

    public void setThumb2(Drawable thumb2) {
        this.thumb2 = thumb2;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}



