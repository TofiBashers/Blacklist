package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * This UseCase saves [BlacklistPhoneNumberItem], and currently selected (or created) [ActivityInterval]'s.
 * Not performs any validation of data.
 * Created by TofiBashers on 16.01.2018.
 */
interface ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase {

    /**
     * Not performs any validation of data.
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI-thread
     */
    fun build(phoneNumberItem: BlacklistPhoneNumberItem): Completable
}
