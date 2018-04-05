package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 24.01.2018.
 */
@Singleton
class SelectOnlyEnabledActivityIntervalsUseCase
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository
) : ISelectOnlyEnabledActivityIntervalsUseCase {


    override fun build(intervalsWithEnabled: List<Pair<Boolean, ActivityInterval>>): Completable {
        return Observable.fromIterable(intervalsWithEnabled)
                .filter { it.first }
                .map { it.second }
                .toList()
                .flatMapCompletable { activityIntervalRepository.putSelectedActivityIntervals(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}