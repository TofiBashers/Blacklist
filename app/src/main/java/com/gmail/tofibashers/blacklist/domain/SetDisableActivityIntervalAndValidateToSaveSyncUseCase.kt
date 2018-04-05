package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */

@Singleton
class SetDisableActivityIntervalAndValidateToSaveSyncUseCase
@Inject
constructor(): ISetDisableActivityIntervalAndValidateToSaveSyncUseCase {

    override fun build(indexOfItem: Int,
                       sourceList: MutableActivityIntervalsWithEnableAndValidState) : Single<MutableActivityIntervalsWithEnableAndValidState> {
        return Single.fromCallable { MutableActivityIntervalsWithEnableAndValidState(sourceList) }
                .flatMap { list: MutableActivityIntervalsWithEnableAndValidState ->
                    replaceStateToDisabledAndSetDefaultInterval(list, indexOfItem)
                }
                .flatMap { list: MutableActivityIntervalsWithEnableAndValidState ->
                    resetValidState(list)
                }
    }

    fun replaceStateToDisabledAndSetDefaultInterval(list: MutableActivityIntervalsWithEnableAndValidState,
                                                    indexOfItem: Int) : Single<MutableActivityIntervalsWithEnableAndValidState> {
        return Single.just(list[indexOfItem])
                .map { oldItem: Pair<Boolean, ActivityInterval> ->
                    val interval = oldItem.second
                    interval.beginTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
                    interval.endTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
                    list.set(indexOfItem, Pair(false, interval))
                    return@map list
                }
    }

    fun resetValidState(list: MutableActivityIntervalsWithEnableAndValidState)
            : Single<MutableActivityIntervalsWithEnableAndValidState>{
        return Maybe.fromCallable { list.find { pair: Pair<Boolean, ActivityInterval> -> pair.first } }
                .map { true }
                .toSingle(false)
                .map { isValid: Boolean ->
                    list.isValidToSave = isValid
                    return@map list
                }
    }
}