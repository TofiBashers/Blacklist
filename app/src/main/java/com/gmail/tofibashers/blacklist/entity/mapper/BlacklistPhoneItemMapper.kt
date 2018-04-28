package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */

@Singleton
class BlacklistPhoneItemMapper
@Inject
constructor(){

    fun toBlacklistItemWithActivityIntervals(phoneNumberItem: BlacklistPhoneNumberItem, intervals: List<ActivityInterval>) =
            BlacklistItemWithActivityIntervals(phoneNumberItem.dbId,
                phoneNumberItem.number,
                phoneNumberItem.isCallsBlocked,
                phoneNumberItem.isSmsBlocked, intervals)

    fun toDbBlacklistItem(phoneNumberItem: BlacklistPhoneNumberItem) : DbBlacklistItem =
            DbBlacklistItem(phoneNumberItem.dbId,
                    phoneNumberItem.number,
                    phoneNumberItem.isCallsBlocked,
                    phoneNumberItem.isSmsBlocked)

    fun toMemoryBlacklistItem(phoneNumberItem: BlacklistPhoneNumberItem) : MemoryBlacklistItem =
            MemoryBlacklistItem(phoneNumberItem.dbId,
                    phoneNumberItem.number,
                    phoneNumberItem.isCallsBlocked,
                    phoneNumberItem.isSmsBlocked)
}