package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 15.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryWhitelistContactItem(var deviceDbId: Long,
                                      var deviceKey: String,
                                      var name: String,
                                      var photoUrl: String)