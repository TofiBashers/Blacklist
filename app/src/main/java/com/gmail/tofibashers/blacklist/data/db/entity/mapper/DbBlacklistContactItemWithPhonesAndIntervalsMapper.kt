package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class DbBlacklistContactItemWithPhonesAndIntervalsMapper
@Inject
constructor(private val phoneWithActivityIntervalsMapper: DbBlacklistContactPhoneWithActivityIntervalsMapper){

    fun toBlacklistContactItemWithPhonesAndIntervals(item: DbBlacklistContactItemWithPhonesAndIntervals) : BlacklistContactItemWithPhonesAndIntervals {
        return BlacklistContactItemWithPhonesAndIntervals(item.contactItem.id,
                item.contactItem.deviceDbId,
                item.contactItem.deviceLookupKey,
                item.contactItem.name,
                item.contactItem.photoUrl,
                phoneWithActivityIntervalsMapper.toBlacklistContactPhoneWithActivityIntervalsList(item.phonesWithIntervals))
    }

    fun toBlacklistContactItemWithPhonesAndIntervalsList(items: List<DbBlacklistContactItemWithPhonesAndIntervals>)
            : List<BlacklistContactItemWithPhonesAndIntervals> = items.map { toBlacklistContactItemWithPhonesAndIntervals(it)
    }
}