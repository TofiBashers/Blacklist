package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithNonIgnoredNumbersFlag
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * This UseCase removes [BlacklistContactItemWithNonIgnoredNumbersFlag] with all associated and unused entities
 * Created by TofiBashers on 10.04.2018.
 */
interface IDeleteBlacklistContactItemUseCase {

    /**
     * Result [Completable] executes at [Schedulers.IO], provides result to Android UI thread
     */
    fun build(itemWithNumbersFlag: BlacklistContactItemWithNonIgnoredNumbersFlag) : Completable
}
