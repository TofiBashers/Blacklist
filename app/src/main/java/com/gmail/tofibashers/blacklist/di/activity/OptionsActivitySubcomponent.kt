package com.gmail.tofibashers.blacklist.di.activity

import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Subcomponent(modules = arrayOf(OptionsActivityModule::class))
@PerActivity
interface OptionsActivitySubcomponent : AndroidInjector<OptionsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<OptionsActivity>()
}