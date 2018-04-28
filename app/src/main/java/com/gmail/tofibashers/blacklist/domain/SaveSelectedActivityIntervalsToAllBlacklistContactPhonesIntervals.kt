package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 19.04.2018.
 */
@Singleton
class SaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervals
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository
) : ISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase {

    override fun build(intervalPosition: Int) : Completable {
        return activityIntervalRepository.getSelectedMultipleActivityIntervalsLists()
                .switchIfEmpty(Single.error(RuntimeException("ActivityIntervals was not pre-selected for contact phone:" + intervalPosition)))
                .map { it.toMutableList() }
                .flatMapCompletable { allPhonesIntervals: MutableList<List<ActivityInterval>> ->
                    activityIntervalRepository.getSelectedActivityIntervals()
                            .switchIfEmpty(Single.error(RuntimeException("ActivityIntervals for saving was not selected")))
                            .flatMapCompletable { modifiedIntervals: List<ActivityInterval> ->
                                allPhonesIntervals.add(intervalPosition, modifiedIntervals)
                                return@flatMapCompletable activityIntervalRepository.putSelectedMultipleActivityIntervalsLists(allPhonesIntervals)
                            }

                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}