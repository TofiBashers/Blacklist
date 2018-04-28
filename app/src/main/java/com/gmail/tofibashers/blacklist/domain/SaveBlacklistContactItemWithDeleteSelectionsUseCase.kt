package com.gmail.tofibashers.blacklist.domain

import android.util.Log
import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneItemMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Singleton
class SaveBlacklistContactItemWithDeleteSelectionsUseCase
@Inject
constructor(
        private val activityIntervalRepository: IActivityIntervalRepository,
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val blacklistContactWithPhonesAndActivityIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val deleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase,
        private val createDefaultActivityIntervalsUseCase: ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase,
        private val blacklistContactItemMapper: BlacklistContactItemMapper,
        private val blacklistContactPhoneMapper: BlacklistContactPhoneMapper
) : ISaveBlacklistContactItemWithDeleteSelectionsUseCase{

    override fun build(contactItem: BlacklistContactItem, contactPhones: List<BlacklistContactPhoneNumberItem>) : Completable {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("Mode not selected when saving")))
                .flatMap {mode: InteractionMode ->
                    return@flatMap if(mode == InteractionMode.CREATE) {
                        activityIntervalRepository.getSelectedMultipleActivityIntervalsLists()
                                .switchIfEmpty(createDefaultActivityIntervalsForAllPhones(contactPhones))
                    }
                    else {
                        validateBlacklistContactExists(contactItem)
                                .andThen(activityIntervalRepository.getSelectedMultipleActivityIntervalsLists())
                                .switchIfEmpty(
                                        Single.error<List<List<ActivityInterval>>>(
                                                RuntimeException("Multiple intervals not selected for saving contact in Edit mode"))
                                )
                    }
                }
                .map { blacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(contactItem, contactPhones, it, blacklistContactPhoneMapper) }
                .flatMapCompletable { blacklistContactWithPhonesAndActivityIntervalsRepository.put(it) }
                .andThen(deleteAllSelectionsSyncUseCase.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun createDefaultActivityIntervalsForAllPhones(contactPhones: List<BlacklistContactPhoneNumberItem>) : Single<List<List<ActivityInterval>>> {
        return Observable.fromIterable(contactPhones)
                .concatMap { createDefaultActivityIntervalsUseCase.build().toObservable() }
                .toList()
    }

    private fun validateBlacklistContactExists(contactItem: BlacklistContactItem): Completable {
        return blacklistContactItemRepository.getByDeviceIdAndDeviceKey(contactItem.deviceDbId, contactItem.deviceKey)
                .switchIfEmpty(Single.error(OutdatedDataException()))
                .toCompletable()
    }
}