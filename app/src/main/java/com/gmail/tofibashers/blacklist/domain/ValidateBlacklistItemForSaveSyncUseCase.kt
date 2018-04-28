package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */
@Singleton
class ValidateBlacklistItemForSaveSyncUseCase
@Inject
constructor() : IValidateBlacklistItemForSaveSyncUseCase {
    override fun build(phoneNumberItem: BlacklistPhoneNumberItem): Single<Boolean> {
        return Single.fromCallable { !phoneNumberItem.number.isBlank() && (phoneNumberItem.isSmsBlocked || phoneNumberItem.isCallsBlocked) }
    }
}