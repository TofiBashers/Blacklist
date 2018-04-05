package com.gmail.tofibashers.blacklist.ui.blacklist

import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 20.01.2018.
 */
sealed class BlacklistViewState {
    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListViewState(val blackListWithIgnoreHidden: GetBlacklistResult.ListWithIgnoreResult) : BlacklistViewState()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : BlacklistViewState()
}