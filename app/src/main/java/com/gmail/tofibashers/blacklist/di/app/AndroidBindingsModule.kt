package com.gmail.tofibashers.blacklist.di.app

import android.app.Activity
import android.app.Service
import com.gmail.tofibashers.blacklist.SmsAndCallsTrackingService
import com.gmail.tofibashers.blacklist.di.activity.BlacklistActivitySubcomponent
import com.gmail.tofibashers.blacklist.di.activity.OptionsActivitySubcomponent
import com.gmail.tofibashers.blacklist.di.activity.TimeSettingsActivitySubcomponent
import com.gmail.tofibashers.blacklist.di.service.TrackingServiceSubcomponent
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Module(
        subcomponents = arrayOf(
                BlacklistActivitySubcomponent::class,
                OptionsActivitySubcomponent::class,
                TimeSettingsActivitySubcomponent::class,
                TrackingServiceSubcomponent::class
        ))
abstract class AndroidBindingsModule {

    @Binds
    @IntoMap
    @ActivityKey(OptionsActivity::class)
    abstract fun provideOptionsActivityInjector(optionsActivitySubcomponentBuilder: OptionsActivitySubcomponent.Builder)
            : AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(BlacklistActivity::class)
    abstract fun provideMainActivityInjector(blacklistActivityComponentBuilder: BlacklistActivitySubcomponent.Builder)
            : AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(TimeSettingsActivity::class)
    abstract fun provideTimeSettingsActivityInjector(timeSettingsActivitySubcomponentBuilder: TimeSettingsActivitySubcomponent.Builder)
            : AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ServiceKey(SmsAndCallsTrackingService::class)
    abstract fun provideTrackingServiceInjector(trackingServiceComponentBuilder: TrackingServiceSubcomponent.Builder)
            : AndroidInjector.Factory<out Service>
}