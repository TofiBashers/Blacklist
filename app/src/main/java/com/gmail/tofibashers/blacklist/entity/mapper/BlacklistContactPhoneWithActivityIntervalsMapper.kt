package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.PairOfBlacklistContactPhonesAndIntervals
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
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

    fun toBlacklistContactPhone(phoneWithIntervals: BlacklistContactPhoneWithActivityIntervals) : BlacklistContactPhoneNumberItem {
        return BlacklistContactPhoneNumberItem(phoneWithIntervals.dbId,
                phoneWithIntervals.deviceDbId,
                phoneWithIntervals.number,
                phoneWithIntervals.isCallsBlocked,
                phoneWithIntervals.isSmsBlocked)
    }

    fun toBlacklistContactPhoneList(phonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>) : List<BlacklistContactPhoneNumberItem> =
            phonesWithIntervals.map { toBlacklistContactPhone(it) }

    fun toMemoryBlacklistContactPhoneWithActivityInterval(item: BlacklistContactPhoneWithActivityIntervals) : MemoryBlacklistContactPhoneWithActivityIntervals =
            MemoryBlacklistContactPhoneWithActivityIntervals(item.dbId,
                    item.deviceDbId,
                    item.number,
                    item.isCallsBlocked,
                    item.isSmsBlocked,
                    activityIntervalMapper.toMemoryActivityIntervalsList(item.activityIntervals))

    fun toMemoryBlacklistContactPhoneWithActivityIntervalList(items: List<BlacklistContactPhoneWithActivityIntervals>) : List<MemoryBlacklistContactPhoneWithActivityIntervals> =
            items.map { toMemoryBlacklistContactPhoneWithActivityInterval(it) }

    fun toPairOfBlacklistContactPhonesAndIntervals(phonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>) : PairOfBlacklistContactPhonesAndIntervals {
        val phones = toBlacklistContactPhoneList(phonesWithIntervals)
        val intervals = phonesWithIntervals.map { it.activityIntervals }
        return PairOfBlacklistContactPhonesAndIntervals(phones, intervals)
    }
}