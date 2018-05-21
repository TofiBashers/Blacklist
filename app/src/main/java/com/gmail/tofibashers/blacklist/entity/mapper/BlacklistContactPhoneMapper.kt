package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 19.04.2018.
 */
@Singleton
class BlacklistContactPhoneMapper
@Inject
constructor(){

    fun toBlacklistContactPhoneWithIntervals(blacklistPhone: BlacklistContactPhoneNumberItem,
                                             activityIntervals: List<ActivityInterval>) : BlacklistContactPhoneWithActivityIntervals =
            BlacklistContactPhoneWithActivityIntervals(
                    blacklistPhone.dbId,
                    blacklistPhone.deviceDbId,
                    blacklistPhone.number,
                    blacklistPhone.isCallsBlocked,
                    blacklistPhone.isSmsBlocked,
                    activityIntervals
            )

    fun toBlacklistContactPhonesWithIntervals(blacklistPhones: List<BlacklistContactPhoneNumberItem>,
                                              activityIntervalsLists: List<List<ActivityInterval>>) : List<BlacklistContactPhoneWithActivityIntervals> {
        if(blacklistPhones.size != activityIntervalsLists.size){
            throw RuntimeException("Phones size not same as their intervals")
        }
        return blacklistPhones.mapIndexed { index, blacklistContactPhoneNumberItem ->
            toBlacklistContactPhoneWithIntervals(blacklistContactPhoneNumberItem, activityIntervalsLists[index])
        }
    }

    fun toDeviceContactPhone(blacklistPhone: BlacklistContactPhoneNumberItem) : DeviceContactPhoneItem =
            DeviceContactPhoneItem(blacklistPhone.deviceDbId, blacklistPhone.number)

    fun toDeviceContactPhoneList(blacklistPhones: List<BlacklistContactPhoneNumberItem>) : List<DeviceContactPhoneItem> =
            blacklistPhones.map { toDeviceContactPhone(it) }

    fun toDbBlacklistContactPhone(phone: BlacklistContactPhoneNumberItem, blacklistContactId: Long? = null) : DbBlacklistContactPhoneItem =
            DbBlacklistContactPhoneItem(phone.dbId,
                    blacklistContactId,
                    phone.deviceDbId,
                    phone.number,
                    phone.isCallsBlocked,
                    phone.isSmsBlocked)

    fun toDbBlacklistContactPhoneList(phones: List<BlacklistContactPhoneNumberItem>,
                                      blacklistContactId: Long? = null) : List<DbBlacklistContactPhoneItem> =
            phones.map { toDbBlacklistContactPhone(it, blacklistContactId) }
}