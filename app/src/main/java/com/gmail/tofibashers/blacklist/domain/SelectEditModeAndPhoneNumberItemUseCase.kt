package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistPhoneNumberItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.01.2018.
 */
@Singleton
class SelectEditModeAndPhoneNumberItemUseCase
@Inject
constructor(
        private val blacklistPhoneNumberItemRepository: IBlacklistPhoneNumberItemRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val activityIntervalsRepository: IActivityIntervalRepository
) : ISelectEditModeAndPhoneNumberItemUseCase {

    override fun build(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return activityIntervalsRepository.getActivityIntervalsAssociatedWithBlacklistPhoneNumberItem(blacklistPhoneNumberItem)
                .flatMapCompletable { intervals: List<ActivityInterval> ->
                    interactionModeRepository.putSelectedMode(InteractionMode.EDIT)
                            .andThen(blacklistPhoneNumberItemRepository.putSelectedBlacklistPhoneNumberItem(blacklistPhoneNumberItem))
                            .andThen(activityIntervalsRepository.putSelectedActivityIntervals(intervals))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}