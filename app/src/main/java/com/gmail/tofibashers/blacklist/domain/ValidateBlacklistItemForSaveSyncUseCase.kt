package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistItem
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
    override fun build(item: BlacklistItem): Single<Boolean> {
        return Single.fromCallable { !item.number.isBlank() && (item.isSmsBlocked || item.isCallsBlocked) }
    }
}