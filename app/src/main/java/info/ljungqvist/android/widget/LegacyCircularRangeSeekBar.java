/*
 * Copyright 2013-2017 Petter Ljungqvist
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LegacyCircularRangeSeekBar extends View {

    private Context mContext;				// The context
    private OnSeekChangeListener mListener; // The listener to listen for changes
    private Paint circleColor;				// The color of the progress ring
    private Paint circleRing;				// The progress circle ring background
    private int angle1 = 0;					// The angle of progress 1
    private int angle2 = 0;					// The angle of progress 2
    private int startAngle = 270;			// The start angle (12 O'clock
    private int barWidth = 5;				// The width of the progress ring
    private int width;						// The width of the view
    private int height;						// The height of the view
    private int maxProgress = 100;			// The maximum progress amount
    private int progress1;					// The current progress
    private int progress2;					// The current progress
    private float radius;					// The radius of the circle
    private float cx;						// The circle's centre X coordinate
    private float cy;						// The circle's centre Y coordinate
    private float dx;	// The X coordinate for the top left corner of the marking drawable
    private float dy;	// The Y coordinate for the top left corner of the marking drawable
    private float markPointX1;	// The X coordinate for the current position of the marker
    private float markPointY1;	// The Y coordinate for the current position of the marker
    private float markPointX2;	// The X coordinate for the current position of the marker
    private float markPointY2;	// The Y coordinate for the current position of the marker
    private Bitmap progressMark;		// The progress mark when the view isn't being progress modified
    private Bitmap progressMarkPressed;	// The progress mark when the view is being progress modified
    private boolean IS_PRESSED1 = false;	// The flag to see if view is pressed
    private boolean IS_PRESSED2 = false;	// The flag to see if view is pressed
    private boolean CIRCLE_ON_SAME = false;	// Draw the full circle if progress1 == progress 2
    /* The flag to see if the setProgress() method was called from our own
     * View's setAngle() method, or externally by a user.*/
    //private boolean CALLED_FROM_ANGLE = false;
    private RectF rect = new RectF();		// The rectangle containing our circles and arcs

    {
        mListener = new OnSeekChangeListener() {
            @Override
            public void onProgressChange(LegacyCircularRangeSeekBar view, int progress1, int progress2, boolean fromUser) { }
        };

        circleColor = new Paint();
        circleRing = new Paint();

        circleColor.setColor(Color.parseColor("#ff33b5e5")); // Set default                                               // black
        circleRing.setColor(Color.GRAY);// Set default background color to Gray

        circleColor.setAntiAlias(true);
        circleRing.setAntiAlias(true);

        circleColor.setStrokeWidth(barWidth);
        circleRing.setStrokeWidth(barWidth);

        circleColor.setStyle(Paint.Style.STROKE);
        circleRing.setStyle(Paint.Style.STROKE);
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context
     * the context
     * @param attrs
     * the attrs
     * @param defStyle
     * the def style
     */
    public LegacyCircularRangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDrawable();
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context
     * the context
     * @param attrs
     * the attrs
     */
    public LegacyCircularRangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDrawable();
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context
     * the context
     */
    public LegacyCircularRangeSeekBar(Context context) {
        super(context);
        mContext = context;
        initDrawable();
    }

    /**
     * Inits the drawable.
     */
    public void initDrawable() {
        progressMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scrubber_control_normal_holo);
        progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.scrubber_control_pressed_holo);
        dx = (float)(Math.max(progressMark.getWidth(), progressMarkPressed.getWidth()))/2.f;
        dy = (float)(Math.max(progressMark.getHeight(), progressMarkPressed.getHeight()))/2.f;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            width = Math.max(widthSize, heightSize);
        } else {
            width = widthSize;
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = Math.max(widthSize, heightSize);
        } else {
            height = heightSize;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);

        int size = Math.min(width-(int)(2.f*dx), height-(int)(2.f*dy)); // Choose the smaller

        cx = width / 2; // Center X for circle
        cy = height / 2; // Center Y for circle
        radius = (float)size / 2.f; // Radius of the outer circle

        markPointX1 = cx + radius * (float)Math.sin(Math.toRadians(angle1));
        markPointY1 = cy - radius * (float)Math.cos(Math.toRadians(angle1));
        markPointX2 = cx + radius * (float)Math.sin(Math.toRadians(angle2));
        markPointY2 = cy - radius * (float)Math.cos(Math.toRadians(angle2));

        rect.set(cx-radius, cy-radius, cx+radius, cy+radius); // assign size to rect
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, circleRing);
        int ang2 = angle2 - angle1;
        if (ang2 < 0) ang2 += 360;
        if (CIRCLE_ON_SAME && progress1 == progress2)
            ang2 = 360;
        canvas.drawArc(rect, startAngle+angle1, ang2, false, circleColor);
        canvas.drawBitmap(
                IS_PRESSED1 ? progressMarkPressed : progressMark,
                markPointX1-dx, markPointY1-dy, null);
        canvas.drawBitmap(
                IS_PRESSED2 ? progressMarkPressed : progressMark,
                markPointX2-dx, markPointY2-dy, null);
        super.onDraw(canvas);
    }

    /**
     * Sets the seek bar change listener.
     *
     * @param listener
     * the new seek bar change listener
     */
    public void setSeekBarChangeListener(OnSeekChangeListener listener) {
        mListener = listener;
    }

    /**
     * Gets the seek bar change listener.
     *
     * @return the seek bar change listener
     */
    public OnSeekChangeListener getSeekBarChangeListener() {
        return mListener;
    }

    /**
     * Gets the bar width.
     *
     * @return the bar width
     */
    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Sets the bar width.
     *
     * @param barWidth
     * the new bar width
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * The listener interface for receiving onSeekChange events. The class that
     * is interested in processing a onSeekChange event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>setSeekBarChangeListener(OnSeekChangeListener)<code> method. When
     * the onSeekChange event occurs, that object's appropriate
     * method is invoked.
     *
     */
    public interface OnSeekChangeListener {

        /**
         * On progress change.
         *
         * @param view
         * the view
         * @param progress1
         * the new progress1
         * @param progress2
         * the new progress2
         */
        public void onProgressChange(LegacyCircularRangeSeekBar view, int progress1, int progress2, boolean fromUser);
    }

    /**
     * Gets maximum margin.
     *
     * @return the maximum margin
     */
    public float getMaxMargin() {
        return Math.max(dx, dy);
    }

    /**
     * Gets the radius.
     *
     * @return the raduis
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Gets the max progress.
     *
     * @return the max progress
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the max progress.
     *
     * @param maxProgress
     * the new max progress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * Gets the progress.
     *
     * @return the progress
     */
    public int getProgress1() {
        return progress1;
    }
    public int getProgress2() {
        return progress2;
    }

    /**
     * Sets the progress.
     *
     * @param progress1
     * @param progress2
     * the new progress
     */
    private void setProgressInternal(int progress1, int progress2, boolean fromUser) {
        boolean update = false;
        if (this.progress1 != progress1) {
            if (progress1 < 0)
                this.progress1 = 0;
            else if (progress1 >= maxProgress)
                this.progress1 = maxProgress - 1;
            else
                this.progress1 = progress1;
            angle1 = 360 * this.progress1 / maxProgress;
            markPointX1 = cx + radius * (float)Math.sin(Math.toRadians(angle1));
            markPointY1 = cy - radius * (float)Math.cos(Math.toRadians(angle1));
            update = true;
        }
        if (this.progress2 != progress2) {
            if (progress2 < 0)
                this.progress2 = 0;
            else if (progress2 >= maxProgress)
                this.progress2 = maxProgress - 1;
            else
                this.progress2 = progress2;
            angle2 = 360 * this.progress2 / maxProgress;
            markPointX2 = cx + radius * (float)Math.sin(Math.toRadians(angle2));
            markPointY2 = cy - radius * (float)Math.cos(Math.toRadians(angle2));
            update = true;
        }
        if (update)
            mListener.onProgressChange(this, this.progress1, this.progress2, fromUser);
    }
    public void setProgress(int progress1, int progress2) {
        setProgressInternal(progress1, progress2, true);
        invalidate();
    }

    /**
     * Sets the ring background color.
     *
     * @param color
     * the new ring background color
     */
    public void setRingBackgroundColor(int color) {
        circleRing.setColor(color);
    }

    /**
     * Sets the progress color.
     *
     * @param color
     * the new progress color
     */
    public void setProgressColor(int color) {
        circleColor.setColor(color);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float d_max = Math.max(dx, dy);
            float x_ = x - cx, y_ = y - cy;
            float r_sq = x_*x_ + y_ * y_;
            float r_min = radius - 2.f*d_max, r_max = radius + 2.f*d_max;
            if (r_sq >= r_min*r_min && r_sq <= r_max*r_max) {
                IS_PRESSED1 = (Math.pow(x-markPointX1,2) + Math.pow(y-markPointY1,2) <
                        Math.pow(x-markPointX2,2) + Math.pow(y-markPointY2,2));
                IS_PRESSED2 = !IS_PRESSED1;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        if (IS_PRESSED1 || IS_PRESSED2) {
            double ang;
            if (y == cy) {
                if (x > cx)
                    ang = Math.PI / 2.;
                else
                    ang = Math.PI * 3. / 2.;
            } else {
                ang = Math.atan((x-cx)/(cy-y));
                if (ang < 0) ang = Math.PI + ang;
                if (x < cx) ang += Math.PI;
            }
            int progress = (int)(.5 + ang / (2.*Math.PI) * (double)(maxProgress));
            if (progress == maxProgress)
                progress = 0;
            //Log.d(Constants.LOG_TAG, "  Ang:"+ang+" prog:"+progress);
            setProgressInternal(
                    IS_PRESSED1 ? progress : progress1,
                    IS_PRESSED2 ? progress : progress2,
                    false);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //getParent().requestDisallowInterceptTouchEvent(false);
            IS_PRESSED1 = false;
            IS_PRESSED2 = false;
        }
        invalidate();
        return true;
    }

    public boolean getCircleOnSame() {
        return CIRCLE_ON_SAME;
    }

    public void setCircleOnSame(final boolean circleOnSame) {
        CIRCLE_ON_SAME = circleOnSame;
        invalidate();
    }
}