package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import io.reactivex.Single
import java.util.*


/**
 * This UseCase mark interval as disabled, and set default time to element, depends on [Locale].
 * Also, validates result interval.
 * Created by TofiBashers on 24.01.2018.
 */
interface ISetDisableActivityIntervalAndValidateToSaveSyncUseCase {

    /**
     * Result [Single] doesn't specify any schedulers.
     * @return new [MutableActivityIntervalsWithEnableAndValidState] with updates.
     * Original parameter [sourceList] is not modificated.
     */
    fun build(indexOfItem: Int,
              sourceList: MutableActivityIntervalsWithEnableAndValidState) : Single<MutableActivityIntervalsWithEnableAndValidState>
}