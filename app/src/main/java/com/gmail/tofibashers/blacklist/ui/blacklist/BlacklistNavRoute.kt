package com.gmail.tofibashers.blacklist.ui.blacklist

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 17.07.2018.
 */
sealed class BlacklistNavRoute {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class BlacklistContactOptionsRoute : BlacklistNavRoute()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class BlacklistPhonenumberOptionsRoute : BlacklistNavRoute()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class SelectContactRoute : BlacklistNavRoute()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class ContactOpenInContactsAppRoute(val contactId: Long, val contactKey: String) : BlacklistNavRoute()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class PhonenumberCallRoute(val phoneNumber: String) : BlacklistNavRoute()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class PhonenumberSmsRoute(val phoneNumber: String) : BlacklistNavRoute()
}