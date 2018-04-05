package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase select enabled [ActivityInterval]'s.
 * Created by TofiBashers on 24.01.2018.
 */
interface ISelectOnlyEnabledActivityIntervalsUseCase {

    /**
     * Result [Single] executes in [Schedulers.IO], provides result in Android UI thread
     */
    fun build(intervalsWithEnabled: List<Pair<Boolean, ActivityInterval>>) : Completable
}