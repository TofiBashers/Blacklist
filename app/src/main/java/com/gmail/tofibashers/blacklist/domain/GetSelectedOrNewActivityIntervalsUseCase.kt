package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 24.01.2018.
 */
@Singleton
class GetSelectedOrNewActivityIntervalsUseCase
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val activityIntervalFactory: ActivityIntervalFactory,
        private val intervalsWithStateFactory: MutableActivityIntervalsWithEnableAndValidStateFactory,
        private val timeFormatUtils: TimeFormatUtils
) : IGetSelectedOrNewActivityIntervalsUseCase {

    override fun build(): Single<MutableActivityIntervalsWithEnableAndValidState> {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("Mode not selected before providing intervals")))
                .flatMap {mode: InteractionMode ->
                    activityIntervalRepository.getSelectedActivityIntervals()
                            .flatMap { intervals: List<ActivityInterval> ->
                                addDisabledIntervalsAndGroup(intervals)
                                        .toMaybe()
                            }
                            .switchIfEmpty(Single.defer {
                                return@defer if (mode == InteractionMode.CREATE) createNewEnabledIntervals()
                                else Single.error(RuntimeException("Intervals not selected in Edit mode"))
                            })
                }
                .map { pairsList: List<Pair<Boolean, ActivityInterval>> ->
                    intervalsWithStateFactory.create(true, pairsList.toMutableList())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun addDisabledIntervalsAndGroup(intervals: List<ActivityInterval>)
            : Single<List<Pair<Boolean, ActivityInterval>>> {
        return Single.fromCallable { timeFormatUtils.getWeekdayIdsInLocalizedOrder() }
                .flatMapObservable {days: List<Int> ->  Observable.fromIterable(days) }
                .concatMap { dayId: Int ->
                    val intervalForDay = intervals.find {
                        interval: ActivityInterval -> interval.weekDayId == dayId
                    }
                    if(intervalForDay == null) {
                        return@concatMap Observable.fromCallable {
                            activityIntervalFactory.create(dayId)
                        }.map { createdInterval: ActivityInterval -> Pair(false, createdInterval) }
                    }
                    else {
                        return@concatMap Observable.just(Pair(true, intervalForDay))
                    }
                }
                .toList()
    }

    private fun createNewEnabledIntervals(): Single<List<Pair<Boolean, ActivityInterval>>> {
        return Single.fromCallable { timeFormatUtils.getWeekdayIdsInLocalizedOrder() }
                .flatMapObservable {days: List<Int> ->  Observable.fromIterable(days) }
                .map { activityIntervalFactory.create(it) }
                .map { Pair(true, it)}
                .toList()
    }
}