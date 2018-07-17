package com.gmail.tofibashers.blacklist.di.app

import android.arch.lifecycle.ViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsViewModel
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactViewModel
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
    @ViewModelKey(BlacklistPhonenumberOptionsViewModel::class)
    abstract fun provideBlacklistPhonenumberOptionsViewModel(blacklistPhonenumberOptionsViewModel: BlacklistPhonenumberOptionsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlacklistViewModel::class)
    abstract fun provideBlacklistViewModel(blacklistViewModel: BlacklistViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TimeSettingsViewModel::class)
    abstract fun provideTimeSettingsViewModel(timeSettingsViewModel: TimeSettingsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectContactViewModel::class)
    abstract fun provideSelectContactViewModel(selectContactViewModel: SelectContactViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlacklistContactOptionsViewModel::class)
    abstract fun provideBlacklistContactOptionsViewModel(blacklistContactOptionsViewModel: BlacklistContactOptionsViewModel) : ViewModel
}