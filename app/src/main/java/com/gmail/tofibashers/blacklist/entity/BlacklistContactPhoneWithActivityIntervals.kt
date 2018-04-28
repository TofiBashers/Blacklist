package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 20.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class BlacklistContactPhoneWithActivityIntervals(var dbId: Long? = null,
                                                      var deviceDbId: Long? = null,
                                                      override var number: String,
                                                      override var isCallsBlocked: Boolean,
                                                      override var isSmsBlocked: Boolean,
                                                      override var activityIntervals: List<ActivityInterval>)
    : BaseBlacklistElementWithActivityIntervals(number,
        isCallsBlocked,
        isSmsBlocked,
        activityIntervals), List<ActivityInterval> by activityIntervals