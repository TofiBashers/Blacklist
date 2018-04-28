package com.gmail.tofibashers.blacklist.ui.select_contact

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 11.04.2018.
 */
sealed class SelectContactNavData {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class BlacklistContactOptionsRoute : SelectContactNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class EditContactRoute(val contactId: Long, val contactKey: String) : SelectContactNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ParentRoute : SelectContactNavData()
}