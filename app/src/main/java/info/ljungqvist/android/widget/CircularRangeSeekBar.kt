package info.ljungqvist.android.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import mu.KLogging
import kotlin.properties.Delegates

class CircularRangeSeekBar : FrameLayout {

    constructor(context: Context)
            : super(context)

    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0)
            : super(context, attributeSet, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attributeSet, defStyleAttr, defStyleRes)


    var seekBarChangeListener: OnSeekChangeListener? = null

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
    private var thumbActive: Thumb? = null

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
        setBackgroundColor(Color.TRANSPARENT)
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

    override fun onTouchEvent(event: MotionEvent): Boolean =
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val mid = size.toFloat() / 2.0
                    val innerR = mid - thumbSize
                    val dx = event.x - mid
                    val dy = event.y - mid
                    val rSq = dx * dx + dy * dy
                    logger.debug { "r = ${Math.sqrt(rSq)}, outer = $mid, inner = $innerR" }
                    if (rSq < mid * mid && rSq > innerR * innerR) {
                        if (sqDist(event.x, event.y, thumb1) <= sqDist(event.x, event.y, thumb2)) {
                            thumb1
                        } else {
                            thumb2
                        }
                                .also { thumbActive = it }
                                .internalOnTouchEvent(event)
                    } else {
                        super.onTouchEvent(event)
                    }
                }
                MotionEvent.ACTION_MOVE ->
                    thumbActive
                            ?.internalOnTouchEvent(event)
                            ?: super.onTouchEvent(event)
                else ->
                    thumbActive
                            ?.also { thumbActive = null }
                            ?.internalOnTouchEvent(event)
                            ?: super.onTouchEvent(event)
            }

    private fun sqDist(x: Float, y: Float, thumb: Thumb): Float {
        val dx = thumb.paddingLeft + thumbSize / 2 - x
        val dy = thumb.paddingTop + thumbSize / 2 - y
        return dx * dx + dy * dy
    }

    private fun setPadding(thumb: Thumb, angle: Double) {
        val mid = (size - thumbSize) / 2
        val paddingLeft = mid + (Math.cos(Math.toRadians(angle)) * mid).toInt()
        val paddingTop = mid + (Math.sin(Math.toRadians(angle)) * mid).toInt()
        thumb.setPadding(paddingLeft, paddingTop, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            thumb.ripple
                    ?.also {
                        thumb.drawableHotspotChanged(
                                paddingLeft.toFloat() + thumbSize / 2,
                                paddingTop.toFloat() + thumbSize / 2)
                    }
                    ?.setBounds(
                            paddingLeft,
                            paddingTop,
                            paddingLeft + 100,
                            paddingTop + 100)
        }
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
            post {
                invalidate()
                seekBarChangeListener?.onProgressChange(this, progress1, progress2, fromUser)
            }
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
        val halfSize = size.toDouble() / 2.0
        val x = xIn.toDouble() - halfSize
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

    companion object : KLogging() {

        fun OnSeekChangeListener(listener: (CircularRangeSeekBar, Int, Int, Boolean) -> Unit): OnSeekChangeListener =
                object : OnSeekChangeListener {
                    override fun onProgressChange(view: CircularRangeSeekBar, progress1: Int, progress2: Int, fromUser: Boolean) =
                            listener(view, progress1, progress2, fromUser)
                }

    }

    private class Thumb(context: Context, val updateLocation: (x: Float, y: Float) -> Unit) : ImageView(context) {

        internal val ripple: Drawable? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.LTGRAY)), null, null)
                            .also { background = it }
                } else {
                    null
                }

        init {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            isClickable = true
            isFocusable = true
        }

        internal fun internalOnTouchEvent(event: MotionEvent): Boolean =
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        parent.requestDisallowInterceptTouchEvent(true)
                        super.onTouchEvent(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        updateLocation(event.x, event.y)
                        true
                    }
                    else ->
                        super.onTouchEvent(event)
                }

        override fun onTouchEvent(event: MotionEvent): Boolean =
                when (event.action) {
                    MotionEvent.ACTION_DOWN ->
                        if (event.x >= paddingLeft && event.y >= paddingTop) {
                            internalOnTouchEvent(event)
                        } else {
                            false
                        }
                    else ->
                        internalOnTouchEvent(event)
                }

        private companion object : KLogging()

    }


    interface OnSeekChangeListener {

        fun onProgressChange(view: CircularRangeSeekBar, progress1: Int, progress2: Int, fromUser: Boolean)
    }
}