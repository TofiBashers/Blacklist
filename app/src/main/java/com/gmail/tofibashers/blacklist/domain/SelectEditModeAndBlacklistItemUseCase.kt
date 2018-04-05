package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import com.gmail.tofibashers.blacklist.entity.OutdatedDataException
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.01.2018.
 */
@Singleton
class SelectEditModeAndBlacklistItemUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val activityIntervalsRepository: IActivityIntervalRepository
) : ISelectEditModeAndBlacklistItemUseCase {

    override fun build(blacklistItem: BlacklistItem): Completable {
        return activityIntervalsRepository.getActivityIntervalsAssociatedWithBlacklistItem(blacklistItem)
                .switchIfEmpty(Single.error(OutdatedDataException()))
                .flatMapCompletable { intervals: List<ActivityInterval> ->
                    interactionModeRepository.putSelectedMode(InteractionMode.EDIT)
                            .andThen(blacklistElementRepository.putSelectedBlackListItem(blacklistItem))
                            .andThen(activityIntervalsRepository.putSelectedActivityIntervals(intervals))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}