package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import io.reactivex.Observable
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
        private val interactionModeRepository: IInteractionModeRepository,
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val blacklistContactPhoneRepository: IBlacklistContactPhoneRepository,
        private val whitelistContactItemRepository: IWhitelistContactItemRepository,
        private val whitelistContactPhoneRepository: IWhitelistContactPhoneRepository,
        private val blacklistContactItemMapper: BlacklistContactItemMapper,
        private val whitelistContactItemMapper: WhitelistContactItemMapper,
        private val blacklistContactPhoneItemFactory: BlacklistContactPhoneNumberItemFactory,
        private val interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory: InteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory
): IGetInteractionModeWithSelectedBlacklistContactItemUseCase {

    override fun build(): Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("InteractionMode not selected!")))
                .flatMap { mode: InteractionMode ->
                    return@flatMap if(mode == InteractionMode.CREATE) {
                        getInteractionModeWithSettingsWhenCreateModeSelected()
                    }
                    else {
                        getInteractionModeWithSettingsWhenEditModeSelected()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getInteractionModeWithSettingsWhenCreateModeSelected() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return whitelistContactItemRepository.getSelected()
                .switchIfEmpty(Single.error(RuntimeException("Contact not selected with InteractionMode.CREATE")))
                .flatMap { whitelistContactItem: WhitelistContactItem ->
                    Single.zip(
                            Single.fromCallable { whitelistContactItemMapper.toBlacklistContact(whitelistContactItem) },
                            whitelistContactPhoneRepository.getAllAssociatedWithContactSortedByNumberAsc(whitelistContactItem)
                                    .map { contactPhonesToBlacklistContactPhonesWithDefaultSettings(it) },
                            BiFunction { blacklistContactItem: BlacklistContactItem, blacklistPhoneNumbers: List<BlacklistContactPhoneNumberItem> ->
                                return@BiFunction interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory.create(InteractionMode.CREATE,
                                        blacklistContactItem,
                                        blacklistPhoneNumbers,
                                        false)
                            })
                }
    }

    private fun getInteractionModeWithSettingsWhenEditModeSelected() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState> {
        return blacklistContactItemRepository.getSelected()
                .switchIfEmpty(Single.error(RuntimeException("BlacklistContact not selected with InteractionMode.EDIT")))
                .flatMap { blacklistContactItem: BlacklistContactItem ->
                    getAllPhonesSortedByNumberAsc(blacklistContactItem)
                            .map { blacklistPhoneNumbers ->
                                interactionModeWithBlacklistContactItemAndNumbersAndValidStateFactory.create(InteractionMode.EDIT,
                                    blacklistContactItem,
                                    blacklistPhoneNumbers,
                                    true) }
                }
    }

    private fun getAllPhonesSortedByNumberAsc(blacklistContactItem: BlacklistContactItem) : Single<List<BlacklistContactPhoneNumberItem>> {
        return Single.fromCallable { blacklistContactItemMapper.toWhitelistContactItem(blacklistContactItem) }
                .flatMapObservable { sourceContact: WhitelistContactItem ->
                    Observable.concat(
                            whitelistContactPhoneRepository.getAllAssociatedWithWhitelistContact(sourceContact)
                                    .map { contactPhonesToBlacklistContactPhonesWithDefaultSettings(it) }
                                    .flattenAsObservable { it },
                            blacklistContactPhoneRepository.getAllAssociatedWithBlacklistContact(blacklistContactItem)
                                    .flattenAsObservable { it })
                }
                .toList()
                .map { phones: List<BlacklistContactPhoneNumberItem> ->
                    phones.sortedBy { phone: BlacklistContactPhoneNumberItem -> phone.number }
                }
    }

    private fun contactPhonesToBlacklistContactPhonesWithDefaultSettings(whitelistContactPhones: List<WhitelistContactPhone>) : List<BlacklistContactPhoneNumberItem> {
        return whitelistContactPhones
                .map { blacklistContactPhoneItemFactory.create(null,
                            it.deviceDbId,
                            it.number,
                            false,
                            false)
                }
    }
}