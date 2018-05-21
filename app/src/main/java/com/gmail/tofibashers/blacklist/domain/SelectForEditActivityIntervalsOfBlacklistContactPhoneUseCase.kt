package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
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
class SelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository,
        private val blacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository
) : ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase {

    override fun build(phonePosition: Int) : Completable {
        return blacklistContactPhoneWithActivityIntervalsRepository.getSelectedList()
                .switchIfEmpty(Single.error(RuntimeException("Multiple phones with intervals not selected, when trying to edit")))
                .map { it[phonePosition]}
                .flatMapCompletable { activityIntervalRepository.putSelectedActivityIntervals(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}