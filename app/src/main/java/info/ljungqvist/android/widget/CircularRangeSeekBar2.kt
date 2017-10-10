package info.ljungqvist.android.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import mu.KLogging
import kotlin.properties.Delegates

class CircularRangeSeekBar2 : FrameLayout {

    constructor(context: Context)
            : super(context)

    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0)
            : super(context, attributeSet, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attributeSet, defStyleAttr, defStyleRes)


    // The color of the progress ring
    private val arcPaint: Paint = Paint()
            .apply {
                color = Color.parseColor("#ff33b5e5")
                isAntiAlias = true
                strokeWidth = 5f
                style = Paint.Style.STROKE
            }
    // The progress circle ring background
    private val circlePaint: Paint = Paint()
            .apply {
                color = Color.GRAY
                isAntiAlias = true
                strokeWidth = 5f
                style = Paint.Style.STROKE
            }

    private val thumb1: Thumb = Thumb(context) { x, y -> thumbTouch(true, x, y) }
    private val thumb2: Thumb = Thumb(context) { x, y -> thumbTouch(false, x, y) }

    private var progress1 = 0
    private var progress2 = 0
    var startAngle by Delegates.observable(270.0) { _, old, new ->
        if (old != new) {
            setProgressInternal(progress1, progress2, false, true)
        }
    }
    private var angle1 = startAngle
    private var angle2 = startAngle

    var progressMax by uiProperty(100)

    private var size = -1
    private var thumbSize = -1
    private val arcRect = RectF()        // The rectangle containing our circles and arcs

    init {
        setBackgroundColor(Color.LTGRAY)
        setImageResource(R.drawable.scrubber_control_holo)
    }

    fun setImageResource(@DrawableRes resId: Int) {
        thumb1.setImageResource(resId)
        thumb2.setImageResource(resId)
        thumbSize = Math.max(
                thumb1.drawable.let { Math.max(it.intrinsicWidth, it.intrinsicHeight) },
                thumb2.drawable.let { Math.max(it.intrinsicWidth, it.intrinsicHeight) }
        )
        updateRect()
        post { invalidate() }
    }

    fun setProgress(progress1: Int, progress2: Int) {
        setProgressInternal(progress1, progress2, false, false)
    }

    override fun onAttachedToWindow() {
        addView(thumb1)
        addView(thumb2)
        super.onAttachedToWindow()
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

        val newSize = Math.min(width, height)
        if (newSize != size) {
            size = newSize
            updateRect()
            invalidate()
        }
        val spec = MeasureSpec.makeMeasureSpec(newSize, MeasureSpec.EXACTLY)
        //MUST CALL THIS
        super.onMeasure(spec, spec)
    }

    override fun onDraw(canvas: Canvas) {
        val mid = size.toFloat() / 2
        val radius = mid - (thumbSize.toFloat() / 2)
        canvas.drawCircle(mid, mid, radius, circlePaint)
        canvas.drawArc(arcRect, angle1.toFloat(), (angle2 - angle1).inDegrees().toFloat(), false, arcPaint)

        setPadding(thumb1, angle1)
        setPadding(thumb2, angle2)

        super.onDraw(canvas)
    }

    private fun setPadding(thumb: Thumb, angle: Double) {
        val mid = (size - thumbSize) / 2
        val paddingLeft = mid + (Math.cos(Math.toRadians(angle)) * mid).toInt()
        val paddingTop = mid + (Math.sin(Math.toRadians(angle)) * mid).toInt()
        thumb.setPadding(paddingLeft, paddingTop, 0, 0)
    }

    private fun updateRect() {
        val upperLeft = thumbSize.toFloat() / 2
        val lowerRight = size.toFloat() - upperLeft
        arcRect.set(upperLeft, upperLeft, lowerRight, lowerRight)
    }


    private fun setProgressInternal(progress1: Int, progress2: Int, fromUser: Boolean, forceChange: Boolean) {
        var changed = forceChange

        progress1
                .limitProgress()
                .takeUnless { it == this.progress1 }
                ?.let {
                    this.progress1 = it
                    changed = true
                }
        progress2
                .limitProgress()
                .takeUnless { it == this.progress2 }
                ?.let {
                    this.progress2 = it
                    changed = true
                }

        if (changed) {
            angle1 = (progress1.toDouble() * 360 / progressMax + startAngle).inDegrees()
            angle2 = (progress2.toDouble() * 360 / progressMax + startAngle).inDegrees()
            post { invalidate() }
        }
    }

    private tailrec fun Int.limitProgress(): Int = when {
        this < 0 -> (this + progressMax).limitProgress()
        this >= progressMax -> (this - progressMax).limitProgress()
        else -> this
    }

    private tailrec fun Double.inDegrees(): Double = when {
        this < 0.0 -> (this + 360.0).inDegrees()
        this >= 360.0 -> (this - 360.0).inDegrees()
        else -> this
    }

    private fun thumbTouch(isThumb1: Boolean, xIn: Float, yIn: Float) {
        logger.debug { "thumb $isThumb1 ($xIn, $yIn)" }
        val halfSize = size.toDouble() / 2.0
        val x = xIn.toDouble()  - halfSize
        val y = yIn.toDouble() - halfSize
        val angle =
                (360.0 / 2.0 / Math.PI *
                        if (0.0 == x) {
                            if (y > 0) Math.PI / 2
                            else -Math.PI / 2
                        } else {
                            Math.atan(y / x) + if (x >= 0) 0.0 else Math.PI
                        } -
                        startAngle)
                        .inDegrees()
        val progress = (angle / 360.0 * progressMax).toInt()
        if (isThumb1) {
            setProgressInternal(progress, progress2, true, false)
        } else {
            setProgressInternal(progress1, progress, true, false)
        }
    }

    private fun <T> uiProperty(value: T) = Delegates.observable(value) { _, old, new ->
        if (old != new) post { invalidate() }
    }

    private companion object : KLogging()

    private class ProgressInfo {
        var progress = 0
        var angle = 0
        var sdf = 0
    }

    private class Thumb(context: Context, val updateLocation: (x: Float, y: Float) -> Unit) : ImageView(context) {

        init {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            isClickable = true
            isFocusable = true
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            logger.debug { "HIT: ${event.x}, ${event.y}" }
            return when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    if (event.x >= paddingLeft && event.y >= paddingTop) {
                        parent.requestDisallowInterceptTouchEvent(true)
                        super.onTouchEvent(event)
                    } else {
                        false
                    }
                MotionEvent.ACTION_MOVE -> {
                    updateLocation(event.x, event.y)
                    true
                }
                else -> {
                    super.onTouchEvent(event)
                }
            }
        }

        private companion object : KLogging()

    }
}