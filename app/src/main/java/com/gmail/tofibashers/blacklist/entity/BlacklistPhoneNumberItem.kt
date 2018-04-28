package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class BlacklistPhoneNumberItem(var dbId: Long? = null,
                                    var number: String,
                                    var isCallsBlocked: Boolean,
                                    var isSmsBlocked: Boolean){

    constructor() : this(null, "", false, false)
}
