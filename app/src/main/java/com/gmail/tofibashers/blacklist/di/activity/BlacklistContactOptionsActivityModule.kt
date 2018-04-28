package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistAdapter
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewModel
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity_MembersInjector
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsAdapter
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsViewModel
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Module
class BlacklistContactOptionsActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(contactOptionsActivity: BlacklistContactOptionsActivity,
                         viewModelFactory: ViewModelFactory) : BlacklistContactOptionsViewModel {
        return ViewModelProviders.of(contactOptionsActivity, viewModelFactory)
                .get(BlacklistContactOptionsViewModel::class.java)
    }

    @Provides
    @PerActivity
    fun provideBlacklistContactOptionsAdapter(contactOptionsActivity: BlacklistContactOptionsActivity) : BlacklistContactOptionsAdapter =
            BlacklistContactOptionsAdapter(contactOptionsActivity)
}