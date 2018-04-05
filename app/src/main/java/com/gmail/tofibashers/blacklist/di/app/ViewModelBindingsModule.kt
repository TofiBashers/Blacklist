package com.gmail.tofibashers.blacklist.di.app

import android.arch.lifecycle.ViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewModel
import com.gmail.tofibashers.blacklist.ui.options.OptionsViewModel
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Module
abstract class ViewModelBindingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(OptionsViewModel::class)
    abstract fun provideOptionsViewModel(optionsViewModel: OptionsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlacklistViewModel::class)
    abstract fun provideBlacklistViewModel(blacklistViewModel: BlacklistViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TimeSettingsViewModel::class)
    abstract fun provideTimeSettingsViewModel(timeSettingsViewModel: TimeSettingsViewModel) : ViewModel
}