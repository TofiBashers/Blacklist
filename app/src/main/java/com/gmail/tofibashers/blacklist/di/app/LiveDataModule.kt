package com.gmail.tofibashers.blacklist.di.app

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewState
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistNavRoute
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.options.OptionsNavData
import com.gmail.tofibashers.blacklist.ui.options.OptionsViewState
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
    fun provideBlackListViewStateData() : MutableLiveData<BlacklistViewState> = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventBlacklistViewModelNavigation() : SingleLiveEvent<BlacklistNavRoute>
            = SingleLiveEvent()

    @Provides
    @Singleton
    fun provideMutableLiveDataOptionsViewState() : MutableLiveData<OptionsViewState> = MutableLiveData()

    @Provides
    @Singleton
    fun provideSingleLiveEventOptionsViewModelNavigation() : SingleLiveEvent<OptionsNavData>
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
}