package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 05.02.2018.
 */
@Singleton
class DeleteAllSelectionsSyncUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository,
        private val activityIntervalRepository: IActivityIntervalRepository,
        private val interactionModeRepository: IInteractionModeRepository
): IDeleteAllSelectionsSyncUseCase {

    override fun build(): Completable {
        return blacklistElementRepository.removeSelectedBlackListItem()
                .andThen(activityIntervalRepository.removeSelectedActivityIntervals())
                .andThen(interactionModeRepository.removeSelectedMode())
    }
}