package com.gmail.tofibashers.blacklist.di.activity

import com.gmail.tofibashers.blacklist.ui.blacklist.BlacklistActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Subcomponent(modules = arrayOf(BlacklistActivityModule::class))
@PerActivity
interface BlacklistActivitySubcomponent : AndroidInjector<BlacklistActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BlacklistActivity>()
}