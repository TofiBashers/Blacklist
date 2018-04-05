package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 03.03.2018.
 */
sealed class GetBlacklistResult {
    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListWithIgnoreResult(val items: List<BlacklistItem>,
                                    val ignoreHidden: Boolean) : List<BlacklistItem> by items, GetBlacklistResult()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class SystemVerWarning(val warningType: SystemVerWarningType) : GetBlacklistResult()
}