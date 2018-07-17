package com.gmail.tofibashers.blacklist.di.app

import com.gmail.tofibashers.blacklist.SmsAndCallsTrackingService
import com.gmail.tofibashers.blacklist.data.SynchronizeDataService
import com.gmail.tofibashers.blacklist.di.PerActivity
import com.gmail.tofibashers.blacklist.di.PerService
import com.gmail.tofibashers.blacklist.di.activity.*
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Module()
abstract class AndroidBindingsModule {

    @ContributesAndroidInjector(modules = arrayOf(BlacklistPhonenumberOptionsActivityModule::class))
    @PerActivity
    abstract fun provideOptionsActivityInjector() : BlacklistPhonenumberOptionsActivity

    @ContributesAndroidInjector(modules = arrayOf(BlacklistActivityModule::class))
    @PerActivity
    abstract fun provideMainActivityInjector() : BlacklistActivity

    @ContributesAndroidInjector(modules = arrayOf(SelectContactActivityModule::class))
    @PerActivity
    abstract fun provideSelectContactActivityInjector() : SelectContactActivity

    @ContributesAndroidInjector(modules = arrayOf(BlacklistContactOptionsActivityModule::class))
    @PerActivity
    abstract fun provideBlacklistContactOptionsActivityInjector() : BlacklistContactOptionsActivity

    @ContributesAndroidInjector(modules = arrayOf(TimeSettingsActivityModule::class))
    @PerActivity
    abstract fun provideTimeSettingsActivityInjector() : TimeSettingsActivity

    @ContributesAndroidInjector()
    @PerService
    abstract fun provideTrackingServiceInjector() : SmsAndCallsTrackingService

    @ContributesAndroidInjector()
    @PerService
    abstract fun provideSynchronizeDataServiceInjector() : SynchronizeDataService
}