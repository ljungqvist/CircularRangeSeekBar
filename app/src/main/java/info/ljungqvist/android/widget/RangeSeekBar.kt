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
import android.support.v7.appcompat.R
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import mu.KLogging

class RangeSeekBar : AppCompatSeekBar {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarStyle)
    constructor(context: Context) : this(context, null)

    var thumb2: Drawable? = getThumb()
            ?.constantState
            ?.newDrawable()

    init {
        thumb2?.callback = this
        thumb2?.state = drawableState
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        thumb2?.let {
            canvas.save()
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
            canvas.restore()
        }
    }

    companion object : KLogging()

}