CircularRangeSeekBar
====================

A circular range seek bar library for Android.

The class is built on RaghavSood's project [AndroidCircularSeekBar](https://github.com/RaghavSood/AndroidCircularSeekBar)

Screenshot:

![Imgur](http://ljungqvist.info/CircularRangeSeekBar/screen.png)

Usage
-----

```java
import info.ljungqvist.circularrangeseekbar.CircularRangeSeekBar;
import info.ljungqvist.circularrangeseekbar.CircularRangeSeekBar.OnSeekChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        CircularRangeSeekBar crsb = new CircularRangeSeekBar(this);
        crsb.setMaxProgress(20);
        crsb.setCircleOnSame(false);
        LayoutParams lp = new LayoutParams(
        		LayoutParams.MATCH_PARENT, 
        		LayoutParams.MATCH_PARENT);
        crsb.setLayoutParams(lp);

        setContentView(crsb);

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
```

Used in
-------

 - [Locale/Tasker Twilight Plug-in](https://play.google.com/store/apps/details?id=com.terdelle.twilight)

Please let me know if you want your porject on this list.
