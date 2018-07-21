package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItemWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */

@Singleton
class BlacklistPhoneNumberItemMapper
@Inject
constructor(){

    fun toBlacklistPhoneNumberItemWithActivityIntervals(phoneNumberItem: BlacklistPhoneNumberItem, intervals: List<ActivityInterval>) =
            BlacklistPhoneNumberItemWithActivityIntervals(phoneNumberItem.dbId,
                phoneNumberItem.number,
                phoneNumberItem.isCallsBlocked,
                phoneNumberItem.isSmsBlocked, intervals)

    fun toDbBlacklistPhoneNumberItem(phoneNumberItem: BlacklistPhoneNumberItem) : DbBlacklistPhoneNumberItem =
            DbBlacklistPhoneNumberItem(phoneNumberItem.dbId,
                    phoneNumberItem.number,
                    phoneNumberItem.isCallsBlocked,
                    phoneNumberItem.isSmsBlocked)

    fun toMemoryBlacklistPhoneNumberItem(phoneNumberItem: BlacklistPhoneNumberItem) : MemoryBlacklistPhoneNumberItem =
            MemoryBlacklistPhoneNumberItem(phoneNumberItem.dbId,
                    phoneNumberItem.number,
                    phoneNumberItem.isCallsBlocked,
                    phoneNumberItem.isSmsBlocked)
}