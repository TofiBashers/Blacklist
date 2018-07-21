package com.gmail.tofibashers.blacklist.data.device.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class DeviceContactItemMapper
@Inject
constructor(){

    fun toDbBlacklistContactItem(item: DeviceContactItem, id: Long? = null): DbBlacklistContactItem =
            DbBlacklistContactItem(id, item.id, item.lookupKey, item.name, item.photoUrl)

    fun toWhitelistContact(item: DeviceContactItem): WhitelistContactItem =
            WhitelistContactItem(item.id, item.lookupKey, item.name, item.photoUrl)

    fun toWhitelistContactList(items: List<DeviceContactItem>): List<WhitelistContactItem> =
            items.map { toWhitelistContact(it) }
}