package com.gmail.tofibashers.blacklist.di.activity

import android.arch.lifecycle.ViewModelProviders
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistAdapter
import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistViewModel
import com.gmail.tofibashers.blacklist.ui.common.ViewModelFactory
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactAdaper
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactViewModel
import dagger.Module
import dagger.Provides


/**
 * Created by TofiBashers on 11.04.2018.
 */
@Module
class SelectContactActivityModule {

    @Provides
    @PerActivity
    fun provideViewModel(selectContactActivity: SelectContactActivity, viewModelFactory: ViewModelFactory) : SelectContactViewModel {
        return ViewModelProviders.of(selectContactActivity, viewModelFactory)
                .get(SelectContactViewModel::class.java)
    }

    @Provides
    @PerActivity
    fun provideSelectContactAdapter(selectContactActivity: SelectContactActivity) : SelectContactAdaper =
            SelectContactAdaper(selectContactActivity.contactClickListener)
}