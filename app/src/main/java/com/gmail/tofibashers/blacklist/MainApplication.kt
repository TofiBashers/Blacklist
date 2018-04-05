package com.gmail.tofibashers.blacklist

import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.gmail.tofibashers.blacklist.di.app.DaggerAppComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by TofiBashers on 20.01.2018.
 */
open class MainApplication : DaggerApplication(){

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        whenBaseContextAttached(base)
    }

    override fun onCreate() {
        super.onCreate()
        initLibraries()
        startService(Intent(this, SmsAndCallsTrackingService::class.java))
    }

    open fun whenBaseContextAttached(base: Context){
        MultiDex.install(this)
    }

    open fun initLibraries(){
        Fabric.with(this, Crashlytics.Builder()
                .core(CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build())
        Stetho.initializeWithDefaults(this)
        JodaTimeAndroid.init(this)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this)
}