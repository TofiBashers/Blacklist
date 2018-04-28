package com.gmail.tofibashers.blacklist.ui.options

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistPhoneNumberItemAndValidState
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class OptionsViewState{

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val state: InteractionModeWithBlacklistPhoneNumberItemAndValidState) : OptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataWithErrorViewState(val state: InteractionModeWithBlacklistPhoneNumberItemAndValidState)
        : OptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : OptionsViewState()
}