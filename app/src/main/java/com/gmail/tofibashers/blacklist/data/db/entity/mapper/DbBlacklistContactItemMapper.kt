package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class DbBlacklistContactItemMapper
@Inject
constructor(){

    fun toDeviceContactItem(item: DbBlacklistContactItem) : DeviceContactItem =
            DeviceContactItem(item.deviceDbId,
                    item.deviceLookupKey,
                    item.name,
                    item.photoUrl)

    fun toDeviceContactItemsList(items: List<DbBlacklistContactItem>) : List<DeviceContactItem> =
            items.map { toDeviceContactItem(it) }

    fun toBlacklistContactItem(item: DbBlacklistContactItem) : BlacklistContactItem =
            BlacklistContactItem(item.id,
                    item.deviceDbId,
                    item.deviceLookupKey,
                    item.name,
                    item.photoUrl)

    fun toBlacklistContactItemsList(items: List<DbBlacklistContactItem>) : List<BlacklistContactItem> =
            items.map { toBlacklistContactItem(it) }
}