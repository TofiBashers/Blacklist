package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class SetEnableActivityIntervalAndValidateToSaveSyncUseCase
@Inject
constructor(): ISetEnableActivityIntervalAndValidateToSaveSyncUseCase {

    override fun build(indexOfItem: Int,
                       sourceList: MutableActivityIntervalsWithEnableAndValidState)
            : Single<MutableActivityIntervalsWithEnableAndValidState> {
        return Single.fromCallable { MutableActivityIntervalsWithEnableAndValidState(sourceList) }
                .flatMap { list: MutableActivityIntervalsWithEnableAndValidState ->
                    Single.just(list[indexOfItem])
                            .map { oldItem: Pair<Boolean, ActivityInterval> ->
                                list.set(indexOfItem, Pair(true, oldItem.second))
                                return@map list
                            }
                }
                .map { list: MutableActivityIntervalsWithEnableAndValidState ->
                    list.isValidToSave = true
                    return@map list
                }
    }
}