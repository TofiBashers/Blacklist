package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 11.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class PhoneNumberIgnoringPolicy(var isCallsBlocked: Boolean,
                                     var isSmsBlocked: Boolean)