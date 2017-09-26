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

package info.ljungqvist.android.activity

import android.app.Activity
import android.os.Bundle
import info.ljungqvist.android.widget.BuildConfig
import info.ljungqvist.android.widget.CircularRangeSeekBar2

import info.ljungqvist.android.widget.R
import mu.KLogging
import org.slf4j.impl.HandroidLoggerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class TestActivity : Activity() {

    init {
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG;
    }

    var count = 0
    var f: ScheduledFuture<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val bar: CircularRangeSeekBar2 = findViewById(R.id.circular_range_seek_bar_2)

        f = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            logger.debug { "#run $count  " }
            bar.setProgress(count % 120, (count + 40) % 100)
            logger.debug { "#run  notdone $count  " }
            count++
            logger.debug { "#run done $count  " }
        }, 100, 100, TimeUnit.MILLISECONDS)
    }

    companion object : KLogging()
}
