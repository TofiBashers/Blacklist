package com.gmail.tofibashers.blacklist.di.activity

import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Subcomponent(modules = arrayOf(BlacklistContactOptionsActivityModule::class))
@PerActivity
interface BlacklistContactOptionsActivitySubcomponent : AndroidInjector<BlacklistContactOptionsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BlacklistContactOptionsActivity>()
}