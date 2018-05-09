package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithPhonesAndIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class BlacklistContactItemWithPhonesAndIntervalsMapper
@Inject
constructor(private val blacklistPhoneItemWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper){

    fun toDbBlacklistContactItemWithPhonesAndIntervals(item: BlacklistContactItemWithPhonesAndIntervals) : DbBlacklistContactItemWithPhonesAndIntervals {
        return DbBlacklistContactItemWithPhonesAndIntervals(
                DbBlacklistContactItem( item.dbId,
                        item.deviceDbId,
                        item.deviceKey,
                        item.name,
                        item.photoUrl),
                blacklistPhoneItemWithActivityIntervalsMapper.toDbBlacklistContactPhonesWithActivityIntervals(item.dbId, item.phones))
    }

    fun toDbBlacklistContactItemWithPhonesAndIntervalsList(items: List<BlacklistContactItemWithPhonesAndIntervals>)
            : List<DbBlacklistContactItemWithPhonesAndIntervals>
            = items.map { toDbBlacklistContactItemWithPhonesAndIntervals(it) }
}