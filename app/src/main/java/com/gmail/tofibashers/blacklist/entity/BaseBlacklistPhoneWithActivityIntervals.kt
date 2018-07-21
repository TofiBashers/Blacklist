package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 27.04.2018.
 */
abstract class BaseBlacklistPhoneWithActivityIntervals(override val number: String,
                                                       override val isCallsBlocked: Boolean,
                                                       override val isSmsBlocked: Boolean,
                                                       open val activityIntervals: List<ActivityInterval>)
    : BaseBlacklistPhone(number, isCallsBlocked, isSmsBlocked)