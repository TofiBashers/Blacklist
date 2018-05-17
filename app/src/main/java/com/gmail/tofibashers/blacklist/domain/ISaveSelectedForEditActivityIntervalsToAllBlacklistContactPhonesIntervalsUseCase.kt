package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase get selected list of [ActivityInterval], and saves to multiple lists of [BlacklistContactPhoneWithActivityIntervals].
 * Created by TofiBashers on 19.04.2018.
 */
interface ISaveSelectedForEditActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase {

    /**
     * @param phonePosition position in selected list of [BlacklistContactPhoneWithActivityIntervals]
     * @return [Completable] that completes after success operation.
     * Result [Completable] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun build(phonePosition: Int) : Completable
}