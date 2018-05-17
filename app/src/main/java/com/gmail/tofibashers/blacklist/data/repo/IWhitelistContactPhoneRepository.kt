package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 14.04.2018.
 */
interface IWhitelistContactPhoneRepository : IBaseListSelectionOperationRepository<WhitelistContactPhone>{

    /**
     * @return list of [WhitelistContactPhone], associated with [whitelistContactItem], sorted by number.
     *
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllAssociatedWithContactSortedByNumberAsc(whitelistContactItem: WhitelistContactItem) : Single<List<WhitelistContactPhone>>

    /**
     * @return list of [WhitelistContactPhone], associated with [whitelistContactItem], without any sorting.
     *
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllAssociatedWithContact(whitelistContactItem: WhitelistContactItem) : Single<List<WhitelistContactPhone>>

    /**
     * @return count of [WhitelistContactPhone], associated with [whitelistContactItem].
     *
     * Result [Single] doesn't modify any schedulers.
     */
    fun getCountOfAssociatedWithContact(whitelistContactItem: WhitelistContactItem) : Single<Int>
}