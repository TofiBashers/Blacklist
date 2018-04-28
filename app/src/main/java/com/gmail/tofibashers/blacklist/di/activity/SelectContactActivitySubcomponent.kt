package com.gmail.tofibashers.blacklist.di.activity

import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 11.04.2018.
 */
@Subcomponent(modules = arrayOf(SelectContactActivityModule::class))
@PerActivity
interface SelectContactActivitySubcomponent : AndroidInjector<SelectContactActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SelectContactActivity>()
}