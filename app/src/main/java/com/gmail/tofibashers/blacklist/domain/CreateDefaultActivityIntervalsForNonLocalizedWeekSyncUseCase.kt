package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityIntervalFactory
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 19.04.2018.
 */
@Singleton
class CreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase
@Inject
constructor(
        private val activityIntervalFactory: ActivityIntervalFactory,
        private val timeFormatUtils: TimeFormatUtils
) : ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase {

    override fun build() : Single<List<ActivityInterval>> {
        return Observable.fromIterable(timeFormatUtils.getWeekdayIdsInNonLocalizedOrder())
                .map { activityIntervalFactory.create(it) }
                .toList()
    }
}