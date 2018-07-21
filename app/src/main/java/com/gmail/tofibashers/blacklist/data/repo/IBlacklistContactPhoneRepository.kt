package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import io.reactivex.Completable
import io.reactivex.Single


/**
 * Created by TofiBashers on 14.04.2018.
 */
interface IBlacklistContactPhoneRepository {

    /**
     * @return list of [WhitelistContactPhone], associated with [blacklistContactItem], without any sorting.
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllAssociatedWithBlacklistContact(blacklistContactItem: BlacklistContactItem) : Single<List<BlacklistContactPhoneNumberItem>>

    /**
     * Removes phones with all associated and unused entities, if exists.
     * @return [Completable] when removing completes, regardless of count of deleted entities.
     * Result [Completable] doesn't modify any schedulers.
     */
    fun removeAssociatedWithBlacklistContact(vararg items: BlacklistContactPhoneNumberItem,
                                             blacklistContact: BlacklistContactItem) : Completable
}