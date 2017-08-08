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
import android.widget.SeekBar
import mu.KLogging

class RangeSeekBar : AppCompatSeekBar {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarStyle)
    constructor(context: Context) : this(context, null)

    var initialState: IntArray = drawableState

    var thumb2: Drawable? = thumb
            ?.constantState
            ?.newDrawable(resources)
            ?.mutate()

    var p = 0

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

    override fun onTouchEvent(event: MotionEvent?): Boolean {


        val res = super.onTouchEvent(event)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            background?.setHotspot(p.toFloat(), 0f)
        }

        return res
    }

    companion object : KLogging()

}