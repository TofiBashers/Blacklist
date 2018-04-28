package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 19.04.2018.
 */
@Singleton
class SelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val createDefaultActivityIntervalsUseCase: ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase
) : ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase {

    override fun build(intervalPosition: Int, totalIntervalsCount: Int) : Completable {
        return activityIntervalRepository.getSelectedMultipleActivityIntervalsLists()
                .switchIfEmpty(
                        interactionModeRepository.getSelectedMode()
                        .switchIfEmpty(Single.error(RuntimeException("Mode not selected when trying to edit intervals")))
                        .flatMap {mode: InteractionMode ->
                            return@flatMap if(mode == InteractionMode.CREATE) createDefaultActivityIntervals(totalIntervalsCount)
                            else Single.error<List<List<ActivityInterval>>>(RuntimeException("Multiple intervals not selected, when edit intervals in Edit mode"))
                        })
                .map { it[intervalPosition]}
                .flatMapCompletable { activityIntervalRepository.putSelectedActivityIntervals(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun createDefaultActivityIntervals(count: Int) : Single<List<List<ActivityInterval>>> {
        return Observable.range(1, count)
                .concatMap { createDefaultActivityIntervalsUseCase.build().toObservable() }
                .toList()
    }
}