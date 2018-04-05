package com.gmail.tofibashers.blacklist.ui.options

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistItemAndValidState
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class OptionsViewState{

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val state: InteractionModeWithBlacklistItemAndValidState) : OptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataWithErrorViewState(val state: InteractionModeWithBlacklistItemAndValidState)
        : OptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : OptionsViewState()
}