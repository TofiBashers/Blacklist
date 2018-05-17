package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryWhitelistContactItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryWhitelistContactPhone
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 10.05.2018.
 */
@Singleton
class MemoryWhitelistContactPhoneMapper
@Inject
constructor(){

    fun toWhitelistContactPhone(item: MemoryWhitelistContactPhone) : WhitelistContactPhone =
            WhitelistContactPhone(item.deviceDbId, item.number)

    fun toWhitelistContactPhoneList(items: List<MemoryWhitelistContactPhone>) : List<WhitelistContactPhone> =
            items.map { toWhitelistContactPhone(it) }
}