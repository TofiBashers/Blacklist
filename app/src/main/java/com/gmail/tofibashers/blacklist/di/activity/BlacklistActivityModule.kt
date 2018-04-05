package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistAdapter
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 21.01.2018.
 */
@Module
class BlacklistActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(blacklistActivity: BlacklistActivity, viewModelFactory: ViewModelFactory) : BlacklistViewModel {
        return ViewModelProviders.of(blacklistActivity, viewModelFactory)
                .get(BlacklistViewModel::class.java)
    }

    @Provides
    @PerActivity
    fun provideBlackListAdapter(blacklistActivity: BlacklistActivity) : BlacklistAdapter = BlacklistAdapter(blacklistActivity)
}