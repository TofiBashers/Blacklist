package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithNonIgnoredNumbersFlag
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Singleton
class BlacklistContactItemWithNonIgnoredNumbersFlagMapper
@Inject
constructor(){

    fun toBlacklistContactItem(itemWithNonIgnoredNumbers: BlacklistContactItemWithNonIgnoredNumbersFlag) : BlacklistContactItem {
        return BlacklistContactItem(itemWithNonIgnoredNumbers.dbId,
                itemWithNonIgnoredNumbers.deviceDbId,
                itemWithNonIgnoredNumbers.deviceKey,
                itemWithNonIgnoredNumbers.name,
                itemWithNonIgnoredNumbers.photoUrl)
    }
}