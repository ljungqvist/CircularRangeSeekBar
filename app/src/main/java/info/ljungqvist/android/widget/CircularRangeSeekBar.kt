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

package info.ljungqvist.android.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar

class CircularRangeSeekBar : View {

    private var mContext: Context? = null                // The context
    /**
     * Gets the seek bar change listener.

     * @return the seek bar change listener
     */
    /**
     * Sets the seek bar change listener.

     * @param listener
     * * the new seek bar change listener
     */
    var seekBarChangeListener: OnSeekChangeListener? = null // The listener to listen for changes
    private var circleColor: Paint? = null                // The color of the progress ring
    private var circleRing: Paint? = null                // The progress circle ring background
    private var angle1 = 0                    // The angle of progress 1
    private var angle2 = 0                    // The angle of progress 2
    private val startAngle = 270            // The start angle (12 O'clock
    /**
     * Gets the bar width.

     * @return the bar width
     */
    /**
     * Sets the bar width.

     * @param barWidth
     * * the new bar width
     */
    var barWidth = 5                // The width of the progress ring
    /**
     * Gets the max progress.

     * @return the max progress
     */
    /**
     * Sets the max progress.

     * @param maxProgress
     * * the new max progress
     */
    var maxProgress = 100            // The maximum progress amount
    /**
     * Gets the progress.

     * @return the progress
     */
    var progress1: Int = 0
        private set                    // The current progress
    var progress2: Int = 0
        private set                    // The current progress
    /**
     * Gets the radius.

     * @return the raduis
     */
    var radius: Float = 0.toFloat()
        private set                    // The radius of the circle
    private var cx: Float = 0.toFloat()                        // The circle's centre X coordinate
    private var cy: Float = 0.toFloat()                        // The circle's centre Y coordinate
    private var dx: Float = 0.toFloat()    // The X coordinate for the top left corner of the marking drawable
    private var dy: Float = 0.toFloat()    // The Y coordinate for the top left corner of the marking drawable
    private var markPointX1: Float = 0.toFloat()    // The X coordinate for the current position of the marker
    private var markPointY1: Float = 0.toFloat()    // The Y coordinate for the current position of the marker
    private var markPointX2: Float = 0.toFloat()    // The X coordinate for the current position of the marker
    private var markPointY2: Float = 0.toFloat()    // The Y coordinate for the current position of the marker
    private var progressMark: Bitmap? = null        // The progress mark when the view isn't being progress modified
    private var progressMarkPressed: Bitmap? = null    // The progress mark when the view is being progress modified
    private var IS_PRESSED1 = false    // The flag to see if view is pressed
    private var IS_PRESSED2 = false    // The flag to see if view is pressed
    var circleOnSame = false
        set(circleOnSame) {
            field = circleOnSame
            invalidate()
        }    // Draw the full circle if progress1 == progress 2
    /* The flag to see if the setProgress() method was called from our own
     * View's setAngle() method, or externally by a user.*/
    //private boolean CALLED_FROM_ANGLE = false;
    private val rect = RectF()        // The rectangle containing our circles and arcs

    init {
        seekBarChangeListener = object : OnSeekChangeListener {
            override fun onProgressChange(view: CircularRangeSeekBar, progress1: Int, progress2: Int, fromUser: Boolean) {}
        }

        circleColor = Paint()
        circleRing = Paint()

        circleColor!!.color = Color.parseColor("#ff33b5e5") // Set default                                               // black
        circleRing!!.color = Color.GRAY// Set default background color to Gray

        circleColor!!.isAntiAlias = true
        circleRing!!.isAntiAlias = true

        circleColor!!.strokeWidth = barWidth.toFloat()
        circleRing!!.strokeWidth = barWidth.toFloat()

        circleColor!!.style = Paint.Style.STROKE
        circleRing!!.style = Paint.Style.STROKE
    }

    /**
     * Instantiates a new circular seek bar.

     * @param context
     * * the context
     * *
     * @param attrs
     * * the attrs
     * *
     * @param defStyle
     * * the def style
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
        initDrawable()
    }

    /**
     * Instantiates a new circular seek bar.

     * @param context
     * * the context
     * *
     * @param attrs
     * * the attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initDrawable()
    }

    /**
     * Instantiates a new circular seek bar.

     * @param context
     * * the context
     */
    constructor(context: Context) : super(context) {
        mContext = context
        initDrawable()
    }

    /**
     * Inits the drawable.
     */
    fun initDrawable() {
        progressMark = BitmapFactory.decodeResource(mContext!!.resources, R.drawable.scrubber_control_normal_holo)
        progressMarkPressed = BitmapFactory.decodeResource(mContext!!.resources,
                R.drawable.scrubber_control_pressed_holo)
        dx = Math.max(progressMark!!.width, progressMarkPressed!!.width).toFloat() / 2f
        dy = Math.max(progressMark!!.height, progressMarkPressed!!.height).toFloat() / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
            width = Math.max(widthSize, heightSize)
        } else {
            width = widthSize
        }

        val height: Int
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
            height = Math.max(widthSize, heightSize)
        } else {
            height = heightSize
        }

        val size = Math.min(width - (2f * dx).toInt(), height - (2f * dy).toInt()) // Choose the smaller
        val h = size + (2f * dy).toInt()
        val w = size + (2f * dx).toInt()
        //MUST CALL THIS
        setMeasuredDimension(w, h)



        cx = (w / 2).toFloat() // Center X for circle
        cy = (h / 2).toFloat() // Center Y for circle
        radius = size.toFloat() / 2f // Radius of the outer circle

        markPointX1 = cx + radius * Math.sin(Math.toRadians(angle1.toDouble())).toFloat()
        markPointY1 = cy - radius * Math.cos(Math.toRadians(angle1.toDouble())).toFloat()
        markPointX2 = cx + radius * Math.sin(Math.toRadians(angle2.toDouble())).toFloat()
        markPointY2 = cy - radius * Math.cos(Math.toRadians(angle2.toDouble())).toFloat()

        rect.set(cx - radius, cy - radius, cx + radius, cy + radius) // assign size to rect
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(cx, cy, radius, circleRing!!)
        var ang2 = angle2 - angle1
        if (ang2 < 0) ang2 += 360
        if (circleOnSame && progress1 == progress2)
            ang2 = 360
        canvas.drawArc(rect, (startAngle + angle1).toFloat(), ang2.toFloat(), false, circleColor!!)
        canvas.drawBitmap(
                if (IS_PRESSED1) progressMarkPressed else progressMark,
                markPointX1 - dx, markPointY1 - dy, null)
        canvas.drawBitmap(
                if (IS_PRESSED2) progressMarkPressed else progressMark,
                markPointX2 - dx, markPointY2 - dy, null)
        super.onDraw(canvas)
    }

    /**
     * The listener interface for receiving onSeekChange events. The class that
     * is interested in processing a onSeekChange event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * `setSeekBarChangeListener(OnSeekChangeListener)` method. When
     * the onSeekChange event occurs, that object's appropriate
     * method is invoked.

    `` */
    interface OnSeekChangeListener {

        /**
         * On progress change.

         * @param view
         * * the view
         * *
         * @param progress1
         * * the new progress1
         * *
         * @param progress2
         * * the new progress2
         */
        fun onProgressChange(view: CircularRangeSeekBar, progress1: Int, progress2: Int, fromUser: Boolean)
    }

    /**
     * Gets maximum margin.

     * @return the maximum margin
     */
    val maxMargin: Float
        get() = Math.max(dx, dy)

    /**
     * Sets the progress.

     * @param progress1
     * *
     * @param progress2
     * * the new progress
     */
    private fun setProgressInternal(progress1: Int, progress2: Int, fromUser: Boolean) {
        var update = false
        if (this.progress1 != progress1) {
            if (progress1 < 0)
                this.progress1 = 0
            else if (progress1 >= maxProgress)
                this.progress1 = maxProgress - 1
            else
                this.progress1 = progress1
            angle1 = 360 * this.progress1 / maxProgress
            markPointX1 = cx + radius * Math.sin(Math.toRadians(angle1.toDouble())).toFloat()
            markPointY1 = cy - radius * Math.cos(Math.toRadians(angle1.toDouble())).toFloat()
            update = true
        }
        if (this.progress2 != progress2) {
            if (progress2 < 0)
                this.progress2 = 0
            else if (progress2 >= maxProgress)
                this.progress2 = maxProgress - 1
            else
                this.progress2 = progress2
            angle2 = 360 * this.progress2 / maxProgress
            markPointX2 = cx + radius * Math.sin(Math.toRadians(angle2.toDouble())).toFloat()
            markPointY2 = cy - radius * Math.cos(Math.toRadians(angle2.toDouble())).toFloat()
            update = true
        }
        if (update)
            seekBarChangeListener!!.onProgressChange(this, this.progress1, this.progress2, fromUser)
    }

    fun setProgress(progress1: Int, progress2: Int) {
        setProgressInternal(progress1, progress2, true)
        invalidate()
    }

    /**
     * Sets the ring background color.

     * @param color
     * * the new ring background color
     */
    fun setRingBackgroundColor(color: Int) {
        circleRing!!.color = color
    }

    /**
     * Sets the progress color.

     * @param color
     * * the new progress color
     */
    fun setProgressColor(color: Int) {
        circleColor!!.color = color
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (event.action == MotionEvent.ACTION_DOWN) {
            val d_max = Math.max(dx, dy)
            val x_ = x - cx
            val y_ = y - cy
            val r_sq = x_ * x_ + y_ * y_
            val r_min = radius - 2f * d_max
            val r_max = radius + 2f * d_max
            if (r_sq >= r_min * r_min && r_sq <= r_max * r_max) {
                IS_PRESSED1 = Math.pow((x - markPointX1).toDouble(), 2.0) + Math.pow((y - markPointY1).toDouble(), 2.0) < Math.pow((x - markPointX2).toDouble(), 2.0) + Math.pow((y - markPointY2).toDouble(), 2.0)
                IS_PRESSED2 = !IS_PRESSED1
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        if (IS_PRESSED1 || IS_PRESSED2) {
            var ang: Double
            if (y == cy) {
                if (x > cx)
                    ang = Math.PI / 2.0
                else
                    ang = Math.PI * 3.0 / 2.0
            } else {
                ang = Math.atan(((x - cx) / (cy - y)).toDouble())
                if (ang < 0) ang = Math.PI + ang
                if (x < cx) ang += Math.PI
            }
            var progress = (.5 + ang / (2.0 * Math.PI) * maxProgress.toDouble()).toInt()
            if (progress == maxProgress)
                progress = 0
            //Log.d(Constants.LOG_TAG, "  Ang:"+ang+" prog:"+progress);
            setProgressInternal(
                    if (IS_PRESSED1) progress else progress1,
                    if (IS_PRESSED2) progress else progress2,
                    false)
        }
        if (event.action == MotionEvent.ACTION_UP) {
            //getParent().requestDisallowInterceptTouchEvent(false);
            IS_PRESSED1 = false
            IS_PRESSED2 = false
        }
        invalidate()
        return true
    }
}