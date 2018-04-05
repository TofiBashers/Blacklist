package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 06.03.2018.
 */
@Singleton
class BlacklistItemWithActivityIntervalsMapper
@Inject
constructor(private val activityIntervalMapper: ActivityIntervalMapper) {

    fun toDbBlacklistItemWithActivityIntervals(item: BlacklistItemWithActivityIntervals)
            : DbBlacklistItemWithActivityIntervals {
        val blacklistItem = DbBlacklistItem(item.dbId,
                item.number,
                item.isCallsBlocked,
                item.isSmsBlocked)
        val activityIntervals = activityIntervalMapper.toDbActivityIntervalsList(item.activityIntervals)
        return DbBlacklistItemWithActivityIntervals(blacklistItem, activityIntervals)
    }
}