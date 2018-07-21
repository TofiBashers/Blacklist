package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 18.04.2018.
 */
@Singleton
class GetInteractionModeWithSelectedBlacklistContactItemUseCase
@Inject
constructor(
        private val selectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase: ISelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase,
        private val interactionModeRepository: IInteractionModeRepository,
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val whitelistContactItemRepository: IWhitelistContactItemRepository,
        private val blacklistContactPhoneWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper,
        private val whitelistContactItemMapper: WhitelistContactItemMapper,
        private val interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory: InteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory
): IGetInteractionModeWithSelectedBlacklistContactItemUseCase {

    override fun build(): Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("InteractionMode not selected!")))
                .flatMap { mode: InteractionMode ->
                    return@flatMap if(mode == InteractionMode.CREATE) {
                        getInteractionModeWithSettingsAndSaveChangesWhenCreateModeSelected()
                    }
                    else {
                        getInteractionModeWithSettingsAndSaveChangesWhenEditModeSelected()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getInteractionModeWithSettingsAndSaveChangesWhenCreateModeSelected() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return whitelistContactItemRepository.getSelected()
                .switchIfEmpty(Single.error(RuntimeException("Contact not selected with InteractionMode.CREATE")))
                .flatMap { whitelistContactItem: WhitelistContactItem ->
                    selectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase.build(InteractionMode.CREATE)
                            .flatMap {
                                selectUpdIntervalsAndGetCreateInteractionMode(whitelistContactItem, it)
                            }
                }
    }

    private fun getInteractionModeWithSettingsAndSaveChangesWhenEditModeSelected() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return blacklistContactItemRepository.getSelected()
                .switchIfEmpty(Single.error(RuntimeException("BlacklistContact not selected with InteractionMode.EDIT")))
                .flatMap { blacklistContactItem: BlacklistContactItem ->
                    selectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase.build(InteractionMode.EDIT)
                            .flatMap { selectUpdIntervalsAndGetEditInteractionMode(blacklistContactItem, it)}
                }
    }

    private fun selectUpdIntervalsAndGetEditInteractionMode(blacklistContactItem: BlacklistContactItem,
                                                            phonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>)
            : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return Single.fromCallable {
            blacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneList(phonesWithIntervals)
        }
        .map { interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory.create(InteractionMode.EDIT,
                blacklistContactItem,
                it,
                true) }
    }

    private fun selectUpdIntervalsAndGetCreateInteractionMode(whitelistContactItem: WhitelistContactItem,
                                                              phonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>)
            : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return Single.zip(
                Single.fromCallable {
                    whitelistContactItemMapper.toBlacklistContact(whitelistContactItem)
                },
                Single.fromCallable {
                    blacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneList(phonesWithIntervals)
                },
                BiFunction { blacklistContactItem: BlacklistContactItem, blacklistPhoneNumbers: List<BlacklistContactPhoneNumberItem> ->
                    return@BiFunction interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory.create(InteractionMode.CREATE,
                            blacklistContactItem,
                            blacklistPhoneNumbers,
                            false)
                })
    }

}