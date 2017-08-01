/*
 * Copyright 2013-2017 Petter Ljungqvist (petter@ljungqvist.info)
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

package info.ljungqvist.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;


public class RangeSeekBar extends ExposingSeekBar implements RangeSeekBarInterface {
    private static final int MAX_LEVEL = 10000;
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;

    private int progress2 = 0;

    private Drawable thumb2;

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
            onProgress2Refresh(scale, fromUser);
        }
    }

    void onProgress2Refresh(float scale, boolean fromUser) {
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
        return thumb2;
    }

    @Override
    public void setOnRangeSeekBarChangeListener(RangeSeekBarInterface.OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
        this.onRangeSeekBarChangeListener = onRangeSeekBarChangeListener;
        setOnSeekBarChangeListener(onRangeSeekBarChangeListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //updateThumbPos(w, h);
    }

    @Override
    public void setThumb(Drawable thumb) {
        Drawable oldThumb = getThumb();
        boolean needUpdate = oldThumb != null && thumb != oldThumb;

        super.setThumb(thumb);

        if (needUpdate) {
            thumb2.setCallback(null);
        }
        if (null != thumb) {
            thumb2 = thumb.mutate().getConstantState().newDrawable();
            thumb2.setCallback(this);
        } else {
            thumb2 = null;
        }
        invalidate();
        if (needUpdate) {
            updateThumb2Pos(getWidth(), getHeight());
            if (thumb != null && thumb2.isStateful()) {
                int[] state = getDrawableState();
                thumb2.setState(state);
            }
        }
    }

    private void updateThumb2Pos(int w, int h) {
        Drawable d = getProgressDrawable();
        int thumbHeight = thumb2 == null ? 0 : thumb2.getIntrinsicHeight();
        // The max height does not incorporate padding, whereas the height
        // parameter does
        int trackHeight = Math.min(getMaxHeight(), h - getPaddingTop() - getPaddingBottom());

        int max = getMax();
        float scale = max > 0 ? (float) getProgress2() / (float) max : 0;

        if (thumbHeight > trackHeight) {
            if (thumb2 != null) {
                setThumbPos(w, thumb2, scale, 0);
            }
            int gapForCenteringTrack = (thumbHeight - trackHeight) / 2;
            if (d != null) {
                // Canvas will be translated by the padding, so 0,0 is where we start drawing
                d.setBounds(0, gapForCenteringTrack,
                        w - getPaddingRight() - getPaddingLeft(),
                        h - getPaddingBottom() - gapForCenteringTrack - getPaddingTop());
            }
        } else {
            if (d != null) {
                // Canvas will be translated by the padding, so 0,0 is where we start drawing
                d.setBounds(0, 0,
                        w - getPaddingRight() - getPaddingLeft(),
                        h - getPaddingBottom() - getPaddingTop());
            }
            int gap = (trackHeight - thumbHeight) / 2;
            if (thumb2 != null) {
                setThumbPos(w, thumb2, scale, gap);
            }
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (thumb2 != null) {
            canvas.save();
            // Translate the padding. For the x, we need to allow the thumb to
            // draw in its extra space
            canvas.translate(getPaddingLeft() - getThumbOffset(), getPaddingTop());
            thumb2.draw(canvas);
            canvas.restore();
            Log.w("!!!!!!", thumb2.getBounds().toShortString());
            Log.w("!!!!!!", getThumb().getBounds().toShortString());
        }
    }

}



