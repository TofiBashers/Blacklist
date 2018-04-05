package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IDeviceData
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
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
        private val preferencesData: IPreferencesData,
        private val deviceData: IDeviceData,
        private val systemVerWarningFactory: GetBlacklistResult_SystemVerWarningFactory,
        private val listWithIgnoreResultFactory: GetBlacklistResult_ListWithIgnoreResultFactory
) : IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase {

    override fun build(): Observable<GetBlacklistResult> {
        return Observable.concat(getSystemVerWarningIfNeed().toObservable(), getItems())
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

    private fun getItems(): Observable<GetBlacklistResult.ListWithIgnoreResult> {
        return Flowable.combineLatest(
                blacklistElementRepository.getAllWithChanges()
                        .map{list -> list.sortedBy { elem -> elem.number }},
                preferencesData.getIgnoreHiddenNumbersWithChanges(),
                BiFunction { sortedItems: List<BlacklistItem>, ignoreHiddenNumbers: Boolean ->
                    listWithIgnoreResultFactory.create(sortedItems, ignoreHiddenNumbers) })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }
}