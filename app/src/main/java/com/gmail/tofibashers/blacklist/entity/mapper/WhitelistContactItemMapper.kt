package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
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

    /**
     * throws [NullPointerException] if any of [WhitelistContactItem.deviceDbId], [WhitelistContactItem.deviceKey] and [WhitelistContactItem.photoUrl] is null.
     */
    fun toWhitelistContactItemWithHashPhones(item: WhitelistContactItem, hasPhones: Boolean) : WhitelistContactItemWithHasPhones {
        return WhitelistContactItemWithHasPhones(item.deviceDbId!!,
                item.deviceKey!!,
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

    fun toDbBlacklistContact(whitelistContactItem: WhitelistContactItem, dbId: Long? = null) : DbBlacklistContactItem =
            DbBlacklistContactItem(dbId,
                    whitelistContactItem.deviceDbId,
                    whitelistContactItem.deviceKey,
                    whitelistContactItem.name,
                    whitelistContactItem.photoUrl)

    fun toDeviceContactItem(whitelistContactItem: WhitelistContactItem) : DeviceContactItem =
            DeviceContactItem(whitelistContactItem.deviceDbId,
                    whitelistContactItem.deviceKey,
                    whitelistContactItem.name,
                    whitelistContactItem.photoUrl)
}