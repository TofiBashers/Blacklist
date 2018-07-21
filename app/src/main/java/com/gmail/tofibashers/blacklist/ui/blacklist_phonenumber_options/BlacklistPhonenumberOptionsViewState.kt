package com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistPhoneNumberItemAndValidState
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class BlacklistPhonenumberOptionsViewState {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val state: InteractionModeWithBlacklistPhoneNumberItemAndValidState) : BlacklistPhonenumberOptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataWithErrorViewState(val state: InteractionModeWithBlacklistPhoneNumberItemAndValidState)
        : BlacklistPhonenumberOptionsViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : BlacklistPhonenumberOptionsViewState()
}