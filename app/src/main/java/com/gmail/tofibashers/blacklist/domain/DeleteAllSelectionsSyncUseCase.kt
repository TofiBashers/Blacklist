package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
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
        private val interactionModeRepository: IInteractionModeRepository,
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val whitelistContactItemRepository: IWhitelistContactItemRepository
): IDeleteAllSelectionsSyncUseCase {

    override fun build(): Completable {
        return blacklistElementRepository.removeSelectedBlacklistPhoneNumberItem()
                .andThen(activityIntervalRepository.removeSelectedActivityIntervals())
                .andThen(interactionModeRepository.removeSelectedMode())
                .andThen(whitelistContactItemRepository.removeSelected())
                .andThen(blacklistContactItemRepository.removeSelected())
    }
}