package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemWithPhonesAndActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Flowable
import io.reactivex.Observable
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
constructor(private val blacklistItemWithActivityIntervalsRepository: IBlacklistItemWithActivityIntervalsRepository,
            private val blacklistContactItemWithPhonesAndIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository,
            private val preferencesData: IPreferencesData,
            private val activityTimeIntervalWithIgnoreSettingsFactory: ActivityTimeIntervalWithIgnoreSettingsFactory)
    : IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase {


    override fun build(): Observable<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>> {
        return Flowable.combineLatest(
                getAllBlacklistNumbersWithTimeAndIgnoreSettingsWithChanges(),
                preferencesData.getIgnoreHiddenNumbersWithChanges(),
                BiFunction{ numbersMap: HashMap<String, TimeAndIgnoreSettingsByWeekdayId>, ignoreHidden: Boolean ->
                    Pair(ignoreHidden, numbersMap)
                })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }

    private fun getAllBlacklistNumbersWithTimeAndIgnoreSettingsWithChanges() : Flowable<HashMap<String, TimeAndIgnoreSettingsByWeekdayId>> {
        return Flowable.combineLatest(
                blacklistItemWithActivityIntervalsRepository.getAllWithChanges(),
                blacklistContactItemWithPhonesAndIntervalsRepository.getAllWithChanges(),
                BiFunction { phoneNumbers: List<BlacklistItemWithActivityIntervals>,
                             contacts: List<BlacklistContactItemWithPhonesAndIntervals> ->
                    Pair(phoneNumbers, contacts)
                })
                .flatMap { toNumbersWithTimeAndIgnoreSettings(it.first, it.second)}
    }

    private fun toNumbersWithTimeAndIgnoreSettings(numbersWithIntervals: List<BlacklistItemWithActivityIntervals>,
                                                   contactsWithPhonesAndIntervals: List<BlacklistContactItemWithPhonesAndIntervals>)
            : Flowable<HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>{
        return Flowable.concat(
                Flowable.fromIterable(numbersWithIntervals)
                        .concatMap { item: BlacklistItemWithActivityIntervals ->
                            toPairNumberWithMapTimeByWeekday(item)
                        },
                Flowable.fromIterable(contactsWithPhonesAndIntervals)
                        .concatMap { Flowable.fromIterable(it.phones) }
                        .concatMap { toPairNumberWithMapTimeByWeekday(it) })
                .toMap({ numberWithTimePair: Pair<String, TimeAndIgnoreSettingsByWeekdayId> ->
                    numberWithTimePair.first },
                        { numberWithTimePair: Pair<String, TimeAndIgnoreSettingsByWeekdayId> ->
                            numberWithTimePair.second })
                .map { resultInMap: Map<String, TimeAndIgnoreSettingsByWeekdayId> ->
                    HashMap(resultInMap)
                }
                .toFlowable()
    }

    private fun toPairNumberWithMapTimeByWeekday(item: BaseBlacklistPhoneWithActivityIntervals)
            : Flowable<Pair<String, TimeAndIgnoreSettingsByWeekdayId>> {
        return Flowable.fromIterable(item.activityIntervals)
                .toMap( { interval: ActivityInterval -> interval.weekDayId },
                        { interval: ActivityInterval ->
                            activityTimeIntervalWithIgnoreSettingsFactory.create(
                                    item.isCallsBlocked,
                                    item.isSmsBlocked,
                                    interval.beginTime,
                                    interval.endTime)
                        })
                .map { timeByDayMap: Map<Int, ActivityTimeIntervalWithIgnoreSettings> ->
                    Pair(item.number, TimeAndIgnoreSettingsByWeekdayId(timeByDayMap)) // because TimeAndIgnoreSettingsByWeekdayId is a strict HashMap alias
                }
                .toFlowable()
    }
}