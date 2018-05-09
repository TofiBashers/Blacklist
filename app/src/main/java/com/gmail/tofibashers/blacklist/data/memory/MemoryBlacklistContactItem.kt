package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 21.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryBlacklistContactItem(var dbId: Long? = null,
                                      var deviceDbId: Long? = null,
                                      var deviceKey: String? = null,
                                      var name: String,
                                      var photoUrl: String? = null)