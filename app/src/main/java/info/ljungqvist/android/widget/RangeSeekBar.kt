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

package info.ljungqvist.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.appcompat.R
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.SeekBar
import mu.KLogging

class RangeSeekBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.seekBarStyle) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    var initialState: IntArray = drawableState

    var thumb2: Drawable? = thumb
            ?.constantState
            ?.newDrawable(resources)
            ?.mutate()

    var p = 0

    private var touchDownX: Float = 0f
    private var isDragging: Boolean = false
    private var progress2: Int = 0

    init {
        thumb2?.run {
            callback = this@RangeSeekBar
            state = drawableState
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                p = progress * 5
                thumb2?.apply {
                    setBounds(p, 0, p + intrinsicWidth, intrinsicHeight)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    background?.setHotspot(p.toFloat(), 0f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                drawableStateChanged()
            }

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        thumb2?.let {
            //            logger.debug { "_DEBUG_PETTER drawing" }
            val saveCount = canvas.save()
            canvas.translate((paddingLeft - thumbOffset).toFloat(), paddingTop.toFloat())

//            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
            canvas.restoreToCount(saveCount)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        thumb2?.setDrawableState(drawableState)
        thumb?.setDrawableState(initialState)
        progressDrawable?.setDrawableState(initialState)

//        val thumb = thumb2
//        if (thumb != null && thumb.isStateful && thumb.setState(drawableState)) {
//            invalidateDrawable(thumb)
//        }
    }

    private fun Drawable.setDrawableState(state: IntArray) {
        if (isStateful && setState(state)) {
            invalidateDrawable(thumb)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                if (isInScrollingContainer()) {
                    touchDownX = event.x
                } else {
                    startDrag(event)
                }

            MotionEvent.ACTION_MOVE ->
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    val x = event.x
                    if (Math.abs(x - touchDownX) > scaledTouchSlop) {
                        startDrag(event)
                    }
                }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    // Touch up when we never crossed the touch slop threshold should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
        }
        return true
    }


    private fun startDrag(event: MotionEvent) {
        isPressed = true

        thumb2
                ?.bounds
                .let(this::invalidate)

        onStartTrackingTouch()
        trackTouchEvent(event)
        attemptClaimDrag()
    }

    private fun onStartTrackingTouch() {
        isDragging = true
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val x = Math.round(event.x)
        val y = Math.round(event.y)
        val width = width
        val availableWidth = width - paddingLeft - paddingRight

        val scale: Float
        var progress = 0.0f

        if (x < paddingLeft) {
            scale = 0.0f
        } else if (x > width - paddingRight) {
            scale = 1.0f
        } else {
            scale = (x - paddingLeft) / availableWidth.toFloat()
        }

        val max = max
        progress += scale * max

        setHotspot(x.toFloat(), y.toFloat())
        setProgress2Internal(Math.round(progress), true, false)
    }

    private fun setHotspot(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            background?.setHotspot(x, y)
        }
    }

    @Synchronized private fun setProgress2Internal(progress: Int, fromUser: Boolean, animate: Boolean): Boolean {
        var p = constrain(progress, 0, max)

        if (p == progress2) {
            // No change from current.
            return false
        }

        progress2 = progress
        refreshProgress(R.id.progress, mProgress, fromUser, animate)
        return true
    }

    companion object : KLogging()

}