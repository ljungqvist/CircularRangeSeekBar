package info.ljungqvist.android.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
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

    init {
        thumb1.setImageResource(R.drawable.scrubber_control_normal_holo)
        thumb2.setImageResource(R.drawable.scrubber_control_normal_holo)
        thumb1.setPadding(100, 100, 100, 100)
        thumb2.setPadding(200, 200, 100, 100)
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

        val size = Math.min(width, height)
        val spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        //MUST CALL THIS
        super.onMeasure(spec, spec)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        logger.debug { "==HIT: ${event?.x}, ${event?.y}" }
        return super.onTouchEvent(event)
    }

    companion object : KLogging()

    private class Thumb(context: Context, val seekCircle: CircularRangeSeekBar2) : ImageView(context) {

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
            return super.onTouchEvent(event)
        }

        companion object : KLogging()

    }
}