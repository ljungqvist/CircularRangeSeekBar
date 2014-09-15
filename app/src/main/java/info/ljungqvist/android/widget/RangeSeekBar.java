package info.ljungqvist.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
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
public class RangeSeekBar extends SeekBar implements RangeSeekBarInterface {
    private static final int MAX_LEVEL = 10000;
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;

    private int progress2 = 0;

    private Drawable thumb2 = null;

    private long uiThreadId;

    private OnRangeSeekBarChangeListener onRangeSeekBarChangeListener;

    public RangeSeekBar(Context context) {
        this(context, null);
    }
    public RangeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }
    public RangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        uiThreadId = Thread.currentThread().getId();
    }

    @Override
    public synchronized int getProgress2() {
        return progress2;
    }

    @Override
    public synchronized final void incrementProgress2By(int diff) {
        setProgress(progress2 + diff);
    }

    @Override
    public synchronized void setProgress2(int progress2) {
        setProgress2(progress2, false);
    }

    synchronized void setProgress2(int progress2, boolean fromUser) {
        if (progress2 < 0) progress2 = 0;
        if (progress2 > getMax()) {
            progress2 = getMax();
        }

        if (progress2 != this.progress2) {
            this.progress2 = progress2;
            refreshProgress2(android.R.id.progress, progress2, fromUser);
        }
    }

    private synchronized void refreshProgress2(int id, int progress2, boolean fromUser) {

        if (uiThreadId == Thread.currentThread().getId()) {
            doRefreshProgress2(id, progress2, fromUser, true);
        } else {
//            if (mRefreshProgressRunnable == null) {
//                mRefreshProgressRunnable = new RefreshProgressRunnable();
//            }
//
//            final RefreshData rd = RefreshData.obtain(id, progress, fromUser);
//            mRefreshData.add(rd);
//            if (mAttached && !mRefreshIsPosted) {
//                post(mRefreshProgressRunnable);
//                mRefreshIsPosted = true;
//            }
        }
    }

    private synchronized void doRefreshProgress2(int id, int progress2, boolean fromUser, boolean callBackToApp) {
        float scale = getMax() > 0 ? (float) progress2 / (float) getMax() : 0;
        final Drawable drawable = getProgressDrawable();
        if (drawable != null) {
            Drawable progressDrawable = null;

            if (drawable instanceof LayerDrawable) {
                progressDrawable = ((LayerDrawable) drawable).findDrawableByLayerId(id);
            }

            final int level = (int) (scale * MAX_LEVEL);
            (progressDrawable != null ? progressDrawable : drawable).setLevel(level);
        } else {
            invalidate();
        }

        if (callBackToApp && id == android.R.id.progress) {
            onProgressRefresh(scale, fromUser);
        }
    }

    void onProgressRefresh(float scale, boolean fromUser) {
//        if (((AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE)).isEnabled()) {
//            scheduleAccessibilityEventSender();
//        }
    }

//    private void scheduleAccessibilityEventSender() {
//        if (mAccessibilityEventSender == null) {
//            mAccessibilityEventSender = new AccessibilityEventSender();
//        } else {
//            removeCallbacks(mAccessibilityEventSender);
//        }
//        postDelayed(mAccessibilityEventSender, TIMEOUT_SEND_ACCESSIBILITY_EVENT);
//    }

    @Override
    public Drawable getThumb2() {
        return null;
    }

    @Override
    public int getThumb2Offset() {
        return 0;
    }

    @Override
    public void setOnRangeSeekBarChangeListener(RangeSeekBarInterface.OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
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



