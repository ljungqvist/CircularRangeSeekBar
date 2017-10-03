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
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class TestActivity : Activity() {

    init {
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG;
    }

    var p1 = 0f
    var p2 = 0f
    var f: ScheduledFuture<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val bar: CircularRangeSeekBar2 = findViewById(R.id.circular_range_seek_bar_2)

        bar.progressMax = 1000

        val r: Random = Random()

        f = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            bar.setProgress(p1.toInt() % 1000, p2.toInt() % 1000)
            p1 += r.nextInt(100).toFloat() / 10
            p2 += r.nextInt(100).toFloat() / 10
        }, 100, 20, TimeUnit.MILLISECONDS)
    }

    companion object : KLogging()
}
