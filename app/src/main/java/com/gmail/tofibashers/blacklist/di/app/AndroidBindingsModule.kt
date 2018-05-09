package com.gmail.tofibashers.blacklist.di.app

import android.app.Activity
import android.app.Service
import com.gmail.tofibashers.blacklist.SmsAndCallsTrackingService
import com.gmail.tofibashers.blacklist.data.SynchronizeDataService
import com.gmail.tofibashers.blacklist.di.activity.*
import com.gmail.tofibashers.blacklist.di.service.SynchronizeDataServiceSubcomponent
import com.gmail.tofibashers.blacklist.di.service.TrackingServiceSubcomponent
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
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
                SelectContactActivitySubcomponent::class,
                BlacklistContactOptionsActivitySubcomponent::class,
                TimeSettingsActivitySubcomponent::class,
                TrackingServiceSubcomponent::class,
                SynchronizeDataServiceSubcomponent::class
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
    @ActivityKey(SelectContactActivity::class)
    abstract fun provideSelectContactActivityInjector(selectContactActivityComponentBuilder: SelectContactActivitySubcomponent.Builder)
            : AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(BlacklistContactOptionsActivity::class)
    abstract fun provideBlacklistContactOptionsActivityInjector(blacklistContactOptionsActivityComponentBuilder: BlacklistContactOptionsActivitySubcomponent.Builder)
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

    @Binds
    @IntoMap
    @ServiceKey(SynchronizeDataService::class)
    abstract fun provideSynchronizeDataServiceInjector(syncServiceComponentBuilder: SynchronizeDataServiceSubcomponent.Builder)
            : AndroidInjector.Factory<out Service>
}