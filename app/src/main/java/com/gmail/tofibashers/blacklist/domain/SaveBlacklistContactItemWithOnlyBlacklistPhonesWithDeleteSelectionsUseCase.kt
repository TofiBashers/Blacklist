package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.observables.GroupedObservable
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
        private val blacklistContactPhoneRepository: IBlacklistContactPhoneRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val deleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase,
        private val validateBaseBlacklistPhoneForSaveSyncUseCase: IValidateBaseBlacklistPhoneForSaveSyncUseCase,
        private val blacklistContactItemMapper: BlacklistContactItemMapper,
        private val blacklistContactPhoneMapper: BlacklistContactPhoneMapper,
        private val blacklistContactPhoneWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper
) : ISaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase {

    override fun build(contactItem: BlacklistContactItem, contactPhones: List<BlacklistContactPhoneNumberItem>) : Completable {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("Mode not selected when saving")))
                .flatMap {mode: InteractionMode ->
                    return@flatMap if(mode == InteractionMode.EDIT) {
                        validateBlacklistContactExists(contactItem)
                                .andThen(getSelectedPhonesWithIntervalsWithUpdPhonesIgnoreSettings(contactPhones))
                                .flatMap { filterOnlyBlacklistPhonesWithRemoveWhitelist(it, contactItem) }
                    }
                    else {
                        getSelectedPhonesWithIntervalsWithUpdPhonesIgnoreSettings(contactPhones)
                                .flatMap { filterOnlyBlacklistPhones(it) }
                    }
                }
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
                                            RuntimeException("Multiple phones with intervals not selected for saving phoneNumber"))
                            )
                            .flattenAsObservable { it },
                    BiFunction { srcPhone: BlacklistContactPhoneNumberItem,
                                 selectedPhoneWithIntervals: BlacklistContactPhoneWithActivityIntervals ->
                        blacklistContactPhoneMapper.toBlacklistContactPhoneWithIntervals(srcPhone, selectedPhoneWithIntervals.activityIntervals)
                    })
                    .toList()
    }

    private fun filterOnlyBlacklistPhonesWithRemoveWhitelist(phones: List<BlacklistContactPhoneWithActivityIntervals>,
                                                             contactItem: BlacklistContactItem)
            : Single<List<BlacklistContactPhoneWithActivityIntervals>>{
        return Observable.fromIterable(phones)
                .flatMapSingle { phoneWithIntervals: BlacklistContactPhoneWithActivityIntervals ->
                    validateBaseBlacklistPhoneForSaveSyncUseCase.build(phoneWithIntervals)
                        .map { Pair(it, phoneWithIntervals) }
                }
                .groupBy(
                        { pairIsValidWithPhone: Pair<Boolean, BlacklistContactPhoneWithActivityIntervals> ->
                            pairIsValidWithPhone.first },
                        { pairIsValidWithPhone: Pair<Boolean, BlacklistContactPhoneWithActivityIntervals> ->
                            pairIsValidWithPhone.second }
                )
                .flatMap { grouped: GroupedObservable<Boolean, BlacklistContactPhoneWithActivityIntervals> ->
                    return@flatMap if(grouped.key!!) { grouped.map { it } }
                    else {
                        grouped.toList()
                                .map {
                                    blacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneList(it)
                                }
                                .flatMapCompletable { blacklistPhones: List<BlacklistContactPhoneNumberItem> ->
                                    blacklistContactPhoneRepository.removeAssociatedWithBlacklistContact(
                                            items = *blacklistPhones.toTypedArray(),
                                            blacklistContact = contactItem)
                                }
                                .toObservable()
                    }
                }
                .toList()
    }

    private fun filterOnlyBlacklistPhones(phones: List<BlacklistContactPhoneWithActivityIntervals>)
            : Single<List<BlacklistContactPhoneWithActivityIntervals>>{
        return Observable.fromIterable(phones)
                .filter {
                    validateBaseBlacklistPhoneForSaveSyncUseCase.build(it)
                            .blockingGet()
                }
                .toList()
    }
}