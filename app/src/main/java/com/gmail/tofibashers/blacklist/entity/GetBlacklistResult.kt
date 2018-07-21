package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 03.03.2018.
 */
sealed class GetBlacklistResult {
    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListWithIgnoreResult(val sectionedItems: List<SectionBlacklistItem>,
                                    val ignoreHidden: Boolean) : List<SectionBlacklistItem> by sectionedItems, GetBlacklistResult()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class SystemVerWarning(val warningType: SystemVerWarningType) : GetBlacklistResult()
}