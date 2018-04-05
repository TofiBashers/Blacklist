package com.gmail.tofibashers.blacklist.di.activity

import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 23.01.2018.
 */

@Subcomponent(modules = arrayOf(TimeSettingsActivityModule::class))
@PerActivity
interface TimeSettingsActivitySubcomponent : AndroidInjector<TimeSettingsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TimeSettingsActivity>()
}