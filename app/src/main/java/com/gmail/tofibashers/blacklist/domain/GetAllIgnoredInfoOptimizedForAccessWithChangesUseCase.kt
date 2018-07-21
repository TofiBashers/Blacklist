package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemWithPhonesAndActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistPhoneNumberItemWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IDeviceData
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.utils.PhoneNumberFormatUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.02.2018.
 */
@Singleton
class GetAllIgnoredInfoOptimizedForAccessWithChangesUseCase
@Inject
constructor(private val blacklistPhoneNumberItemWithActivityIntervalsRepository: IBlacklistPhoneNumberItemWithActivityIntervalsRepository,
            private val blacklistContactItemWithPhonesAndIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository,
            private val preferencesData: IPreferencesData,
            private val deviceData: IDeviceData,
            private val phoneNumberFormatUtils: PhoneNumberFormatUtils,
            private val activityTimeIntervalWithIgnoreSettingsFactory: ActivityTimeIntervalWithIgnoreSettingsFactory)
    : IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase {


    override fun build(): Observable<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>> {
        return Flowable.combineLatest(
                getAllBlacklistNumbersWithTimeAndIgnoreSettingsWithChanges(),
                preferencesData.getIgnoreHiddenNumbersWithChanges(),
                BiFunction{ defaultCodeWithNumbersMap: Pair<String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>,
                            ignoreHidden: Boolean ->
                    Triple(ignoreHidden, defaultCodeWithNumbersMap.first, defaultCodeWithNumbersMap.second)
                })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }

    private fun getAllBlacklistNumbersWithTimeAndIgnoreSettingsWithChanges()
            : Flowable<Pair<String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>> {
        return Flowable.combineLatest(
                blacklistPhoneNumberItemWithActivityIntervalsRepository.getAllWithChanges(),
                blacklistContactItemWithPhonesAndIntervalsRepository.getAllWithChanges(),
                BiFunction { phoneNumbers: List<BlacklistPhoneNumberItemWithActivityIntervals>,
                             contacts: List<BlacklistContactItemWithPhonesAndIntervals> ->
                    Pair(phoneNumbers, contacts)
                })
                .switchMap { lists: Pair<List<BlacklistPhoneNumberItemWithActivityIntervals>, List<BlacklistContactItemWithPhonesAndIntervals>> ->
                    deviceData.getCelluarNetworkOrLocalCountryCodeWithChanges()
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(Schedulers.io())
                            .flatMap { defaultCountryCode: String ->
                                toTypedNumbersWithTimeAndIgnoreSettings(lists.first, lists.second, defaultCountryCode)
                                        .map { Pair(defaultCountryCode, it) }
                            }
                }
    }

    private fun toTypedNumbersWithTimeAndIgnoreSettings(numbersWithIntervals: List<BlacklistPhoneNumberItemWithActivityIntervals>,
                                                        contactsWithPhonesAndIntervals: List<BlacklistContactItemWithPhonesAndIntervals>,
                                                        defaultCountryCode: String)
            : Flowable<HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>{
        return Flowable.concat(
                Flowable.fromIterable(numbersWithIntervals)
                        .concatMap { item: BlacklistPhoneNumberItemWithActivityIntervals ->
                            toPairTypedNumberWithMapTimeByWeekday(item, defaultCountryCode)
                        },
                Flowable.fromIterable(contactsWithPhonesAndIntervals)
                        .concatMap { Flowable.fromIterable(it.phones) }
                        .concatMap { toPairTypedNumberWithMapTimeByWeekday(it, defaultCountryCode) })
                .toMap({ numberWithTimePair: Pair<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId> ->
                    numberWithTimePair.first },
                        { numberWithTimePair: Pair<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId> ->
                            numberWithTimePair.second })
                .map { resultInMap: Map<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId> ->
                    HashMap(resultInMap)
                }
                .toFlowable()
    }

    private fun toPairTypedNumberWithMapTimeByWeekday(item: BaseBlacklistPhoneWithActivityIntervals,
                                                      defaultCountryCode: String)
            : Flowable<Pair<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>> {
        return Single.zip(
                Flowable.fromIterable(item.activityIntervals)
                        .toMap( { interval: ActivityInterval -> interval.weekDayId },
                                { interval: ActivityInterval ->
                                    activityTimeIntervalWithIgnoreSettingsFactory.create(
                                            item.isCallsBlocked,
                                            item.isSmsBlocked,
                                            interval.beginTime,
                                            interval.endTime)
                                }),
                Single.fromCallable {
                    phoneNumberFormatUtils.toPhoneNumberTypeWithValue(item.number, defaultCountryCode)
                },
                BiFunction { timeByDayMap: Map<Int, ActivityTimeIntervalWithIgnoreSettings>,
                             numberWithType: PhoneNumberTypeWithValue ->
                    Pair(numberWithType, TimeAndIgnoreSettingsByWeekdayId(timeByDayMap)) // because TimeAndIgnoreSettingsByWeekdayId is a strict HashMap alias
                }
        )
                .toFlowable()
    }
}