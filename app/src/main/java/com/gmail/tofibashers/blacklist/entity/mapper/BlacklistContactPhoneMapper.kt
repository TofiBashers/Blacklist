package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.entity.*
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

}