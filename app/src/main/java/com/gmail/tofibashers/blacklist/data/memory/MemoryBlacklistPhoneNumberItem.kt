package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 06.03.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryBlacklistPhoneNumberItem(var dbId: Long? = null,
                                          var number: String,
                                          var isCallsBlocked: Boolean,
                                          var isSmsBlocked: Boolean)