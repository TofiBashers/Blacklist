package com.gmail.tofibashers.blacklist

import android.content.Context
import net.danlew.android.joda.JodaTimeAndroid
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


/**
 * Created by TofiBashers on 18.03.2018.
 */
class TestApplication : MainApplication() {

    override fun whenBaseContextAttached(base: Context) {}

    override fun initLibraries() {
        JodaTimeAndroid.init(this)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build())
    }
}