package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class BlacklistContactPhoneRepository
@Inject
constructor() : IBlacklistContactPhoneRepository{

    //TODO: not implemented
    override fun getAllAssociatedWithBlacklistContact(blacklistContactItem: BlacklistContactItem): Single<List<BlacklistContactPhoneNumberItem>> =
            Single.just(emptyList())
}