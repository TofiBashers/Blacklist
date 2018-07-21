package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class MemoryBlacklistContactItemMapper
@Inject
constructor(){

    fun toBlacklistContactItem(item: MemoryBlacklistContactItem) : BlacklistContactItem =
            BlacklistContactItem(item.dbId,
                    item.deviceDbId,
                    item.deviceKey,
                    item.name,
                    item.photoUrl)
}