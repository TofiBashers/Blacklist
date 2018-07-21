package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class BlacklistPhoneNumberItem(var dbId: Long? = null,
                                    override var number: String,
                                    override var isCallsBlocked: Boolean,
                                    override var isSmsBlocked: Boolean)
    : BaseBlacklistPhone(number, isCallsBlocked, isSmsBlocked){

    constructor() : this(null, "", false, false)
}
