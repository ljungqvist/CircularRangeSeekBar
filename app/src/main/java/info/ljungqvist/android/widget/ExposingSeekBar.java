/*
 * Copyright 2014-2015 Petter Ljungqvist (petter@ljungqvist.info)
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

package info.ljungqvist.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ExposingSeekBar extends SeekBar {

    private Method View_canResolveLayoutDirection;
    private Method Drawable_setLayoutDirection;
    private Method AbsSeekBar_setThumbPos;
    private Field ProgressBar_mMaxHeight;

    public ExposingSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try {
            View_canResolveLayoutDirection = View.class.getMethod("canResolveLayoutDirection");
            Drawable_setLayoutDirection = Drawable.class.getMethod("setLayoutDirection", int.class);
            AbsSeekBar_setThumbPos = AbsSeekBar.class.getDeclaredMethod("setThumbPos", int.class, Drawable.class, float.class, int.class);
            AbsSeekBar_setThumbPos.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            ProgressBar_mMaxHeight = ProgressBar.class.getDeclaredField("mMaxHeight");
            ProgressBar_mMaxHeight.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    protected void setDrawableLayoutDirection(Drawable drawable, int layoutDirection) {
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                setDrawableLayoutDirection17(drawable, layoutDirection);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    @TargetApi(17)
    private void setDrawableLayoutDirection17(Drawable drawable, int layoutDirection) throws InvocationTargetException, IllegalAccessException {
        if ((Boolean) View_canResolveLayoutDirection.invoke(this)) {
            Drawable_setLayoutDirection.invoke(drawable, layoutDirection);
        }
    }

    protected int getMaxHeight() {
        try {
            return (Integer) ProgressBar_mMaxHeight.get(this);
        } catch (NoSuchFieldError e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 10000;
    }

    protected void setThumbPos(int w, Drawable thumb, float scale, int gap) {
        Log.d("!!!!!!", "setThumbPos(" + w + ", " + (thumb == null ? "null" : "thumb") + ", " + scale + ", " + gap + ")");
        Log.d("!!!!!!", "before:" + thumb.getBounds().toShortString());
        try {
            AbsSeekBar_setThumbPos.invoke(this, w, thumb, scale, gap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d("!!!!!!", "after:" + thumb.getBounds().toShortString());
    }

}
