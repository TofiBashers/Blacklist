package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryWhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 15.04.2018.
 */
@Singleton
class MemoryWhitelistContactItemMapper
@Inject
constructor(){

    fun toWhitelistContactItem(item: MemoryWhitelistContactItem) : WhitelistContactItem =
            WhitelistContactItem(item.deviceDbId,
                    item.deviceKey,
                    item.name,
                    item.photoUrl)
}