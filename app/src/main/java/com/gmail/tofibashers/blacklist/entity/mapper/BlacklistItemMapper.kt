package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */

@Singleton
class BlacklistItemMapper
@Inject
constructor(){

    fun toBlacklistItemWithActivityIntervals(item: BlacklistItem, intervals: List<ActivityInterval>) =
            BlacklistItemWithActivityIntervals(item.dbId,
                item.number,
                item.isCallsBlocked,
                item.isSmsBlocked, intervals)

    fun toDbBlacklistItem(item: BlacklistItem) : DbBlacklistItem =
            DbBlacklistItem(item.dbId,
                    item.number,
                    item.isCallsBlocked,
                    item.isSmsBlocked)

    fun toMemoryBlacklistItem(item: BlacklistItem) : MemoryBlacklistItem =
            MemoryBlacklistItem(item.dbId,
                    item.number,
                    item.isCallsBlocked,
                    item.isSmsBlocked)
}