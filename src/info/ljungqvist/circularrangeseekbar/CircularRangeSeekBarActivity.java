package info.ljungqvist.circularrangeseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class CircularRangeSeekBarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_range_seek_bar);
		
        CircularRangeSeekBar crsb = new CircularRangeSeekBar(this);
        crsb.setMaxProgress(20);
        crsb.setCircleOnSame(false);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        crsb.setLayoutParams(lp);
        
        ((RelativeLayout) findViewById(R.id.base)).addView(crsb);
    }
    
}
