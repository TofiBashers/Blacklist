package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 17.04.2018.
 */
@Singleton
class ValidateBlacklistContactPhoneNumbersForSaveSyncUseCase
@Inject
constructor(private val validateBlacklistContactPhoneNumberForSaveSyncUseCase: ValidateBaseBlacklistPhoneForSaveSyncUseCase)
    : IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase {

    override fun build(phoneNumbers: List<BlacklistContactPhoneNumberItem>): Single<Boolean> {
        return Maybe.fromCallable {
            phoneNumbers.find {
                validateBlacklistContactPhoneNumberForSaveSyncUseCase.build(it)
                        .blockingGet()
            }
        }
                .map { true }
                .toSingle(false)
    }
}