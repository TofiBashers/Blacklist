package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 10.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class WhitelistContactItemWithHasPhones(var deviceDbId: Long,
                                             var deviceKey: String,
                                             var name: String,
                                             var photoUrl: String,
                                             var hasPhones: Boolean)