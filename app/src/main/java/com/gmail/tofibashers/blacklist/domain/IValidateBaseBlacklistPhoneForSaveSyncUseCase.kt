package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BaseBlacklistPhone
import io.reactivex.Single


/**
 * Base validation for blacklist phone, by [BaseBlacklistPhone.isCallsBlocked] and [BaseBlacklistPhone.isSmsBlocked] params.
 * Created by TofiBashers on 10.05.2018.
 */
interface IValidateBaseBlacklistPhoneForSaveSyncUseCase {

    fun build(phone: BaseBlacklistPhone) : Single<Boolean>
}