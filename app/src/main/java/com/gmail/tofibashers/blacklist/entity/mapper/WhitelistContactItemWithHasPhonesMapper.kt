package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.04.2018.
 */
@Singleton
class WhitelistContactItemWithHasPhonesMapper
@Inject
constructor(){

    fun toWhitelistContactItem(item: WhitelistContactItemWithHasPhones) : WhitelistContactItem {
        return WhitelistContactItem(item.deviceDbId,
                item.deviceKey,
                item.name,
                item.photoUrl)
    }
}