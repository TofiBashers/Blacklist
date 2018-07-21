package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 10.05.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryWhitelistContactPhone(var deviceDbId: Long? = null,
                                       var number: String)