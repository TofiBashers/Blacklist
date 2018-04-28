package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 19.04.2018.
 */
@Singleton
class BlacklistContactItemMapper
@Inject
constructor(){

    /**
     * Map item with non null [BlacklistContactItem.deviceDbId] and [BlacklistContactItem.deviceKey]
     * @return mapped [WhitelistContactItem]
     * @throws [NullPointerException] if [BlacklistContactItem.deviceDbId] or [BlacklistContactItem.deviceKey] is null
     */
    fun toWhitelistContactItem(blacklistContact: BlacklistContactItem) : WhitelistContactItem =
            WhitelistContactItem(
                    blacklistContact.deviceDbId!!,
                    blacklistContact.deviceKey!!,
                    blacklistContact.name,
                    blacklistContact.photoUrl)

    fun toBlacklistContactItemWithNonIgnoredFlag(blacklistContact: BlacklistContactItem,
                                                 withNonIgnoredNumbers: Boolean) : BlacklistContactItemWithNonIgnoredNumbersFlag =
            BlacklistContactItemWithNonIgnoredNumbersFlag(
                    blacklistContact.dbId,
                    blacklistContact.deviceDbId,
                    blacklistContact.deviceKey,
                    blacklistContact.name,
                    blacklistContact.photoUrl,
                    withNonIgnoredNumbers)

    fun toBlacklistContactItemWithPhonesAndIntervals(blacklistContact: BlacklistContactItem,
                                                     blacklistPhones: List<BlacklistContactPhoneNumberItem>,
                                                     activityIntervalsLists: List<List<ActivityInterval>>,
                                                     blacklistContactPhoneMapper: BlacklistContactPhoneMapper) : BlacklistContactItemWithPhonesAndIntervals {
        val phonesWithIntervals = blacklistContactPhoneMapper.toBlacklistContactPhonesWithIntervals(blacklistPhones, activityIntervalsLists)
        return BlacklistContactItemWithPhonesAndIntervals(
                blacklistContact.dbId,
                blacklistContact.deviceDbId,
                blacklistContact.deviceKey,
                blacklistContact.name,
                blacklistContact.photoUrl,
                phonesWithIntervals)
    }

    fun toMemoryBlacklistContactItem(blacklistContact: BlacklistContactItem) : MemoryBlacklistContactItem =
            MemoryBlacklistContactItem(
                    blacklistContact.dbId,
                    blacklistContact.deviceDbId,
                    blacklistContact.deviceKey,
                    blacklistContact.name,
                    blacklistContact.photoUrl)
}