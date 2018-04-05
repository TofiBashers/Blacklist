package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 28.01.2018.
 */

@Singleton
class CheckNumberMustBeIgnoredNowSyncUseCase
@Inject
constructor(private val timeFormatUtils: TimeFormatUtils): ICheckNumberMustBeIgnoredNowSyncUseCase {

    override fun build(number: String?,
                       isSms: Boolean,
                       ignoredSettingsByNumbers: HashMap<String, TimeAndIgnoreSettingsByWeekdayId>,
                       ignoreHiddenNumbers: Boolean): Single<Boolean> {
        return Single.defer<Boolean> {
            if(number != null && ignoredSettingsByNumbers.isEmpty()) {
                Single.just(false)
            }
            else if(number.isNullOrEmpty()) {
                Single.just(ignoreHiddenNumbers)
            }
            else {
                Maybe.fromCallable { timeFormatUtils.currentWeekdayAsActivityIntervalFormat() }
                        .flatMap { currentDay: Int ->
                            val timeAndSettings = ignoredSettingsByNumbers[number]?.get(currentDay)
                            if (timeAndSettings == null
                                    || (isSms && !timeAndSettings.isSmsBlocked)
                                    || (!isSms && !timeAndSettings.isCallsBlocked)) {
                                return@flatMap Maybe.empty<ActivityTimeIntervalWithIgnoreSettings>()
                            }
                            else {
                                return@flatMap Maybe.just(timeAndSettings)
                            }
                        }
                        .map { settings: ActivityTimeIntervalWithIgnoreSettings ->
                            timeFormatUtils.isCurrentTimeInInterval(settings.beginTime, settings.endTime)
                        }
                        .toSingle(false)
            }
        }
    }
}