package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 10.05.2018.
 */
@Singleton
class MemoryBlacklistContactPhoneWithActivityIntervalsMapper
@Inject
constructor(private val memoryActivityIntervalMapper: MemoryActivityIntervalMapper){

    fun toBlacklistContactPhoneWithActivityInterval(item: MemoryBlacklistContactPhoneWithActivityIntervals) : BlacklistContactPhoneWithActivityIntervals {
        return BlacklistContactPhoneWithActivityIntervals(item.dbId,
                item.deviceDbId,
                item.number,
                item.isCallsBlocked,
                item.isSmsBlocked,
                memoryActivityIntervalMapper.toActivityIntervalsList(item.activityIntervals))
    }

    fun toBlacklistContactPhoneWithActivityIntervalsList(items: List<MemoryBlacklistContactPhoneWithActivityIntervals>) : List<BlacklistContactPhoneWithActivityIntervals> =
            items.map { toBlacklistContactPhoneWithActivityInterval(it) }
}