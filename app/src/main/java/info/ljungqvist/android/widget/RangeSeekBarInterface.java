package info.ljungqvist.android.widget;

import android.graphics.drawable.Drawable;
import android.widget.SeekBar;

/*
 * Copyright 2013-2014 Petter Ljungqvist (petter@ljungqvist.info)
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
public interface RangeSeekBarInterface {
    interface OnRangeSeekBarChangeListener extends SeekBar.OnSeekBarChangeListener {
        void onProgress2Changed(RangeSeekBar seekBar, int progress, boolean fromUser);
    }
    int getProgress2();
    void incrementProgress2By(int diff);
    void setProgress2(int progress2);
    Drawable getThumb2();
    int getThumb2Offset();
    void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener onRangeSeekBarChangeListener);
}
