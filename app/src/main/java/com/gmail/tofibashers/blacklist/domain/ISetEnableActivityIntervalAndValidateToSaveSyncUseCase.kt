package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import io.reactivex.Single


/**
 * This UseCase mark as enabled result interval.
 * Created by TofiBashers on 02.02.2018.
 */
interface ISetEnableActivityIntervalAndValidateToSaveSyncUseCase {

    /**
     * Result [Single] doesn't specify any schedulers.
     * @return new [MutableActivityIntervalsWithEnableAndValidState] with updated enabled and valid modeWithState.
     * Original parameter [sourceList] not modificated.
     */
    fun build(indexOfItem: Int,
              sourceList: MutableActivityIntervalsWithEnableAndValidState) : Single<MutableActivityIntervalsWithEnableAndValidState>
}