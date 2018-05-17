package com.gmail.tofibashers.blacklist.domain

import android.util.Log
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactPhoneWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IWhitelistContactPhoneRepository
import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase
@Inject
constructor(
        private val createDefaultActivityIntervalsUseCase: ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase,
        private val whitelistContactPhoneRepository: IWhitelistContactPhoneRepository,
        private val blacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository,
        private val blacklistContactPhoneWithActivityIntervalsFactory: BlacklistContactPhoneWithActivityIntervalsFactory
) : ISelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase {

    override fun build(mode: InteractionMode): Single<List<BlacklistContactPhoneWithActivityIntervals>> {
        return Observable.concat(
                whitelistContactPhoneRepository.getSelectedList()
                        .switchIfEmpty(Single.error(RuntimeException("Not selected whitelist phones")))
                        .flatMap { contactPhonesToBlacklistContactPhonesWithDefaultSettingsAndIntervals(it) }
                        .flattenAsObservable { it },
                Observable.defer {
                    return@defer if (mode == InteractionMode.EDIT) {
                        blacklistContactPhoneWithActivityIntervalsRepository.getSelectedList()
                                .switchIfEmpty(
                                        Single.error(RuntimeException("Not selected blacklist phones with intervals on Edit mode")))
                                .flattenAsObservable { it }
                    } else Observable.empty()
                })
                .toList()
                .map { phonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals> ->
                    phonesWithIntervals.sortedBy { it.number }
                }
                .flatMap {
                    blacklistContactPhoneWithActivityIntervalsRepository.putSelectedList(it)
                            .andThen(Single.just(it))
                }
    }

    private fun contactPhonesToBlacklistContactPhonesWithDefaultSettingsAndIntervals(whitelistContactPhones: List<WhitelistContactPhone>)
            : Single<List<BlacklistContactPhoneWithActivityIntervals>> {
        return Observable.fromIterable(whitelistContactPhones)
                .concatMap { whitelistContactPhone: WhitelistContactPhone ->
                    createDefaultActivityIntervalsUseCase.build()
                            .map { defaultIntervals: List<ActivityInterval> ->
                                blacklistContactPhoneWithActivityIntervalsFactory.create(null,
                                        whitelistContactPhone.deviceDbId,
                                        whitelistContactPhone.number,
                                        false,
                                        false,
                                        defaultIntervals)
                            }
                            .toObservable()
                }
                .toList()
    }
}