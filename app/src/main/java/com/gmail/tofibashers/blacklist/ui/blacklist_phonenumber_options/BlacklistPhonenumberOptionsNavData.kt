package com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options

import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class BlacklistPhonenumberOptionsNavData {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ListRoute(val savingResult: SavingResult) : BlacklistPhonenumberOptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class SavedNumberAlreadyExistsRoute : BlacklistPhonenumberOptionsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ActivityIntervalDetailsRoute : BlacklistPhonenumberOptionsNavData()
}