package info.ljungqvist.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class RangeSeekBar extends SeekBar {

	public interface OnRangeSeekBarChangeListener extends OnSeekBarChangeListener {
		void onSecondaryProgressChanged(RangeSeekBar seekBar, int progress, boolean fromUser);
	}
	
	private OnRangeSeekBarChangeListener onRangeSeekBarChangeListener;

	public RangeSeekBar(Context context) {
		super(context, null);
	}
	public RangeSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.seekBarStyle);
	}
	public RangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
		this.onRangeSeekBarChangeListener = onRangeSeekBarChangeListener;
		setOnSeekBarChangeListener(onRangeSeekBarChangeListener);
	}
	
	@Override
	protected void  onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//updateThumbPos(w, h);
	}

}
