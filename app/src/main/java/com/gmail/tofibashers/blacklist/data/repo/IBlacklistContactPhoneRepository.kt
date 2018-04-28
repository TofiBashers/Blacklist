package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
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
}