package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 16.04.2018.
 */
sealed class BlacklistContactOptionsNavData {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListRoute(val result: SavingResult) : BlacklistContactOptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ListRouteWithCancelAndChangedOrDeletedError : BlacklistContactOptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ActivityIntervalDetailsRoute : BlacklistContactOptionsNavData()
}