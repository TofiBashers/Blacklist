package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BaseBlacklistPhone
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 10.05.2018.
 */
@Singleton
class ValidateBaseBlacklistPhoneForSaveSyncUseCase
@Inject
constructor() : IValidateBaseBlacklistPhoneForSaveSyncUseCase {

    override fun build(phone: BaseBlacklistPhone) : Single<Boolean> =
            Single.fromCallable { phone.isCallsBlocked || phone.isSmsBlocked }
}