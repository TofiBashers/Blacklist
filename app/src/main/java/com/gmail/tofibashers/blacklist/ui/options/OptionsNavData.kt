package com.gmail.tofibashers.blacklist.ui.options

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class OptionsNavData {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListRoute(val optionsResult: OptionsResult) : OptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class SavedNumberAlreadyExistsRoute : OptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ActivityIntervalDetailsRoute : OptionsNavData()
}