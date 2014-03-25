package info.ljungqvist.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CircularSeekBar extends SeekBar {

	public CircularSeekBar(Context context) {
		super(context, null);
	}
	public CircularSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.seekBarStyle);
	}
	public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
