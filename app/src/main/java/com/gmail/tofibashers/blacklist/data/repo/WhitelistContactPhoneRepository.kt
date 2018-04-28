package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class WhitelistContactPhoneRepository
@Inject
constructor() : IWhitelistContactPhoneRepository {

    //TODO: not implemented
    override fun getAllAssociatedWithContactSortedByNumberAsc(whitelistContactItem: WhitelistContactItem): Single<List<WhitelistContactPhone>> =
        Single.just(emptyList())

    //TODO: not implemented
    override fun getAllAssociatedWithWhitelistContact(whitelistContactItem: WhitelistContactItem): Single<List<WhitelistContactPhone>> =
            Single.just(emptyList())

    //TODO: not implemented
    override fun getCountOfAssociatedWithContact(whitelistContactItem: WhitelistContactItem): Single<Int> = Single.just(0)
}