package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsViewModel
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 23.01.2018.
 */

@Module
class TimeSettingsActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(timeSettingsActivity: TimeSettingsActivity, viewModelFactory: ViewModelFactory) : TimeSettingsViewModel {
        return ViewModelProviders.of(timeSettingsActivity, viewModelFactory)
                .get(TimeSettingsViewModel::class.java)
    }
}