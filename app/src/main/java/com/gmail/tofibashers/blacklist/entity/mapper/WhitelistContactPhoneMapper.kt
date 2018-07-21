package com.gmail.tofibashers.blacklist.entity.mapper

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
class WhitelistContactPhoneMapper
@Inject
constructor(){

    fun toMemoryWhitelistContactPhone(item: WhitelistContactPhone) : MemoryWhitelistContactPhone =
            MemoryWhitelistContactPhone(item.deviceDbId, item.number)

    fun toMemoryWhitelistContactPhones(items: List<WhitelistContactPhone>) : List<MemoryWhitelistContactPhone> =
            items.map { toMemoryWhitelistContactPhone(it) }
}