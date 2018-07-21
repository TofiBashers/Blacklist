package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 29.01.2018.
 */
data class BlacklistPhoneNumberItemWithActivityIntervals(var dbId: Long? = null,
                                                         override var number: String,
                                                         override var isCallsBlocked: Boolean,
                                                         override var isSmsBlocked: Boolean,
                                                         override val activityIntervals: List<ActivityInterval>)
    : BaseBlacklistPhoneWithActivityIntervals(number,
        isCallsBlocked,
        isSmsBlocked,
        activityIntervals), List<ActivityInterval> by activityIntervals {
}