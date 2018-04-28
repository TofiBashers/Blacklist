package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistContactItemAndNumbersAndValidState
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 16.04.2018.
 */
sealed class BlacklistContactOptionsViewState {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val modeWithState: InteractionModeWithBlacklistContactItemAndNumbersAndValidState) : BlacklistContactOptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : BlacklistContactOptionsViewState()
}