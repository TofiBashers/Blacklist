package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase get selected [ActivityInterval]'s or create default, not perform any validation.
 * Created by TofiBashers on 16.01.2018.
 */
interface IGetSelectedOrNewActivityIntervalsUseCase {

    /**
     * This method not perform any validation,
     * and [MutableActivityIntervalsWithEnableAndValidState.isValidToSave] always sets to true.
     * Result [Single] executes in [Schedulers.IO], provides result to Android UI thread.
     */
    fun build(): Single<MutableActivityIntervalsWithEnableAndValidState>
}