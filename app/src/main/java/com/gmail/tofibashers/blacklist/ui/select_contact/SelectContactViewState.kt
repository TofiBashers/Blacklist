package com.gmail.tofibashers.blacklist.ui.select_contact

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 14.04.2018.
 */
sealed class SelectContactViewState {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val state: List<WhitelistContactItemWithHasPhones>) : SelectContactViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : SelectContactViewState()
}