package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.entity.TimeChangeInitData
import com.gmail.tofibashers.blacklist.entity.TimeChangeInitDataFactory
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.03.2018.
 */
@Singleton
class CreateTimeChangeInitDataUseCase
@Inject
constructor(private val timeFormatUtils: TimeFormatUtils,
            private val timeChangeInitDataFactory: TimeChangeInitDataFactory): ICreateTimeChangeInitDataUseCase {

    override fun build(isBeginTimeForChange: Boolean,
                       position: Int,
                       intervals: MutableActivityIntervalsWithEnableAndValidState): Single<TimeChangeInitData> {
        return Single.fromCallable { intervals[position].second }
                .flatMap { interval: ActivityInterval ->
                    Single.zip(
                            Single.fromCallable {
                                if(isBeginTimeForChange) TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
                                else timeFormatUtils.getNearestTimeValueAfter(interval.beginTime)
                            },
                            Single.fromCallable {
                                if(isBeginTimeForChange) timeFormatUtils.getNearestTimeValueBefore(interval.endTime)
                                else TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
                            },
                            BiFunction { minTime: LocalTime, maxTime: LocalTime ->
                                val selectableTimesWithMidnight = timeFormatUtils.getLocalTimesBetween(minTime, maxTime)
                                        .toMutableList()
                                        .apply {
                                            add(TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME)
                                        }
                                return@BiFunction timeChangeInitDataFactory.create(position,
                                        isBeginTimeForChange,
                                        selectableTimesWithMidnight,
                                        if(isBeginTimeForChange) minTime else maxTime)
                            })
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }
}