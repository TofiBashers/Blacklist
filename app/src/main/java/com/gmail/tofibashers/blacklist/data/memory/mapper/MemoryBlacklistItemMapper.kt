package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 06.03.2018.
 */
@Singleton
class MemoryBlacklistItemMapper
@Inject
constructor(){

    fun toBlacklistItem(item: MemoryBlacklistItem) : BlacklistPhoneNumberItem =
            BlacklistPhoneNumberItem(item.dbId,
                    item.number,
                    item.isCallsBlocked,
                    item.isSmsBlocked)
}