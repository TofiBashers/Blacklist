package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Singleton
class GetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository,
        private val blacklistContactItemRepository: IBlacklistContactItemRepository,
        private val whitelistContactPhoneRepository: IWhitelistContactPhoneRepository,
        private val preferencesData: IPreferencesData,
        private val deviceData: IDeviceData,
        private val blacklistContactItemMapper: BlacklistContactItemMapper,
        private val systemVerWarningFactory: GetBlacklistResult_SystemVerWarningFactory,
        private val listWithIgnoreResultFactory: GetBlacklistResult_ListWithIgnoreResultFactory,
        private val sectionBlacklistItem_HeaderFactory: SectionBlacklistItem_HeaderFactory,
        private val sectionBlacklistItem_ContactFactory: SectionBlacklistItem_ContactFactory,
        private val sectionBlacklistItem_PhoneNumberFactory: SectionBlacklistItem_PhoneNumberFactory
) : IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase {

    override fun build(): Observable<GetBlacklistResult> {
        return Observable.concat(getSystemVerWarningIfNeed().toObservable(), getItemsWithIgnoreResult())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getSystemVerWarningIfNeed() : Maybe<GetBlacklistResult.SystemVerWarning> {
        return deviceData.isKitkatOrHigherSystemVersion()
                .flatMapMaybe { isKitkatOrHigher: Boolean ->
                    if(isKitkatOrHigher){
                        preferencesData.getIsFirstTimeLaunchOnKitkatOrHigher()
                                .flatMapMaybe {
                                   if(it) {
                                        preferencesData.setIsFirstTimeLaunchOnKitkatOrHigherFalse()
                                                .toSingle { systemVerWarningFactory.create(SystemVerWarningType.INCOMPATIBLE_VER) }
                                                .toMaybe()
                                    }
                                    else Maybe.empty<GetBlacklistResult.SystemVerWarning>()
                                }
                    }
                    preferencesData.getIsFirstTimeLaunchBeforeKitkat()
                            .flatMapMaybe {
                                if(it) {
                                    preferencesData.setIsFirstTimeLaunchBeforeKitkatFalse()
                                            .toSingle { systemVerWarningFactory.create(SystemVerWarningType.MAY_UPDATE_TO_INCOMPATIBLE_VER) }
                                            .toMaybe()
                                }
                                else Maybe.empty<GetBlacklistResult.SystemVerWarning>()
                            }
                }
    }

    private fun getItemsWithIgnoreResult(): Observable<GetBlacklistResult.ListWithIgnoreResult> {
        return Flowable.combineLatest(
                getSectionedItemsWithChanges(),
                preferencesData.getIgnoreHiddenNumbersWithChanges(),
                BiFunction { sortedPhoneNumberItems: List<SectionBlacklistItem>, ignoreHiddenNumbers: Boolean ->
                    listWithIgnoreResultFactory.create(sortedPhoneNumberItems, ignoreHiddenNumbers) })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }

    private fun getSectionedItemsWithChanges(): Flowable<List<SectionBlacklistItem>> {
        return Flowable.combineLatest(
                blacklistContactItemRepository.getAllSortedByNameAscWithChanges()
                        .concatMap { toContactItemsWithNonIgnoredFlags(it).toFlowable() },
                blacklistElementRepository.getAllWithChanges()
                        .map{list -> list.sortedBy { elem -> elem.number }},
                BiFunction { sortedContactItems: List<BlacklistContactItemWithNonIgnoredNumbersFlag>,
                             sortedPhoneNumberItems: List<BlacklistPhoneNumberItem> ->
                    toWrappersList(sortedPhoneNumberItems, sortedContactItems) })
    }

    private fun toContactItemsWithNonIgnoredFlags(items: List<BlacklistContactItem>) : Single<List<BlacklistContactItemWithNonIgnoredNumbersFlag>> {
        return Observable.fromIterable(items)
                .concatMap { item: BlacklistContactItem ->
                    Single.fromCallable { blacklistContactItemMapper.toWhitelistContactItem(item) }
                            .flatMap { whitelistContactPhoneRepository.getCountOfAssociatedWithContact(it) }
                            .map { count: Int ->
                                blacklistContactItemMapper.toBlacklistContactItemWithNonIgnoredFlag(item, count>0)
                            }
                            .toObservable()
                }
                .toList()
    }

    private fun hasHeaders(phoneNumberItems: List<BlacklistPhoneNumberItem>, contactItems: List<BlacklistContactItemWithNonIgnoredNumbersFlag>) =
            !phoneNumberItems.isEmpty() && !contactItems.isEmpty()

    private fun toWrappersList(phoneNumberItems: List<BlacklistPhoneNumberItem>,
                               contactItems: List<BlacklistContactItemWithNonIgnoredNumbersFlag>) : List<SectionBlacklistItem>{
        return mutableListOf<SectionBlacklistItem>().apply {
            val hasHeaders = hasHeaders(phoneNumberItems, contactItems)
            if(hasHeaders){
                add(sectionBlacklistItem_HeaderFactory.create(SectionBlacklistItem.Header.Type.CONTACTS))
            }
            addAll(contactItems.map { item: BlacklistContactItemWithNonIgnoredNumbersFlag ->
                sectionBlacklistItem_ContactFactory.create(item)
            })
            if(hasHeaders){
                add(sectionBlacklistItem_HeaderFactory.create(SectionBlacklistItem.Header.Type.NUMBERS))
            }
            addAll(phoneNumberItems.map { item: BlacklistPhoneNumberItem ->
                sectionBlacklistItem_PhoneNumberFactory.create(item)
            })
        }
    }
}