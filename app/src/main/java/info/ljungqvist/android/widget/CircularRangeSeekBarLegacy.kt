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

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import info.ljungqvist.android.widget.util.FloatPoint
import mu.KLogging

class CircularRangeSeekBarLegacy : View {

    var seekBarChangeListener: OnSeekChangeListener? = null

    var barWidth = 5                // The width of the progress ring

    // The color of the progress ring
    private var circleColor: Paint = Paint()
            .apply {
                color = Color.parseColor("#ff33b5e5")
                isAntiAlias = true
                strokeWidth = barWidth.toFloat()
                style = Paint.Style.STROKE
            }
    // The progress circle ring background
    private var circleRing: Paint = Paint()
            .apply {
                color = Color.GRAY
                isAntiAlias = true
                strokeWidth = barWidth.toFloat()
                style = Paint.Style.STROKE
            }
    private var angle1 = 0                    // The angle of progress 1
    private var angle2 = 0                    // The angle of progress 2
    private val startAngle = 270              // The start angle (12 O'clock)

    var maxProgress = 100            // The maximum progress amount

    var progress1: Int = 0
        private set                    // The current progress
    var progress2: Int = 0
        private set                    // The current progress

    var radius: Float = 0f
        private set                    // The radius of the circle
    private var center: FloatPoint = FloatPoint(0f, 0f) // The circle's centre
    private var d: FloatPoint = FloatPoint(0f, 0f) // coordinates for the top left corner of the marking drawable
    private var markPoint1: FloatPoint = FloatPoint(0f, 0f) // coordinates for the current position of marker 1
    private var markPoint2: FloatPoint = FloatPoint(0f, 0f) // coordinates for the current position of marker 2
    private var progressMark: Bitmap? = null        // The progress mark when the view isn't being progress modified
    private var progressMarkPressed: Bitmap? = null    // The progress mark when the view is being progress modified
    private var isPressed1 = false    // The flag to see if view is pressed
    private var isPressed2 = false    // The flag to see if view is pressed
    var circleOnSame = false
        set(circleOnSame) {
            field = circleOnSame
            invalidate()
        }    // Draw the full circle if progress1 == progress 2
    /* The flag to see if the setProgress() method was called from our own
     * View's setAngle() method, or externally by a user.*/
    //private boolean CALLED_FROM_ANGLE = false;
    private val rect = RectF()        // The rectangle containing our circles and arcs

    private val ripple: Drawable? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.LTGRAY)), null, null)
                        .also { background = it }
            } else {
                null
            }


    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0)
            : super(context, attrs, defStyle)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, defStyleRes: Int)
            : super(context, attrs, defStyle, defStyleRes)


    /**
     * Inits the drawable.
     */
    init {
        seekBarChangeListener = OnSeekChangeListener { _, progress1, progress2, _ ->
            logger.debug { "p1: $progress1, p2: $progress2" }
        }
        Pair(
                BitmapFactory.decodeResource(context.resources, R.drawable.scrubber_control_normal_holo),
                BitmapFactory.decodeResource(context.resources, R.drawable.scrubber_control_pressed_holo)
        ).let { (mark, markPressed) ->
            progressMark = mark
            progressMarkPressed = markPressed
            d = FloatPoint(
                    Math.max(mark.width, markPressed.width).toFloat() / 2f,
                    Math.max(mark.height, markPressed.height).toFloat() / 2f
            )
        }
        setProgressInternal(0, 0, false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width: Int =
                if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
                    Math.max(widthSize, heightSize)
                } else {
                    widthSize
                }

        val height: Int =
                if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
                    Math.max(widthSize, heightSize)
                } else {
                    heightSize
                }

        val diameter = Math.min(width - (2f * d.x).toInt(), height - (2f * d.y).toInt()) // Choose the smaller
        val size = (d * 2f).toIntPoint() + diameter
        //MUST CALL THIS
        setMeasuredDimension(size.x, size.y)


        center = (size / 2).toFloatPoint()
        radius = diameter.toFloat() / 2f // Radius of the outer circle


        rect.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius) // assign size to rect

        setProgressInternal(progress1, progress2, false, true)


    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circleRing)
        var ang2 = angle2 - angle1
        if (ang2 < 0) ang2 += 360
        if (circleOnSame && progress1 == progress2)
            ang2 = 360
        canvas.drawArc(rect, (startAngle + angle1).toFloat(), ang2.toFloat(), false, circleColor)
        canvas.drawBitmap(
                if (isPressed1) progressMarkPressed else progressMark,
                markPoint1.x - d.x, markPoint1.y - d.y, null)
        canvas.drawBitmap(
                if (isPressed2) progressMarkPressed else progressMark,
                markPoint2.x - d.x, markPoint2.y - d.y, null)
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
        fun onProgressChange(view: CircularRangeSeekBarLegacy, progress1: Int, progress2: Int, fromUser: Boolean)
    }

    fun OnSeekChangeListener(listener: (CircularRangeSeekBarLegacy, Int, Int, Boolean) -> Unit): OnSeekChangeListener =
            object : OnSeekChangeListener {
                override fun onProgressChange(view: CircularRangeSeekBarLegacy, progress1: Int, progress2: Int, fromUser: Boolean) =
                        listener(view, progress1, progress2, fromUser)
            }

    /**
     * Gets maximum margin.

     * @return the maximum margin
     */
    val maxMargin: Float
        get() = d.max()

    private fun setProgressInternal(progress1: Int, progress2: Int, fromUser: Boolean) =
            setProgressInternal(progress1, progress2, fromUser, false)

    private fun setProgressInternal(progress1: Int, progress2: Int, fromUser: Boolean, force: Boolean) {
        var update = false
        if (force || this.progress1 != progress1) {
            this.progress1 =
                    when {
                        progress1 < 0 ->
                            0
                        progress1 >= maxProgress ->
                            maxProgress - 1
                        else ->
                            progress1
                    }
            angle1 = 360 * this.progress1 / maxProgress
            markPoint1 = center + FloatPoint(
                    radius * Math.sin(Math.toRadians(angle1.toDouble())).toFloat(),
                    -radius * Math.cos(Math.toRadians(angle1.toDouble())).toFloat()
            )
            update = true
        }
        if (force || this.progress2 != progress2) {
            this.progress2 =
                    when {
                        progress2 < 0 ->
                            0
                        progress2 >= maxProgress ->
                            maxProgress - 1
                        else ->
                            progress2
                    }
            angle2 = 360 * this.progress2 / maxProgress
            markPoint2 = center + FloatPoint(
                    radius * Math.sin(Math.toRadians(angle2.toDouble())).toFloat(),
                    -radius * Math.cos(Math.toRadians(angle2.toDouble())).toFloat()
            )
            update = true
        }

        if (update && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ripple?.let { rip ->
                (if (isPressed1) markPoint1 else markPoint2)
                        .also { (x, y) -> drawableHotspotChanged(x, y) }
                        .let(FloatPoint::toIntPoint)
                        .let { (x, y) ->
                            rip.setBounds(x - 50, y - 50, x + 50, y + 50)
                        }
            }
        }
        if (update && !force)
            seekBarChangeListener?.onProgressChange(this, this.progress1, this.progress2, fromUser)
    }

    fun setProgress(progress1: Int, progress2: Int) {
        setProgressInternal(progress1, progress2, false)
        invalidate()
    }

    /**
     * Sets the ring background color.

     * @param color
     * * the new ring background color
     */
    fun setRingBackgroundColor(color: Int) {
        circleRing.color = color
    }

    /**
     * Sets the progress color.

     * @param color
     * * the new progress color
     */
    fun setProgressColor(color: Int) {
        circleColor.color = color
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val p = FloatPoint(event.x, event.y)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            background
//                    ?.also { it.setHotspot(p.x, p.y) }
//                    ?.let { it as? RippleDrawable }
//                    ?.setColor(ColorStateList.valueOf(Color.RED))
//        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            val p_ = p - center
            val r_sq = (p_ * p_).sum()
            val r_min = radius - 2f * maxMargin
            val r_max = radius + 2f * maxMargin
            if (r_sq >= r_min * r_min && r_sq <= r_max * r_max) {
                isPressed = true
                isPressed1 = p.squareDistance(markPoint1) < p.squareDistance(markPoint2)
                isPressed2 = !isPressed1
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        if (isPressed1 || isPressed2) {
            var ang: Double
            if (p.y == center.y) {
                ang = if (p.x > center.x)
                    Math.PI / 2.0
                else
                    Math.PI * 3.0 / 2.0
            } else {
                ang = Math.atan(((p.x - center.x) / (center.y - p.y)).toDouble())
                if (ang < 0) ang += Math.PI
                if (p.x < center.x) ang += Math.PI
            }
            var progress = (.5 + ang / (2.0 * Math.PI) * maxProgress.toDouble()).toInt()
            if (progress == maxProgress)
                progress = 0
            //Log.d(Constants.LOG_TAG, "  Ang:"+ang+" prog:"+progress);
            setProgressInternal(
                    if (isPressed1) progress else progress1,
                    if (isPressed2) progress else progress2,
                    true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ripple?.let { rip ->
                    (if (isPressed1) markPoint1 else markPoint2)
                            .also { (x, y) -> drawableHotspotChanged(x, y) }
                            .let(FloatPoint::toIntPoint)
                            .let { (x, y) ->
                                rip.setBounds(x - 50, y - 50, x + 50, y + 50)
                            }
                }
            }
        }
        if (event.action == MotionEvent.ACTION_UP) {
            isPressed = false
            //getParent().requestDisallowInterceptTouchEvent(false);
            isPressed1 = false
            isPressed2 = false
        }

        invalidate()
        return true
    }

    companion object : KLogging()
}