package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 17.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class BlacklistContactPhoneNumberItem(var dbId: Long? = null,
                                           var deviceDbId: Long? = null,
                                           var number: String,
                                           var isCallsBlocked: Boolean,
                                           var isSmsBlocked: Boolean)