package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.entity.TimeChangeInitData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides settings fot time changing. Executes async.
 * Created by TofiBashers on 14.03.2018.
 */
interface ICreateTimeChangeInitDataUseCase {


    /**
     * Provides settings for time changing. Result [TimeChangeInitData.selectableTimes] always contains at
     * least one item, corresponds to midnight value. [TimeChangeInitData.isBeginTimeToChange] and
     * [TimeChangeInitData.position] not changed from source.
     * @return Single with [TimeChangeInitData]
     * Result [Single] executes in [Schedulers.COMPUTATION], provides result to android UI-thread
     */
    fun build(isBeginTimeForChange: Boolean,
              position: Int,
              intervals: MutableActivityIntervalsWithEnableAndValidState) : Single<TimeChangeInitData>
}