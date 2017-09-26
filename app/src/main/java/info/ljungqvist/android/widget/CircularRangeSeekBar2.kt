package info.ljungqvist.android.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import mu.KLogging

class CircularRangeSeekBar2 : FrameLayout {

    constructor(context: Context)
            : super(context)

    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0)
            : super(context, attributeSet, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attributeSet, defStyleAttr, defStyleRes)

    private val thumb1: Thumb = Thumb(context, this)
    private val thumb2: Thumb = Thumb(context, this)

    private var progress1 = 0
    private var progress2 = 50
    private var progressMax = 100

    private var size = -1
    private var thumbSize = -1

    private var needsUpdating = false

    init {
        setBackgroundColor(Color.LTGRAY)
        thumb1.listener = { x, y -> thumbTouch(1, x, y) }
        thumb2.listener = { x, y -> thumbTouch(2, x, y) }
        setImageResource(R.drawable.scrubber_control_normal_holo)
    }

    fun setImageResource(@DrawableRes resId: Int) {
        thumb1.setImageResource(resId)
        thumb2.setImageResource(resId)
        thumbSize = Math.max(
                thumb1.drawable.let { Math.max(it.intrinsicWidth, it.intrinsicHeight) },
                thumb2.drawable.let { Math.max(it.intrinsicWidth, it.intrinsicHeight) }
        )
        invalidateThis()
    }

    fun setProgress(progress1: Int, progress2: Int) {
        setProgressInternal(progress1, progress2, false)
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
            invalidateThis()
        }
        val spec = MeasureSpec.makeMeasureSpec(newSize, MeasureSpec.EXACTLY)
        //MUST CALL THIS
        super.onMeasure(spec, spec)
    }

    override fun onDraw(canvas: Canvas) {
        val angle1 = progress1.toDouble() / progressMax * 360 - 90
        val angle2 = progress2.toDouble() / progressMax * 360 - 90

        if (needsUpdating) {
            setPadding(thumb1, angle1)
            setPadding(thumb2, angle2)
            needsUpdating = false
        }

        super.onDraw(canvas)
    }

    private fun setPadding(thumb: Thumb, angle: Double) {
        val mid = (size - thumbSize) / 2
        val paddingLeft = mid + (Math.cos(Math.toRadians(angle)) * mid).toInt()
        val paddingTop = mid + (Math.sin(Math.toRadians(angle)) * mid).toInt()
        thumb.setPadding(paddingLeft, paddingTop, 0, 0)
    }


    private fun setProgressInternal(progress1: Int, progress2: Int, fromUser: Boolean) {
        var changed = false

        progress1
                .limit()
                .takeUnless { it == this.progress1 }
                ?.let {
                    this.progress1 = it
                    changed = true
                }
        progress2
                .limit()
                .takeUnless { it == this.progress2 }
                ?.let {
                    this.progress2 = it
                    changed = true
                }

        if (changed) {
            invalidateThis()
        }
    }

    private fun Int.limit(): Int =
        when {
            this < 0 -> 0
            this >= progressMax -> progressMax - 1
            else -> this
        }

    private fun thumbTouch(thumb: Int, x: Float, y: Float) {

    }

    private fun invalidateThis() {
        needsUpdating = true
        post { invalidate() }
    }

    private companion object : KLogging()

    private class ProgressInfo {
        var progress = 0
        var angle = 0
        var sdf = 0
    }

    private class Thumb(context: Context, val seekCircle: CircularRangeSeekBar2) : ImageView(context) {

        internal var listener: ((Float, Float) -> Unit)? = null

        init {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            isClickable = true
            isFocusable = true
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            logger.debug { "HIT: ${event.x}, ${event.y}" }
            if (event.action == MotionEvent.ACTION_DOWN) {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            listener?.invoke(event.x, event.y)
            return super.onTouchEvent(event)
        }

        private companion object : KLogging()

    }
}