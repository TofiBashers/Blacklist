package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase saves selected one list of [ActivityInterval], and saves to multiple lists of [ActivityInterval].
 * Created by TofiBashers on 19.04.2018.
 */
interface ISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase {

    /**
     * @param intervalPosition position in selected list of [ActivityInterval]
     * @return [Completable] that completes after success operation.
     * Result [Completable] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun build(intervalPosition: Int) : Completable
}