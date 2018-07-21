package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 06.03.2018.
 */
@Singleton
class BlacklistPhoneNumberItemWithActivityIntervalsMapper
@Inject
constructor(private val activityIntervalMapper: ActivityIntervalMapper) {

    fun toDbBlacklistPhoneNumberItemWithActivityIntervals(item: BlacklistPhoneNumberItemWithActivityIntervals)
            : DbBlacklistPhoneNumberItemWithActivityIntervals {
        val blacklistItem = DbBlacklistPhoneNumberItem(item.dbId,
                item.number,
                item.isCallsBlocked,
                item.isSmsBlocked)
        val activityIntervals = activityIntervalMapper.toDbActivityIntervalsList(item.activityIntervals)
        return DbBlacklistPhoneNumberItemWithActivityIntervals(blacklistItem, activityIntervals)
    }
}