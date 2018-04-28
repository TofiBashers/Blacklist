package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 17.04.2018.
 */
@Singleton
class ValidateBlacklistContactPhoneNumbersForSaveSyncUseCase
@Inject
constructor(): IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase {

    override fun build(phoneNumbers: List<BlacklistContactPhoneNumberItem>): Single<Boolean> {
        return Maybe.fromCallable {
            phoneNumbers.find {
                item: BlacklistContactPhoneNumberItem -> item.isCallsBlocked || item.isSmsBlocked
            }
        }
                .map { true }
                .toSingle(false)
    }
}