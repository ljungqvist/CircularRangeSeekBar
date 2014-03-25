package info.ljungqvist.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CircularRangeSeekBar extends SeekBar {

	public CircularRangeSeekBar(Context context) {
		super(context, null);
	}
	public CircularRangeSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.seekBarStyle);
	}
	public CircularRangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}