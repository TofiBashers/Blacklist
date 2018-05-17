package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Singleton
class SaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase
@Inject
constructor(
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val blacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository,
        private val blacklistContactWithPhonesAndActivityIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val deleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase,
        private val validateBaseBlacklistPhoneForSaveSyncUseCase: IValidateBaseBlacklistPhoneForSaveSyncUseCase,
        private val blacklistContactItemMapper: BlacklistContactItemMapper,
        private val blacklistContactPhoneMapper: BlacklistContactPhoneMapper
) : ISaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase {

    override fun build(contactItem: BlacklistContactItem, contactPhones: List<BlacklistContactPhoneNumberItem>) : Completable {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("Mode not selected when saving")))
                .flatMapCompletable {mode: InteractionMode ->
                    return@flatMapCompletable if(mode == InteractionMode.EDIT) validateBlacklistContactExists(contactItem)
                    else Completable.complete()
                }
                .andThen(getSelectedPhonesWithIntervalsWithUpdPhonesIgnoreSettings(contactPhones))
                .flatMap { filterNotWhitelistPhones(it) }
                .map { blacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(contactItem, it) }
                .flatMapCompletable { blacklistContactWithPhonesAndActivityIntervalsRepository.put(it) }
                .andThen(deleteAllSelectionsSyncUseCase.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    private fun validateBlacklistContactExists(contactItem: BlacklistContactItem): Completable {
        return blacklistContactItemRepository.getByDbId(contactItem.dbId)
                .switchIfEmpty(Single.error(OutdatedDataException()))
                .toCompletable()
    }

    private fun getSelectedPhonesWithIntervalsWithUpdPhonesIgnoreSettings(contactPhones: List<BlacklistContactPhoneNumberItem>) : Single<List<BlacklistContactPhoneWithActivityIntervals>> {
            return Observable.zip(
                    Observable.fromIterable(contactPhones),
                    blacklistContactPhoneWithActivityIntervalsRepository.getSelectedList()
                            .switchIfEmpty(
                                    Single.error<List<BlacklistContactPhoneWithActivityIntervals>>(
                                            RuntimeException("Multiple intervals not selected for saving contact"))
                            )
                            .flattenAsObservable { it },
                    BiFunction { srcPhone: BlacklistContactPhoneNumberItem,
                                 selectedPhoneWithIntervals: BlacklistContactPhoneWithActivityIntervals ->
                        blacklistContactPhoneMapper.toBlacklistContactPhoneWithIntervals(srcPhone, selectedPhoneWithIntervals.activityIntervals)
                    })
                    .toList()
    }

    private fun filterNotWhitelistPhones(phones: List<BlacklistContactPhoneWithActivityIntervals>) : Single<List<BlacklistContactPhoneWithActivityIntervals>>{
        return Observable.fromIterable(phones)
                .filter {
                    validateBaseBlacklistPhoneForSaveSyncUseCase.build(it)
                            .blockingGet()
                }
                .toList()
    }
}