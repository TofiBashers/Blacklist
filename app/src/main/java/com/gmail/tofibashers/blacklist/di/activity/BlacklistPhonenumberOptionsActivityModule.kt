package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.di.PerActivity
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsViewModel
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 21.01.2018.
 */

@Module
class BlacklistPhonenumberOptionsActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(optionsActivity: BlacklistPhonenumberOptionsActivity, viewModelFactory: ViewModelFactory) : BlacklistPhonenumberOptionsViewModel {
        return ViewModelProviders.of(optionsActivity, viewModelFactory)
                .get(BlacklistPhonenumberOptionsViewModel::class.java)
    }
}