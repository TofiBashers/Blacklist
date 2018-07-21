package com.gmail.tofibashers.blacklist.di.app

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistNavRoute
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewState
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsNavData
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsViewState
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsNavData
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsViewState
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactNavData
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactViewState
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsNavData
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsViewState
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Module
class LiveDataModule {

    @Provides
    @Singleton
    fun provideBlacklistViewStateData() : MutableLiveData<BlacklistViewState> = MutableLiveData()

    @Provides
    @Singleton
    fun provideMutableLiveDataOptionsViewState() : MutableLiveData<BlacklistPhonenumberOptionsViewState> = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventOptionsViewModelNavigation() : SingleLiveEvent<BlacklistPhonenumberOptionsNavData>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideMutableLiveDataTimeSettingsViewState() : MutableLiveData<TimeSettingsViewState> = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventTimeSettingsViewModelNavigation() : SingleLiveEvent<TimeSettingsNavData>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideSingleLiveEventSystemVerWarning() : SingleLiveEvent<GetBlacklistResult.SystemVerWarning>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideMutableLiveDataSelectContactViewState() : MutableLiveData<SelectContactViewState>
            = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventSelectContactNavData() : SingleLiveEvent<SelectContactNavData>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideMutableLiveDataBlacklistContactOptionsViewState() : MutableLiveData<BlacklistContactOptionsViewState>
            = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventBlacklistContactOptionsNavData() : SingleLiveEvent<BlacklistContactOptionsNavData>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideSingleLiveEventBlacklistContactNavData() : SingleLiveEvent<BlacklistNavRoute>
            = SingleLiveEvent()
}