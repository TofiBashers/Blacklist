package com.gmail.tofibashers.blacklist.data.device.mapper

import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.05.2018.
 */
@Singleton
class DeviceContactPhoneItemMapper
@Inject
constructor(){

    fun toWhitelistContactPhone(item: DeviceContactPhoneItem) : WhitelistContactPhone =
            WhitelistContactPhone(item.id, item.number)

    fun toWhitelistContactPhoneList(items: List<DeviceContactPhoneItem>) : List<WhitelistContactPhone> =
            items.map { toWhitelistContactPhone(it) }
}