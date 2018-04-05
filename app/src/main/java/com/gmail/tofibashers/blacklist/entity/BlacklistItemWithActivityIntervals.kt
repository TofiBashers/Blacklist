package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 29.01.2018.
 */
data class BlacklistItemWithActivityIntervals(var dbId: Long? = null,
                                              var number: String,
                                              var isCallsBlocked: Boolean,
                                              var isSmsBlocked: Boolean,
                                              val activityIntervals: List<ActivityInterval>)
    : List<ActivityInterval> by activityIntervals