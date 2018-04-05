package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbBlacklistItemWithActivityIntervalsMapper
@Inject
constructor(private val dbActivityIntervalMapper: DbActivityIntervalMapper){

    fun toBlacklistItemWithActivityIntervals(item: DbBlacklistItemWithActivityIntervals)
            : BlacklistItemWithActivityIntervals {
        val dbBlacklistItem = item.dbBlacklistItem
        val activityIntervals = dbActivityIntervalMapper.toActivityIntervalsList(item.dbActivityIntervals)
        return BlacklistItemWithActivityIntervals(dbBlacklistItem.id,
                dbBlacklistItem.number,
                dbBlacklistItem.ignoreCalls,
                dbBlacklistItem.ignoreSms,
                activityIntervals)
    }

    fun toBlacklistItemWithActivityIntervalsList(intervals: List<DbBlacklistItemWithActivityIntervals>)
            : List<BlacklistItemWithActivityIntervals> =
            intervals.map { toBlacklistItemWithActivityIntervals(it) }
}