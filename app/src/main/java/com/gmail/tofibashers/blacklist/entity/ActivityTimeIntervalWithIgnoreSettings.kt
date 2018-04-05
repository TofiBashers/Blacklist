package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory
import org.joda.time.LocalTime


/**
 * Created by TofiBashers on 30.01.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class ActivityTimeIntervalWithIgnoreSettings(
        var isCallsBlocked: Boolean,
        var isSmsBlocked: Boolean,
        var beginTime: LocalTime,
        var endTime: LocalTime)