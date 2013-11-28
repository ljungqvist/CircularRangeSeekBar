package info.ljungqvist.circularrangeseekbar.app;

import info.ljungqvist.circularrangeseekbar.CircularRangeSeekBar;
import info.ljungqvist.circularrangeseekbar.CircularRangeSeekBar.OnSeekChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
                
        CircularRangeSeekBar crsb = new CircularRangeSeekBar(this);
        crsb.setMaxProgress(20);
        crsb.setCircleOnSame(false);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        crsb.setLayoutParams(lp);

        
        ((LinearLayout) findViewById(R.id.base)).addView(crsb);

        crsb.setSeekBarChangeListener(new OnSeekChangeListener() {

			@Override
			public void onProgressChange(
					CircularRangeSeekBar view, 
					int progress1, 
					int progress2,
					boolean fromUser) {
				if (!fromUser)
					Log.d("CircularRangeSeekBar", 
							"Progress: from " + progress1 + " to " + progress2);
			}
		});
    }

}
