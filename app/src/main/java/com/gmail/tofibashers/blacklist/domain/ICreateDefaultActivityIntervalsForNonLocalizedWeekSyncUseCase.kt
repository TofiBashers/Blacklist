package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by TofiBashers on 19.04.2018.
 */
interface ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase {

    fun build() : Single<List<ActivityInterval>>
}