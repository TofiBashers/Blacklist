package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 27.04.2018.
 */
abstract class BaseBlacklistElementWithActivityIntervals(open var number: String,
                                                         open var isCallsBlocked: Boolean,
                                                         open var isSmsBlocked: Boolean,
                                                         open val activityIntervals: List<ActivityInterval>)