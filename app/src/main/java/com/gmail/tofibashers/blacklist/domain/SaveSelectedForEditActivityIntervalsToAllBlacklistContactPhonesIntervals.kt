package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactPhoneWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
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
class SaveSelectedForEditActivityIntervalsToAllBlacklistContactPhonesIntervals
@Inject
constructor(
        private val blacklistContactPhonesWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository,
        private val activityIntervalRepository: IActivityIntervalRepository
) : ISaveSelectedForEditActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase {

    override fun build(phonePosition: Int) : Completable {
        return blacklistContactPhonesWithActivityIntervalsRepository.getSelectedList()
                .switchIfEmpty(Single.error(RuntimeException("ActivityIntervals was not pre-selected for contact phone:" + phonePosition)))
                .map { it.toMutableList() }
                .flatMapCompletable { allPhonesWithIntervals: MutableList<BlacklistContactPhoneWithActivityIntervals> ->
                    activityIntervalRepository.getSelectedActivityIntervals()
                            .switchIfEmpty(Single.error(RuntimeException("ActivityIntervals for saving was not selected")))
                            .flatMapCompletable { modifiedIntervals: List<ActivityInterval> ->
                                allPhonesWithIntervals[phonePosition].activityIntervals = modifiedIntervals
                                return@flatMapCompletable blacklistContactPhonesWithActivityIntervalsRepository.putSelectedList(allPhonesWithIntervals)
                            }

                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}