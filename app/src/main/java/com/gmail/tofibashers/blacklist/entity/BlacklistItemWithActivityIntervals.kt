package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 29.01.2018.
 */
data class BlacklistItemWithActivityIntervals(var dbId: Long? = null,
                                              override var number: String,
                                              override var isCallsBlocked: Boolean,
                                              override var isSmsBlocked: Boolean,
                                              override val activityIntervals: List<ActivityInterval>)
    : BaseBlacklistElementWithActivityIntervals(number,
        isCallsBlocked,
        isSmsBlocked,
        activityIntervals), List<ActivityInterval> by activityIntervals