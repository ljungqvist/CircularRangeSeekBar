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

import android.view.ViewGroup
import android.view.ViewParent
import android.widget.AbsSeekBar


internal fun AbsSeekBar.isInScrollingContainer(): Boolean {
    var p: ViewParent? = parent
    while (p != null && p is ViewGroup) {
        if (p.shouldDelayChildPressedState()) {
            return true
        }
        p = p.parent
    }
    return false
}

internal fun AbsSeekBar.constrain(value: Int, min: Int, max: Int): Int = when {
    min > max -> throw IllegalArgumentException("'min' must not be grater than 'max' (min=$min > max=$max)")
    value < min -> min
    value > max -> max
    else -> value
}