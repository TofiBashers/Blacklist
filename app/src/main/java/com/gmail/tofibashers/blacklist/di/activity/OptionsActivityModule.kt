package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.di.PerActivity
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import com.gmail.tofibashers.blacklist.ui.options.OptionsViewModel
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 21.01.2018.
 */

@Module
class OptionsActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(optionsActivity: OptionsActivity, viewModelFactory: ViewModelFactory) : OptionsViewModel {
        return ViewModelProviders.of(optionsActivity, viewModelFactory)
                .get(OptionsViewModel::class.java)
    }
}