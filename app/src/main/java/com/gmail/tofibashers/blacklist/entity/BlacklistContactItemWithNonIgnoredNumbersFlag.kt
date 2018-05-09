package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 08.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class BlacklistContactItemWithNonIgnoredNumbersFlag(var dbId: Long? = null,
                                                         var deviceDbId: Long? = null,
                                                         var deviceKey: String? = null,
                                                         var name: String,
                                                         var photoUrl: String? = null,
                                                         var withNonIgnoredNumbers: Boolean)