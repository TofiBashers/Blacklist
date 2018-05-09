package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class DbBlacklistContactPhoneItemMapper
@Inject
constructor() {

    fun toBlacklistContactPhoneNumberItem(phoneItem: DbBlacklistContactPhoneItem) : BlacklistContactPhoneNumberItem =
            BlacklistContactPhoneNumberItem(phoneItem.id,
                    phoneItem.deviceDbId,
                    phoneItem.number,
                    phoneItem.ignoreCalls,
                    phoneItem.ignoreSms)

    fun toBlacklistContactPhoneNumberItemList(items: List<DbBlacklistContactPhoneItem>) : List<BlacklistContactPhoneNumberItem> =
            items.map { toBlacklistContactPhoneNumberItem(it) }

    fun toDeviceContactPhone(phoneItem: DbBlacklistContactPhoneItem) : DeviceContactPhoneItem =
            DeviceContactPhoneItem(phoneItem.deviceDbId, phoneItem.number)

    fun toDeviceContactPhoneList(phoneItems: List<DbBlacklistContactPhoneItem>) : List<DeviceContactPhoneItem> =
            phoneItems.map { toDeviceContactPhone(it) }
}