package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 03.05.2018.
 */
@Singleton
class BlacklistContactPhoneWithActivityIntervalsMapper
@Inject
constructor(private val activityIntervalMapper: ActivityIntervalMapper){

    fun toDbBlacklistContactPhoneWithActivityIntervals(contactId: Long? = null,
                                                       item: BlacklistContactPhoneWithActivityIntervals) : DbBlacklistContactPhoneWithActivityIntervals {
        return DbBlacklistContactPhoneWithActivityIntervals(
                DbBlacklistContactPhoneItem(item.dbId,
                        contactId,
                        item.deviceDbId,
                        item.number,
                        item.isCallsBlocked,
                        item.isSmsBlocked),
                activityIntervalMapper.toDbActivityIntervalsList(item.activityIntervals))
    }

    fun toDbBlacklistContactPhonesWithActivityIntervals(contactId: Long? = null,
                                                        phones: List<BlacklistContactPhoneWithActivityIntervals>) : List<DbBlacklistContactPhoneWithActivityIntervals> {
        return phones.map { toDbBlacklistContactPhoneWithActivityIntervals(contactId, it) }
    }
}