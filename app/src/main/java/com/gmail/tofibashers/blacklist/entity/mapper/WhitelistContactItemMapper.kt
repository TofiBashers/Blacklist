package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryWhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.04.2018.
 */
@Singleton
class WhitelistContactItemMapper
@Inject
constructor(){

    fun toWhitelistContactItemWithHashPhones(item: WhitelistContactItem, hasPhones: Boolean) : WhitelistContactItemWithHasPhones {
        return WhitelistContactItemWithHasPhones(item.deviceDbId,
                item.deviceKey,
                item.name,
                item.photoUrl,
                hasPhones)
    }

    fun toMemoryWhitelistContactItem(item: WhitelistContactItem) : MemoryWhitelistContactItem {
        return MemoryWhitelistContactItem(item.deviceDbId,
                item.deviceKey,
                item.name,
                item.photoUrl)
    }

    fun toBlacklistContact(whitelistContactItem: WhitelistContactItem, dbId: Long? = null) : BlacklistContactItem =
            BlacklistContactItem(dbId,
                    whitelistContactItem.deviceDbId,
                    whitelistContactItem.deviceKey,
                    whitelistContactItem.name,
                    whitelistContactItem.photoUrl)
}