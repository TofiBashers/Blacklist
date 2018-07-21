package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 10.05.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryBlacklistContactPhoneWithActivityIntervals(var dbId: Long? = null,
                                                            var deviceDbId: Long? = null,
                                                            var number: String,
                                                            var isCallsBlocked: Boolean,
                                                            var isSmsBlocked: Boolean,
                                                            var activityIntervals: List<MemoryActivityInterval>)