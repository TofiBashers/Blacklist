package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.05.2018.
 */
@Singleton
class DbBlacklistContactPhoneWithActivityIntervalsMapper
@Inject
constructor(private val dbActivityIntervalMapper: DbActivityIntervalMapper){

    fun toBlacklistContactPhoneWithActivityIntervals(item: DbBlacklistContactPhoneWithActivityIntervals) : BlacklistContactPhoneWithActivityIntervals =
        BlacklistContactPhoneWithActivityIntervals(item.dbBlacklistContactPhone.id,
                item.dbBlacklistContactPhone.deviceDbId,
                item.dbBlacklistContactPhone.number,
                item.dbBlacklistContactPhone.ignoreCalls,
                item.dbBlacklistContactPhone.ignoreSms,
                dbActivityIntervalMapper.toActivityIntervalsList(item.activityIntervals))

    fun toBlacklistContactPhoneWithActivityIntervalsList(items: List<DbBlacklistContactPhoneWithActivityIntervals>) : List<BlacklistContactPhoneWithActivityIntervals>
            = items.map { toBlacklistContactPhoneWithActivityIntervals(it) }
}