package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemWithNonIgnoredNumbersFlagMapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Singleton
class SelectEditModeAndContactItemUseCase
@Inject
constructor(
        private val blacklistContactRepository: IBlacklistContactItemRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val activityIntervalsRepository: IActivityIntervalRepository,
        private val blacklistContactItemWithNonIgnoredNumbersFlagMapper: BlacklistContactItemWithNonIgnoredNumbersFlagMapper
) : ISelectEditModeAndContactItemUseCase{

    override fun build(item: BlacklistContactItemWithNonIgnoredNumbersFlag): Completable {
        return Single.fromCallable { blacklistContactItemWithNonIgnoredNumbersFlagMapper.toBlacklistContactItem(item) }
                .flatMapCompletable { contactItem: BlacklistContactItem ->
                    activityIntervalsRepository.getActivityIntervalsAssociatedWithBlacklistContactItem(contactItem)
                            .flatMapCompletable { intervals: List<ActivityInterval> ->
                                interactionModeRepository.putSelectedMode(InteractionMode.EDIT)
                                        .andThen(blacklistContactRepository.putSelected(contactItem))
                                        .andThen(activityIntervalsRepository.putSelectedActivityIntervals(intervals))
                            }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}