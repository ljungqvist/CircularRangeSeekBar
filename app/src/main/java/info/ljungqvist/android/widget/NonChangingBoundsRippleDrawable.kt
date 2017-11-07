package info.ljungqvist.android.widget

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable

internal class NonChangingBoundsRippleDrawable(color: ColorStateList?, content: Drawable?, mask: Drawable?)
    : RippleDrawable(color, content, mask) {

    internal fun setBoundsInternal(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        // do nothing
    }
}
