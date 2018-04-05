package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettingsFactory
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
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
            private val preferencesData: IPreferencesData,
            private val activityTimeIntervalWithIgnoreSettingsFactory: ActivityTimeIntervalWithIgnoreSettingsFactory)
    : IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase {


    override fun build(): Observable<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>> {
        return Flowable.combineLatest(
                blacklistItemWithActivityIntervalsRepository.getAllWithChanges()
                        .concatMap { toNumberWithTimeAndIgnoreSettings(it) },
                preferencesData.getIgnoreHiddenNumbersWithChanges(),
                BiFunction{ numbersMap: HashMap<String, TimeAndIgnoreSettingsByWeekdayId>, ignoreHidden: Boolean ->
                    Pair(ignoreHidden, numbersMap)
                })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }

    private fun toNumberWithTimeAndIgnoreSettings(itemsWithIntervals: List<BlacklistItemWithActivityIntervals>)
            : Flowable<HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>{
        return Flowable.fromIterable(itemsWithIntervals)
                .concatMap { item: BlacklistItemWithActivityIntervals ->
                    toPairNumberWithMapTimeByWeekday(item)
                }
                .toMap({ numberWithTimePair: Pair<String, TimeAndIgnoreSettingsByWeekdayId> ->
                    numberWithTimePair.first },
                        { numberWithTimePair: Pair<String, TimeAndIgnoreSettingsByWeekdayId> ->
                            numberWithTimePair.second })
                .map { resultInMap: Map<String, TimeAndIgnoreSettingsByWeekdayId> ->
                    HashMap(resultInMap)
                }
                .toFlowable()
    }

    private fun toPairNumberWithMapTimeByWeekday(item: BlacklistItemWithActivityIntervals)
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