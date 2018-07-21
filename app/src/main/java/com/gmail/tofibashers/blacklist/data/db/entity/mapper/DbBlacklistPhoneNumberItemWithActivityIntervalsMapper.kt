package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbBlacklistPhoneNumberItemWithActivityIntervalsMapper
@Inject
constructor(private val dbActivityIntervalMapper: DbActivityIntervalMapper){

    fun toBlacklistPhoneNumberItemWithActivityIntervals(item: DbBlacklistPhoneNumberItemWithActivityIntervals)
            : BlacklistPhoneNumberItemWithActivityIntervals {
        val dbBlacklistItem = item.dbBlacklistPhoneNumberItem
        val activityIntervals = dbActivityIntervalMapper.toActivityIntervalsList(item.dbActivityIntervals)
        return BlacklistPhoneNumberItemWithActivityIntervals(dbBlacklistItem.id,
                dbBlacklistItem.number,
                dbBlacklistItem.ignoreCalls,
                dbBlacklistItem.ignoreSms,
                activityIntervals)
    }

    fun toBlacklistPhoneNumberItemWithActivityIntervalsList(intervals: List<DbBlacklistPhoneNumberItemWithActivityIntervals>)
            : List<BlacklistPhoneNumberItemWithActivityIntervals> =
            intervals.map { toBlacklistPhoneNumberItemWithActivityIntervals(it) }
}