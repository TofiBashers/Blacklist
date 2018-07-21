package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 06.03.2018.
 */
@Singleton
class MemoryBlacklistPhoneNumberItemMapper
@Inject
constructor(){

    fun toBlacklistPhoneNumberItem(item: MemoryBlacklistPhoneNumberItem) : BlacklistPhoneNumberItem =
            BlacklistPhoneNumberItem(item.dbId,
                    item.number,
                    item.isCallsBlocked,
                    item.isSmsBlocked)
}